package com.chilangolabs.hexclock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainViewModel : ViewModel() {


    val mutableColor = MutableLiveData<MainActivity.HexHour>()

    fun getLastHexaTime() {
        viewModelScope.launch {
            mutableColor.value = getHexaTime()
        }
    }

    private suspend fun getHexaTime() = withContext(Dispatchers.Default) {
        val cal = Calendar.getInstance()

        val colorR = "#${cal.getHexaColor(Calendar.HOUR_OF_DAY)}${cal.getHexaColor(Calendar.MINUTE)}${cal.getHexaColor(Calendar.SECOND)}"
        val formatHour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))
        val formatMinute = String.format("%02d", cal.get(Calendar.MINUTE))
        val formatSecond = String.format("%02d", cal.get(Calendar.SECOND))

        val hourR = "$formatHour:$formatMinute:$formatSecond"
        return@withContext MainActivity.HexHour(colorR, hourR)
    }

    fun Calendar.getHexaColor(date: Int): String = when (date) {
        Calendar.HOUR_OF_DAY -> String.format("%02X", this.get(date) * 255 / 23)
        Calendar.MINUTE -> String.format("%02X", this.get(date) * 255 / 59)
        Calendar.SECOND -> String.format("%02X", this.get(date) * 255 / 59)
        else -> ""
    }
}