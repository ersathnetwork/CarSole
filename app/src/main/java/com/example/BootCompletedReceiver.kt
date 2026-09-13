package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Head units power-cycle constantly (ignition on/off). Since CarSole is now
 * registered as a HOME app, we relaunch it right after boot so the driver
 * always lands on the dashboard instead of a blank screen or the stock
 * launcher picker.
 */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(launchIntent)
        }
    }
}
