package android.spb.waterbalance.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DailyStatisticData")
data class DailyStatisticData(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "dateString") var dateString: String,
    @ColumnInfo(name = "total") var total: Float,
    @ColumnInfo(name = "nextDayPtr") var nextDayPtr: Int?,
    @ColumnInfo(name = "firstDrinkPtr") var firstDrinkPtr: Int? = null,
    @ColumnInfo(name = "dailyNorm") var dailyNorm: Float = 0f
) {
    constructor(): this(
        id = null,
        dateString = "",
        nextDayPtr = null,
        total = 0f)
}

fun DailyStatisticData.goodWaterBalance() = dailyNorm <= total