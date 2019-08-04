package android.spb.waterbalance.database.data

import android.os.Parcelable
import android.spb.waterbalance.R
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "DrinkData")
data class DrinkData(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "amount") var amount: Int,
    @ColumnInfo(name = "factor") var factor: Float,
    @ColumnInfo(name = "nextDrinkId") var nextDrinkId: Int?
): Parcelable {
    constructor(): this(null, "", 0, 0f, null)
    constructor(name: String) : this(null, name, 0, 1f, null)
    constructor(name: String, factor: Float) : this(null, name, 0, factor, null)
}

// TODO: пернести из ресурсов в константы
fun DrinkData.getDrawableRes() = when (name) {
    "Вода" -> R.drawable.water_splash
    "Вода с газом" -> R.drawable.water_s
    "Черный кофе"  -> R.drawable.coffee
    "Черный чай" -> R.drawable.tea_cup
    "Крепкий алкоголь" -> R.drawable.glass_alco
    "Молоко" -> R.drawable.milk
    "Сок"-> R.drawable.fruit
    "Энергетик" -> R.drawable.energy_drink
    "Вино" -> R.drawable.wine_bottle
    "Газировка" -> R.drawable.can
    "Йогурт"-> R.drawable. yogurt
    "Добавить" -> R.drawable.baseline_add_circle_24
    else -> R.drawable.glass
}

val namesList = listOf("Вода", "Вода с газом", "Черный кофе", "Черный чай", "Крепкий алкоголь", "Молоко", "Сок",
    "Энергетик", "Вино", "Вино", "Газировка" , "Йогурт")