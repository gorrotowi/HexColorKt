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

    private var timer: Timer? = null

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
                    val data: Deferred<HexHour> = bg {
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

    private fun getHexaTime(): HexHour {
        val cal = Calendar.getInstance()

        val colorR = "#${cal.getHexaColor(Calendar.HOUR_OF_DAY)}${cal.getHexaColor(Calendar.MINUTE)}${cal.getHexaColor(Calendar.SECOND)}"
        val formatHour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))
        val formatMinute = String.format("%02d", cal.get(Calendar.MINUTE))
        val formatSecond = String.format("%02d", cal.get(Calendar.SECOND))

        val hourR = "$formatHour:$formatMinute:$formatSecond"
        return HexHour(colorR, hourR)
    }

    private fun applyColor(colors: HexHour) {
        val intColor = Color.parseColor(colors.color)
        rootView.setBackgroundColor(intColor)
        txtClockColor.text = colors.color
        txtClockColorTime.text = colors.hour
        txtClockColor.setTextColor(colorForText(intColor))
        txtClockColorTime.setTextColor(colorForText(intColor))
        window.statusBarColor = intColor
    }

    fun Calendar.getHexaColor(date: Int): String = when (date) {
        Calendar.HOUR_OF_DAY -> String.format("%02X", this.get(date) * 255 / 23)
        Calendar.MINUTE -> String.format("%02X", this.get(date) * 255 / 59)
        Calendar.SECOND -> String.format("%02X", this.get(date) * 255 / 59)
        else -> ""
    }

    private fun colorForText(color: Int): Int {
        val tmpColor = ((0.02126 * Color.red(color)) + (0.7152 * Color.green(color)) + (0.0722 * Color.blue(color))).toInt()
        return if (tmpColor < 128) {
            Color.WHITE
        } else {
            Color.BLACK
        }
    }

    data class HexHour(val color: String, val hour: String)

}
