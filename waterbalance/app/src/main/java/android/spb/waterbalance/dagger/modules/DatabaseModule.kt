package android.spb.waterbalance.dagger.modules

import android.content.Context
import android.spb.waterbalance.database.dao.DailyStatisticDataDao
import android.spb.waterbalance.database.Database
import android.spb.waterbalance.database.dao.DrinkDataDao
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(ctx: Context) = Room.databaseBuilder(
                        ctx.applicationContext,
                        Database::class.java,
                        Database.DB_VERSION)
//                        .allowMainThreadQueries()
                        .build()

    @Provides
    @Singleton
    fun provideUserDaoInterface(db: Database) = db.userDataDao()

    @Provides
    @Singleton
    fun provideStatisticDataDao(db: Database): DailyStatisticDataDao = db.dailyStatisticDataDao()

    @Provides
    @Singleton
    fun provideDrinkDataDao(db: Database): DrinkDataDao = db.drinkDataDao()
}