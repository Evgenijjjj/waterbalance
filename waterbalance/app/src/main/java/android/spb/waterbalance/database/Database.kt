package android.spb.waterbalance.database

import android.spb.waterbalance.database.dao.DailyStatisticDataDao
import android.spb.waterbalance.database.dao.DrinkDataDao
import android.spb.waterbalance.database.dao.UserDataDao
import android.spb.waterbalance.database.data.DailyStatisticData
import android.spb.waterbalance.database.data.DrinkData
import android.spb.waterbalance.database.data.UserData
import androidx.room.RoomDatabase

@androidx.room.Database(
    entities = [
        UserData::class,
        DailyStatisticData::class,
        DrinkData::class
    ], version = 1
)
abstract class Database : RoomDatabase() {

    companion object {
        const val DB_VERSION = "0.0.5"
    }

    abstract fun userDataDao(): UserDataDao
    abstract fun dailyStatisticDataDao(): DailyStatisticDataDao
    abstract fun drinkDataDao(): DrinkDataDao
}