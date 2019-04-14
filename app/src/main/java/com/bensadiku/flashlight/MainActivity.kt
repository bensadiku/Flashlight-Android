package com.bensadiku.flashlight

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Will host the other fragments that will be showing using the Navigation components
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
