package com.bensadiku.flashlight.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bensadiku.flashlight.utils.PreferenceHelper


class InitialFragmentViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private var COUNTDOWN_TIME = 300000L

        private const val CAMERA_FRONT = "1"
        private const val CAMERA_BACK = "0"
    }


    //CountDownTimer values
    private var countDownTimer: CountDownTimer? = null
    private val _time = MutableLiveData<Long>()

    // The String version of the current time
    val currentTimeString = Transformations.map(_time) { time ->
        DateUtils.formatElapsedTime(time)
    }

    private val _hasPermissions = MutableLiveData<Boolean>()
    val hasPermissions: LiveData<Boolean>
        get() = _hasPermissions

    private val _flashLightStatus = MutableLiveData<Boolean>()
    val flashLightStatus: LiveData<Boolean>
        get() = _flashLightStatus

    private val context: Context = application.applicationContext

    private lateinit var cameraManager: CameraManager
    //private lateinit var cameraId: String//cameraId = cameraManager.cameraIdList[0]
    private var hasCameraFlash: Boolean

    private var cameraId = CAMERA_BACK

    // ------- Shared Preferences variables -------\\

    private var lightType: Boolean = false

    private val _enableTimer = MutableLiveData<Boolean>()
    val enableTimer: LiveData<Boolean>
        get() = _enableTimer

    private val _keepAwake = MutableLiveData<Boolean>()
    val keepAwake: LiveData<Boolean>
        get() = _keepAwake

    private val _sendNotifications = MutableLiveData<Boolean>()
    val sendNotifications: LiveData<Boolean>
        get() = _sendNotifications

    /**
     * Init cameraManager
     * Check if the device has flashlight
     * Check permissions
     */
    init {
        try {
            cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        } catch (e: Exception) {

        }
        hasCameraFlash = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        checkPermissions()
    }

    /**
     * @param countDown contains the countdown of the timer when it's enabled
     *
     * onTick will divide by a second
     * onFinish will just call the method  flashLightOff() which turns off flashlight
     */
    private fun setupTimer(countDown: Long) {
        //CountDownTimer
        countDownTimer = object : CountDownTimer(countDown, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _time.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                flashLightOff()
            }
        }
    }

    /**
     * Will get preferences from the PreferenceHelperClass
     */
    fun getPreferences() {
        val prefHelper = PreferenceHelper(context)

        _enableTimer.value = prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.EnableTimer)
        _keepAwake.value = prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.KeepAwake)
        _sendNotifications.value = prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.SendNotifications)

        //Check if the user changed the timer, and set it to the COUNTDOWN_TIME variable and setupTimer with it or with the default one
        if (!prefHelper.getTimer(PreferenceHelper.Timer.AddTimer).isNullOrEmpty()) {
            COUNTDOWN_TIME = prefHelper.getTimer(PreferenceHelper.Timer.AddTimer).toLong()
        }
        setupTimer(COUNTDOWN_TIME)


        //checks the light type, if it's true use switched to front light
        //else it's the back light
        lightType = prefHelper.getBooleanPref(PreferenceHelper.BooleanPref.PickLight)
        cameraId = when (lightType) {
            true -> CAMERA_FRONT
            false -> CAMERA_BACK
        }
    }

    /**
     * Cancel the timer if the viewmodel is destroyed
     */
    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }

    /**
     * Checks if the app has permissions
     * Assigns the boolean value to the _hasPermissions which is then set to a livedata variable hasPermissions
     * hasPermissions variable in turn is observed by the InitialFragment
     */
    fun checkPermissions() {
        _hasPermissions.value =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Public method toggleFlashlight is called by the InitialFragment
     * This method will determine whether to turn on the flashlight or not by checking if the camera has flash and if the flash is on
     */
    fun toggleFlashlight() {
        if (hasCameraFlash) {
            if (_flashLightStatus.value == true)
                flashLightOff()
            else
                flashLightOn()
        } else {
            Toast.makeText(
                context, "No flash available on your device", Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Will turn on the flashlight using the cameraManager and passing the cameraId which could be 0 or 1 based on the preference of the user and the boolean value true
     * Will also determine whether it should start the countdown or not based on the user preference
     */
    private fun flashLightOn() {
        try {
            cameraManager.setTorchMode(cameraId, true)
            _flashLightStatus.value = true
            if (_enableTimer.value == true) {
                countDownTimer?.start()
            }
        } catch (e: Exception) {
        }
    }

    /**
     * Will turn off the flashlight using the cameraManager and passing the cameraId which could be 0 or 1 based on the preference of the user and the boolean value false
     * Will also determine whether it should stop the countdown or not based on the user preference
     */
    private fun flashLightOff() {
        try {
            cameraManager.setTorchMode(cameraId, false)
            _flashLightStatus.value = false

            if (_enableTimer.value == true) {
                _time.value = DONE
                countDownTimer?.cancel()
            }
        } catch (e: CameraAccessException) {
        }
    }
}