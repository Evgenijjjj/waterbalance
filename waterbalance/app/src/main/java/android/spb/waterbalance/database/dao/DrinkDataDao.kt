package android.spb.waterbalance.database.dao

import android.spb.waterbalance.database.data.DrinkData
import androidx.room.*

@Dao
interface DrinkDataDao {
    @Query("DELETE from drinkdata")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(d: DrinkData)

    @Query("SELECT * FROM drinkdata WHERE id = :id LIMIT 1")
    fun getDrinkData(id: Int): DrinkData

    @Update
    fun updateDrinkData(drinkData: DrinkData)

    @Query("SELECT * from drinkdata WHERE id = (SELECT MAX(id) from drinkdata)")
    fun getLastDrinkData(): DrinkData
}