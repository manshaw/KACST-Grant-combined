package cz.covid19cz.erouska.ext

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.getSystemService
import cz.covid19cz.erouska.AppConfig
import cz.covid19cz.erouska.R
import cz.covid19cz.erouska.ui.base.BaseFragment
import cz.covid19cz.erouska.utils.CustomTabHelper
import cz.covid19cz.erouska.utils.L


fun Context.isLocationEnabled(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // This is new method provided in API 28
        getSystemService<LocationManager>()?.isLocationEnabled ?: false
    } else {
        // This is Deprecated in API 28
        val mode = Settings.Secure.getInt(
            this.contentResolver, Settings.Secure.LOCATION_MODE,
            Settings.Secure.LOCATION_MODE_OFF
        )
        mode != Settings.Secure.LOCATION_MODE_OFF
    }
}

fun Context.isBatterySaverEnabled() =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getSystemService<PowerManager>()?.isPowerSaveMode ?: false

fun Context.hasLocationPermission(): Boolean {
    return PermissionChecker.checkSelfPermission(
        this,
        getLocationPermission()
    ) == PermissionChecker.PERMISSION_GRANTED
}

fun Context.openLocationSettings() {
    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
}

fun Context.openPermissionsScreen() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri: Uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun getLocationPermission(): String {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    } else {
        android.Manifest.permission.ACCESS_FINE_LOCATION
    }
}

@Suppress("DEPRECATION")
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    with(connectivityManager) {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            activeNetworkInfo?.isConnected
        } else {
            getNetworkCapabilities(activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false
    }
}

fun Context.withInternet(onlineAction: () -> Unit) {
    if (isNetworkAvailable()) onlineAction() else Toast.makeText(
        this,
        R.string.no_internet,
        Toast.LENGTH_SHORT
    ).show()
}

fun Context.shareApp() {
    val text = getString(R.string.share_app_text, AppConfig.shareAppDynamicLink)
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, text)
    startActivity(Intent.createChooser(intent, getString(R.string.share_app_title)))
}

fun BaseFragment<*, *>.showWeb(url: String, customTabHelper: CustomTabHelper) {
    val intent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        .setCloseButtonIcon(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.ic_action_up
            )
        )
        .build()
    if (customTabHelper.chromePackageName != null) {
        intent.launchUrl(requireContext(), Uri.parse(url))
    } else {
        // Custom Tabs not available
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
            )
        } catch (e: ActivityNotFoundException) {
            L.e(e)
        }
    }
}
