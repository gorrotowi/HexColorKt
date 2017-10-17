package com.chilangolabs.hexclock

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.*

class MainActivity : AppCompatActivity() {

    var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        //StartClock
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                async(UI) {
                    val data: Deferred<Colors> = bg {
                        getHexaTime()
                    }
                    applyColor(data.await())
                }
            }
        }, 0, 1000)
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
    }

    private fun getHexaTime(): Colors {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY) * 255 / 23
        val minutes = calendar.get(Calendar.MINUTE) * 255 / 59
        val seconds = calendar.get(Calendar.SECOND) * 255 / 59

        val colorR = "#${String.format("%02X", hour)}${String.format("%02X", minutes)}${String.format("%02X", seconds)}"
        val hourR = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}"
        return Colors(colorR, hourR)
    }

    private fun applyColor(colors: Colors) {
        val intColor = Color.parseColor(colors.color)
        rootView.setBackgroundColor(intColor)
        txtClockColor.text = colors.color
        txtClockColorTime.text = colors.hour
        txtClockColor.setTextColor(colorForText(intColor))
        txtClockColorTime.setTextColor(colorForText(intColor))
        window.statusBarColor = intColor
    }

    private fun colorForText(color: Int): Int {
        val tmpColor = ((0.02126 * Color.red(color)) + (0.7152 * Color.green(color)) + (0.0722 * Color.blue(color))).toInt()
        return if (tmpColor < 128) {
            Color.WHITE
        } else {
            Color.BLACK
        }
    }

    data class Colors(val color: String, val hour: String)

}
