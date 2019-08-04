package android.spb.waterbalance.base

import android.spb.waterbalance.database.data.DailyStatisticData
import java.time.Instant
import java.util.*

fun Float.toDecimalsString(d: Int = 2) = String.format("%.${d}f", this)

fun DailyStatisticData.getDate() = Date.from(Instant.ofEpochMilli(Date.parse(dateString)))

fun getMonth(monthInt: Int) = when (monthInt) {
    0 -> "Янв"
    1 -> "Фев"
    2 -> "Март"
    3 -> "Апр"
    4 -> "Май"
    5 -> "Июнь"
    6 -> "Июль"
    7 -> "Авг"
    8 -> "Сен"
    9 -> "Окт"
    10 -> "Ноябрь"
    11 -> "Дек"
    else -> ""
}