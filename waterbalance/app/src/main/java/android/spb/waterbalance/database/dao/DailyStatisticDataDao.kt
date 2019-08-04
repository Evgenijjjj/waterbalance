package android.spb.waterbalance.database.dao

import android.spb.waterbalance.database.data.DailyStatisticData
import androidx.room.*

@Dao
interface DailyStatisticDataDao {
    @Query("DELETE from userdata")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ds: DailyStatisticData)

    @Query("SELECT * FROM DailyStatisticData WHERE id = :id LIMIT 1")
    fun getDailyStatistic(id: Int): DailyStatisticData

    @Update
    fun update(userData: DailyStatisticData)

    @Query("SELECT * from DailyStatisticData WHERE id = (SELECT MAX(id) from DailyStatisticData)")
    fun getLastDailyStatisticData(): DailyStatisticData
}