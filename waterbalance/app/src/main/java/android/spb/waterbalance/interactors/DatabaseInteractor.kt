package android.spb.waterbalance.interactors

import android.spb.waterbalance.database.data.UserData
import android.spb.waterbalance.database.data.DailyStatisticData
import android.spb.waterbalance.database.data.DrinkData
import android.spb.waterbalance.presentation.startpage_activity.fragment_chart.ChartWeek
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface DatabaseInteractor {
    fun getMainUser(): Maybe<UserData>

    fun updateUser(userData: UserData): Completable

    fun insertUser(userData: UserData): Completable

    fun getAllUsers(): Flowable<List<UserData>>

    fun getAllStatisticsUser(userData: UserData): Single<List<DailyStatisticData>?>

    fun addDrinkToUser(userData: UserData, drinkData: DrinkData): Single<DailyStatisticData>

    // [statistic?, [daysFromStart, lastPeriod]]
    fun getTodayStatisticForUser(userData: UserData): Single<Pair<DailyStatisticData?, Pair<Int, Int>>>

    fun getChartStatisticForUser(userData: UserData): Single<ChartWeek>

    fun getWaterBalanceRecord(userData: UserData): Single<Int>

    fun getDrinksStatistic(userData: UserData): Single<List<DrinkData>>
}