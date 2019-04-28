package com.bensadiku.flashlight.ui.initialfragment

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bensadiku.flashlight.Flashlight
import com.bensadiku.flashlight.MainActivity
import com.bensadiku.flashlight.R
import com.bensadiku.flashlight.database.FlashlightDatabase

const val CAMERA_REQUEST = 50

class InitialFragment : Fragment() {
    private lateinit var buttonSwitch: Button
    private lateinit var imageSwitch: ImageView
    private lateinit var layout: ConstraintLayout
    private lateinit var imageSettings: ImageView
    private lateinit var textTimer: TextView

    private var canPushNotifications: Boolean = true
    private lateinit var initialFragmentViewModel: InitialFragmentViewModel
    private var notificationManager: NotificationManagerCompat? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //----Initializing viewmodel properties
        val application = requireNotNull(this.activity).application
        val flashlightDatabaseDao = FlashlightDatabase.getInstance(context!!).flashlightDatabaseDao()
        val viewModelFactory =
            InitialViewModelFactory(flashlightDatabaseDao, application)
        initialFragmentViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(InitialFragmentViewModel::class.java)


        notificationManager = NotificationManagerCompat.from(context!!)

        buttonSwitch = view.findViewById(R.id.activity_main_switch_button)
        imageSwitch = view.findViewById(R.id.activity_main_switch_image)
        layout = view.findViewById(R.id.activity_main_layout)
        imageSettings = view.findViewById(R.id.activity_main_settings_image)
        textTimer = view.findViewById(R.id.activity_main_timer_text)


        /**
         * Will navigate to the settings fragment(Preference Fragment)
         */
        imageSettings.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }

        /**
         * hasPermissions variable will be true when the user has granted permissions for the camera
         * The constraint layout click-listener will be enabled based on the hasPermissions variable
         * At the same time the hasPermissions variable will determine whether the buttonSwitch should be visible or not
         */
        initialFragmentViewModel.hasPermissions.observe(this, Observer { isEnabled ->
            layout.isEnabled = isEnabled
            when (isEnabled) {
                true -> buttonSwitch.visibility = View.GONE
                false -> buttonSwitch.visibility = View.VISIBLE
            }
        })

        /**
         * flashLightStatus variable will be observed for any changes.
         * This variable will be true only when the flash light is on in which case this observer will update the image
         */
        initialFragmentViewModel.flashLightStatus.observe(this, Observer { flashLightStatus ->
            if (flashLightStatus) {
                imageSwitch.setImageResource(R.mipmap.ic_flash_on)
                sendOnTimerChannel1(true)
            } else {
                imageSwitch.setImageResource(R.mipmap.ic_flash_off)
                sendOnTimerChannel1(false)
            }
        })

        /**
         * Will observe the currentTimeString variable in the viewmodel for the _timer to change and update the text
         * however the timer will not update in the viewmodel if the user has it disabled which is what we want
         */
        initialFragmentViewModel.currentTimeString.observe(this, Observer { timer ->
            textTimer.text = timer
        })

        //Requesting for permissions on the button
        buttonSwitch.setOnClickListener {
            requestPermissions(
                Array<String?>(2) { Manifest.permission.CAMERA },
                CAMERA_REQUEST
            )
        }

        /**
         * Click listener on the constraint layout, will call the viewmodel method
         * which will determine if it should turn the flashlight on or off
         */
        layout.setOnClickListener {
            initialFragmentViewModel.toggleFlashlight()
        }

        //------------ Shared Preferences observations ------------------

        /**
         * Observe the enableTimer variable in the viewmodel for changes
         * If the enableTimer variable is true, we show the text timer
         * if false, we hide it with view.gone
         */
        initialFragmentViewModel.enableTimer.observe(this, Observer { isEnabled ->
            when (isEnabled) {
                true -> textTimer.visibility = View.VISIBLE
                false -> textTimer.visibility = View.GONE
            }
        })

        /**
         * Will check is the user wants to keep the screen awake and will keep it or not based on that value
         * by default will be false
         */
        initialFragmentViewModel.keepAwake.observe(this, Observer { stayWoke ->
            view.keepScreenOn = stayWoke
        })

        /**
         * Will observe the sendNotification live data variable for changes
         * by default will be true
         * global variable canPushNotifications will be set to whatever the value of canSend is
         */
        initialFragmentViewModel.sendNotifications.observe(this, Observer { canSend ->
            canPushNotifications = canSend
        })
    }

    /**
     * Will send notifications on channel 1 if sdk >=O. If sdk is lower api then it will just send a generic notification to notify the flashlight is on.
     * @param sendNotification is a check if the notification should be pushed or removed
     * if the light is on, sendNotification param will be set to true in which case the notification will be pushed
     * else if the light is off, sendNotification param will be set to false, which will remove the notification
     *
     * Variable canPushNotifications will determine whether the notifications are allowed to be pushed or not.
     *      If not(is false), then on the else if, will check if the sendNotification param is false, in which case the notification just needs to be removed regardless of permission
     */
    private fun sendOnTimerChannel1(sendNotification: Boolean) {
        if (canPushNotifications) {
            if (sendNotification) {
                val activityIntent = Intent(context, MainActivity::class.java)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                val contentIntent =
                    PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT)

                val notification = NotificationCompat.Builder(context!!, Flashlight.TIMER_CHANNEL_1)
                    .setSmallIcon(R.mipmap.ic_flash_on)
                    .setContentTitle("Light is on!")
                    .setContentText("Tap to go back")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .build()
                notificationManager?.notify(1, notification)
            } else {
                notificationManager?.cancel(1)
            }
        } else if (!sendNotification) {
            notificationManager?.cancel(1)
        }
    }

    /**
     * If permissions are granted, ask the view model to check them again.
     * Why do this? Because we have a observer above "hasPermissions"  which will do ui clean up and enable buttons
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialFragmentViewModel.checkPermissions()
            } else {
                Toast.makeText(context, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show()
            }
        }
    }
}