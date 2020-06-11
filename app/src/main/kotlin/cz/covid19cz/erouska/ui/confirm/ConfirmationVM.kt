package cz.covid19cz.erouska.ui.confirm

import androidx.lifecycle.viewModelScope
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import cz.covid19cz.erouska.AppConfig
import cz.covid19cz.erouska.AppConfig.FIREBASE_REGION
import cz.covid19cz.erouska.db.DatabaseRepository
import cz.covid19cz.erouska.db.SharedPrefsRepository
import cz.covid19cz.erouska.db.export.CsvExporter
import cz.covid19cz.erouska.ui.base.BaseVM
import cz.covid19cz.erouska.ui.confirm.event.ErrorEvent
import cz.covid19cz.erouska.ui.confirm.event.FinishedEvent
import cz.covid19cz.erouska.ui.confirm.event.LogoutEvent
import cz.covid19cz.erouska.utils.Auth
import cz.covid19cz.erouska.utils.L
import cz.covid19cz.erouska.utils.formatPhoneNumber
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ConfirmationVM(
    private val database: DatabaseRepository,
    private val prefs: SharedPrefsRepository,
    private val exporter: CsvExporter
) : BaseVM() {

    companion object {
        const val UPLOAD_TIMEOUT_MILLIS = 30000L
    }

    private val functions = Firebase.functions(FIREBASE_REGION)
    private var exportDisposable: Disposable? = null
    private val storage = Firebase.storage

    override fun onCleared() {
        super.onCleared()
        exportDisposable?.dispose()
    }

    fun deleteAllData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val data = hashMapOf(
                        "buid" to prefs.getDeviceBuid()
                    )
                    functions.getHttpsCallable("deleteUploads").call(data).await()
                    database.clear()
                    publish(FinishedEvent)
                } catch (e: Exception) {
                    handleError(e)
                }
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    functions.getHttpsCallable("deleteUser").call().await()
                    database.clear()
                    prefs.clear()
                    Auth.signOut()
                    publish(FinishedEvent)
                } catch (e: Exception) {
                    handleError(e)
                }
            }
        }
    }

    fun sendData() {
        exportDisposable?.dispose()
        val buid = prefs.getDeviceBuid()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val data = hashMapOf(
                        "buid" to buid
                    )
                    val active = functions.getHttpsCallable("isBuidActive").call(data).await().data as? Boolean ?: false
                    if (!active) {
                        publish(LogoutEvent)
                    }
                    else {
                        exportDisposable = exporter.export().subscribe({
                            uploadToStorage(it)
                        }, {
                            handleError(it)
                        })
                    }
                }
                catch (e: Exception) {
                    handleError(e)
                }
            }
        }
    }

    private fun uploadToStorage(csv: ByteArray) {
        val fuid = Auth.getFuid()
        val timestamp = System.currentTimeMillis()
        val buid = prefs.getDeviceBuid()
        storage.maxUploadRetryTimeMillis = UPLOAD_TIMEOUT_MILLIS;
        val ref = storage.reference.child("proximity/$fuid/$buid/$timestamp.csv")
        val metadata = storageMetadata {
            contentType = "text/csv"
            setCustomMetadata("version", AppConfig.CSV_VERSION.toString())
        }
        ref.putBytes(csv, metadata).addOnSuccessListener {
            prefs.saveLastUploadTimestamp(timestamp)
            publish(FinishedEvent)
        }.addOnFailureListener {
            handleError(it)
        }
    }

    private fun handleError(e: Throwable) {
        L.e(e)
        if (e is FirebaseFunctionsException && e.code == FirebaseFunctionsException.Code.UNAUTHENTICATED) {
            publish(LogoutEvent)
        } else if (e is StorageException && e.errorCode == StorageException.ERROR_NOT_AUTHENTICATED) {
            publish(LogoutEvent)
        } else {
            publish(ErrorEvent(e))
        }
    }
}
