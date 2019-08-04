package android.spb.waterbalance.database.data

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "UserData")
data class UserData(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "isMale") var isMale: Boolean,
    @ColumnInfo(name = "growth") var growth: Int,
    @ColumnInfo(name = "weight") var weight: Int,
    @ColumnInfo(name = "age") var age: Int,
    @ColumnInfo(name = "firstDayPtr") var firstDayPtr: Int?,
    @ColumnInfo(name = "isMain") var isMain: Boolean = false,
    @ColumnInfo(name = "regDate") var regDate: String
    )

fun UserData.getDefaultDailyNorm(): Float {
    var newValue = (30 * weight).toFloat()
    if (age < 14f) {
        newValue /= (age / 15f)
    }

    if (newValue > 5000f)
        newValue = 5000f

    return newValue
}

fun addBadDrinkToUserDailyNorm(currentNorm: Float, drinkValue: Float): Float {
    Log.d("addNewDrinkToUser", "addBadDrinkToUserDailyNorm: currentNorm: $currentNorm, drinkValue: $drinkValue")
    return if (currentNorm + drinkValue >= 5000f) 5000f
    else currentNorm + drinkValue
}