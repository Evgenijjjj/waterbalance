package android.spb.waterbalance.database.dao

import android.spb.waterbalance.database.data.UserData
import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface UserDataDao {
    @Query("DELETE from userdata")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userData: UserData)

    @Query("SELECT * from userdata")
    fun getAll(): Flowable<List<UserData>>

    @Query("SELECT * from userdata WHERE name = :name LIMIT 1")
    fun getUserWithName(name: String): Maybe<UserData>

    @Query("SELECT * from userdata WHERE id = :id LIMIT 1")
    fun getUserWithId(id: Int): Maybe<UserData>

    @Query("SELECT * from userdata WHERE isMain = :isMain LIMIT 1")
    fun getMainUser(isMain: Boolean = true): Maybe<UserData>

    @Update
    fun updateUser(userData: UserData)
}