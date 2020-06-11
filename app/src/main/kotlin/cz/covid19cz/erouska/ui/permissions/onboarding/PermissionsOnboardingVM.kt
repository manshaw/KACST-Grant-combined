package cz.covid19cz.erouska.ui.permissions.onboarding

import android.app.Application
import android.bluetooth.BluetoothManager
import cz.covid19cz.erouska.R
import cz.covid19cz.erouska.ui.permissions.BasePermissionsVM

class PermissionsOnboardingVM(bluetoothManager: BluetoothManager, app: Application) :
    BasePermissionsVM(bluetoothManager, app) {
    override fun goToNextScreen() {
        navigate(R.id.action_nav_bt_onboard_to_nav_login)
    }
}