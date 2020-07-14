package com.chilangolabs.hexclock

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.robinhood.ticker.TickerUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    private val job = SupervisorJob()
    private val coroutineContext: CoroutineContext
        get() {
            return job + Dispatchers.Main
        }
    private var scope = CoroutineScope(coroutineContext)

    private var timer: Timer? = null

//    val viewModel by lazy {
//        ViewModelProvider(this).get(MainViewModel::class.java)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtClockColor?.setCharacterLists(TickerUtils.provideAlphabeticalList())
        txtClockColorTime?.setCharacterLists(TickerUtils.provideNumberList())

    }

//    private fun setUpObservables() {
//        viewModel.mutableColor.observe(this, androidx.lifecycle.Observer { color ->
//            applyColor(color)
//        })
//    }

    override fun onResume() {
        super.onResume()
        //StartClock

//        setUpObservables()

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                scope.launch() {
//                    Log.i("CTX", "Coroutine ->>>")
//                    viewModel.getLastHexaTime()
                    val color = getHexaTime()
                    applyColor(color)
                }
            }
        }, 0, 1000)
    }

    override fun onRestart() {
        super.onRestart()
        scope = CoroutineScope(coroutineContext)
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        scope.cancel()
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

    private fun applyColor(hexHour: HexHour) {
        val intColor = Color.parseColor(hexHour.color)
        rootView.setBackgroundColor(intColor)
        txtClockColor.text = hexHour.color
        txtClockColorTime.text = hexHour.hour
        txtClockColor.textColor = colorForText(intColor)
        txtClockColorTime.textColor = colorForText(intColor)
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
