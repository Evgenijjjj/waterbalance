package android.spb.waterbalance.dagger.modules

import android.spb.waterbalance.database.dao.DailyStatisticDataDao
import android.spb.waterbalance.database.dao.DrinkDataDao
import android.spb.waterbalance.database.dao.UserDataDao
import android.spb.waterbalance.interactors.DatabaseInteractor
import android.spb.waterbalance.interactors.DatabaseInteractorImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class InteractorsModule {

    @Provides
    @Singleton
    fun provideDatabaseInteractor(
        userDataDao: UserDataDao,
        dailyStatisticDataDao: DailyStatisticDataDao,
        drinkDataDao: DrinkDataDao
    ): DatabaseInteractor = DatabaseInteractorImpl(userDataDao, dailyStatisticDataDao, drinkDataDao)
}