package android.spb.waterbalance.interactors

import android.spb.waterbalance.base.getDate
import android.spb.waterbalance.base.getMonth
import android.spb.waterbalance.database.dao.DailyStatisticDataDao
import android.spb.waterbalance.database.dao.DrinkDataDao
import android.spb.waterbalance.database.dao.UserDataDao
import android.spb.waterbalance.database.data.*
import android.spb.waterbalance.presentation.startpage_activity.fragment_chart.ChartDay
import android.spb.waterbalance.presentation.startpage_activity.fragment_chart.ChartWeek
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collector
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.math.abs

class DatabaseInteractorImpl(
    private val userDataDao: UserDataDao,
    private val statisticDataDao: DailyStatisticDataDao,
    private val drinkDataDao: DrinkDataDao
) : DatabaseInteractor {

    private val asyncUserDao = Single.just(userDataDao).subscribeOn(Schedulers.io())
    private val asyncDrinkDao = Single.just(drinkDataDao).subscribeOn(Schedulers.io())
    private val asyncDailyStatDao = Single.just(statisticDataDao).subscribeOn(Schedulers.io())

    override fun getMainUser(): Maybe<UserData> {
        return userDataDao.getMainUser().subscribeOn(Schedulers.io())
    }

    override fun updateUser(userData: UserData): Completable {
        return asyncUserDao.map { it.updateUser(userData) }.toCompletable()
    }

    override fun insertUser(userData: UserData): Completable {
        return asyncUserDao.map { it.insert(userData) }.toCompletable()
    }

    override fun getAllUsers(): Flowable<List<UserData>> {
        return userDataDao.getAll().subscribeOn(Schedulers.newThread())
    }

    override fun getAllStatisticsUser(userData: UserData): Single<List<DailyStatisticData>?> {
        return userDataDao.getUserWithId(userData.id ?: 0)
            .map { getAllStatisticForUser(it) }
            .subscribeOn(Schedulers.io())
            .toSingle()
    }

    override fun getTodayStatisticForUser(userData: UserData): Single<Pair<DailyStatisticData?, Pair<Int, Int>>> {
        return asyncUserDao
            .map {
                val daysFromStart = getDaysFromStart(userData)
                val allStat = getAllStatisticForUser(userData)?.sortedBy { it.id }
                val lastStat = allStat?.last()
                Log.d("getTodayStatisticForUser", "all stat: $allStat")

                if (lastStat == null) {
                    return@map null to (daysFromStart to 1)
                }

                if (isToday(Date(), Date.from(Instant.ofEpochMilli(Date.parse(lastStat.dateString))), daysOffset = 0)) {
                    Log.d("getTodayStatisticForUser", "last is today")
                    var days = 1

                    for ((i, d) in allStat.reversed().withIndex()) {
                        if (i == (allStat.size - 1)) break
                        val prevDay = allStat.reversed()[i + 1]
                        if (isToday(
                                Date.from(Instant.ofEpochMilli(Date.parse(d.dateString))),
                                Date.from(Instant.ofEpochMilli(Date.parse(prevDay.dateString))),
                                daysOffset = 1
                            )
                        ) {
                            Log.d("getTodayStatisticForUser", "TRUE: 1 = ${d.dateString}, 2 = ${prevDay.dateString} ")
                            days++
                        } else {
                            Log.d("getTodayStatisticForUser", "FALSE: 1 = ${d.dateString}, 2 = ${prevDay.dateString} ")
                            break
                        }
                    }
                    lastStat to (daysFromStart to days)
                } else if (isToday(
                        Date(),
                        Date.from(Instant.ofEpochMilli(Date.parse(lastStat.dateString))),
                        daysOffset = 1
                    )
                ) {
                    Log.d("getTodayStatisticForUser", "last was day ago")
                    var days = 2

                    for ((i, d) in allStat.reversed().withIndex()) {
                        if (i == (allStat.size - 1)) break
                        val prevDay = allStat.reversed()[i + 1]
                        if (isToday(
                                Date.from(Instant.ofEpochMilli(Date.parse(d.dateString))),
                                Date.from(Instant.ofEpochMilli(Date.parse(prevDay.dateString))),
                                daysOffset = 1
                            )
                        ) {
                            Log.d("getTodayStatisticForUser", "TRUE: 1 = ${d.dateString}, 2 = ${prevDay.dateString} ")
                            days++
                        } else {
                            Log.d("getTodayStatisticForUser", "FALSE: 1 = ${d.dateString}, 2 = ${prevDay.dateString} ")
                            break
                        }
                    }
                    null to (daysFromStart to days)
                } else {
                    null to (daysFromStart to 1)
                }
            }
    }

    override fun addDrinkToUser(userData: UserData, drinkData: DrinkData): Single<DailyStatisticData> {
        return asyncDrinkDao
            .map {
                it.insert(drinkData)
                it.getLastDrinkData()
            }
            .map {
                val stat = getAllStatisticForUser(userData)
                Log.d("addNewDrinkToUser", "init stat: $stat, drink data: $it")

                if (stat == null) {
                    Log.d("addNewDrinkToUser", "$stat")
                    createNewDailyStatisticsForUser(userData, it)
                } else {
                    Log.d("addNewDrinkToUser", "$stat")
                    val last = stat.sortedBy { it.id }.last()
                    val currentDate = Date()
                    val lastDate = Date.from(Instant.ofEpochMilli(Date.parse(last.dateString)))
                    Log.d("addNewDrinkToUser", "currentDate: $currentDate, lastDate: $lastDate")

                    if (isToday(currentDate, lastDate)) {
                        addDrinkToDailyStatistic(last, it)
                    } else {
                        addNewDailyStatistic(last, it, userData)
                    }
                }
            }
    }

    override fun getChartStatisticForUser(userData: UserData): Single<ChartWeek> {
        return asyncDailyStatDao
            .map {
                getAllStatisticForUser(userData) ?: emptyList()
            }
            .map {
                Log.d("getChartStatisticForUser", "all days: $it")

                val endCalendar = Calendar.getInstance().apply {
                    time = Date()
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }
                val endRange = endCalendar.time.time
                val startRange = endRange - TimeUnit.MILLISECONDS.convert(7L, TimeUnit.DAYS)
                val startCalendar = Calendar.getInstance().apply { time = Date(startRange) }
                Log.d("getChartStatisticForUser", "start Date: ${startCalendar.time}, end Date: ${endCalendar.time}")

                val daysInRange = it.filter {
                    Date.from(Instant.ofEpochMilli(Date.parse(it.dateString))).time in (startRange..endRange)
                }

                Log.d("getChartStatisticForUser", "days in range: $it")

                val mn = ChartDay(getDailyStat(daysInRange, Calendar.MONDAY), Calendar.MONDAY, "Пн")
                val tu = ChartDay(getDailyStat(daysInRange, Calendar.TUESDAY), Calendar.TUESDAY, "Вт")
                val we = ChartDay(getDailyStat(daysInRange, Calendar.WEDNESDAY), Calendar.WEDNESDAY, "Ср")
                val th = ChartDay(getDailyStat(daysInRange, Calendar.THURSDAY), Calendar.THURSDAY, "Чт")
                val fr = ChartDay(getDailyStat(daysInRange, Calendar.FRIDAY), Calendar.FRIDAY, "Пт")
                val sa = ChartDay(getDailyStat(daysInRange, Calendar.SATURDAY), Calendar.SATURDAY, "Сб")
                val su = ChartDay(getDailyStat(daysInRange, Calendar.SUNDAY), Calendar.SUNDAY, "Вс")

                val strRange =
                    "${startCalendar.get(Calendar.DAY_OF_MONTH)} ${getMonth(startCalendar.get(Calendar.MONTH))} - ${
                    endCalendar.get(Calendar.DAY_OF_MONTH)} ${getMonth(endCalendar.get(Calendar.MONTH))}"

                ChartWeek(listOf(mn, tu, we, th, fr, sa, su), strRange)
            }
    }

    override fun getWaterBalanceRecord(userData: UserData): Single<Int> {
        return asyncUserDao
            .map {
                getAllStatisticForUser(userData)?.filter { it.goodWaterBalance() }?.size ?: 0
            }
    }

    override fun getDrinksStatistic(userData: UserData): Single<List<DrinkData>> {
        return asyncUserDao
            .map {
                getAllStatisticForUser(userData) ?: emptyList()
            }
            .map {
                it.map { getAllDrinksForDailyStatistic(it) }
            }
            .map {
                it.map { it ?: emptyList() }.stream().flatMap { it.stream() }.collect(Collectors.toList())
            }
            .map {
                val newLst = mutableListOf<DrinkData>()

                for (drink in it) {
                    val findRes = newLst.findLast { it.name == drink.name }
                    if (findRes != null) {
                        findRes.amount += drink.amount
                    } else {
                        newLst.add(drink)
                    }
                }

                for (name in namesList) {
                    if (newLst.findLast { it.name == name } == null)
                        newLst.add(DrinkData().copy(name = name, amount = 0))
                }

                newLst.sortedByDescending { it.amount }
            }

    }

    private fun createNewDailyStatisticsForUser(userData: UserData, drinkData: DrinkData): DailyStatisticData {
        val dailyStat = DailyStatisticData()
        dailyStat.dateString = Date().toString()
        val total = drinkData.factor * drinkData.amount
        if (total > 0) {
            dailyStat.total = total
            dailyStat.dailyNorm = userData.getDefaultDailyNorm()
        } else {
            dailyStat.dailyNorm = addBadDrinkToUserDailyNorm(userData.getDefaultDailyNorm(), abs(total))
            Log.d("addNewDrinkToUser", "change daily norm: total = $total, new Dailynorm = ${dailyStat.dailyNorm}")
        }
        dailyStat.firstDrinkPtr = drinkData.id
        statisticDataDao.insert(dailyStat)
        val ret = statisticDataDao.getLastDailyStatisticData()
        userDataDao.updateUser(userData.copy(firstDayPtr = ret.id))
        return ret
    }

    private fun addDrinkToDailyStatistic(
        dailyStatisticData: DailyStatisticData,
        drinkData: DrinkData
    ): DailyStatisticData {
        val total = drinkData.factor * drinkData.amount
        if (total > 0) {
            dailyStatisticData.total += total
        } else {
            dailyStatisticData.dailyNorm = addBadDrinkToUserDailyNorm(dailyStatisticData.dailyNorm, abs(total))
            Log.d(
                "addNewDrinkToUser",
                "change daily norm: total = $total, new Dailynorm = ${dailyStatisticData.dailyNorm}"
            )
        }
        val drink = getAllDrinksForDailyStatistic(dailyStatisticData)?.last()
        if (drink == null) {
            dailyStatisticData.firstDrinkPtr = drinkData.id
        } else {
            drink.nextDrinkId = drinkData.id
            drinkDataDao.updateDrinkData(drink)
        }
        statisticDataDao.update(dailyStatisticData)
        return statisticDataDao.getDailyStatistic(dailyStatisticData.id!!)
    }

    private fun addNewDailyStatistic(
        lastStat: DailyStatisticData,
        drinkData: DrinkData,
        userData: UserData
    ): DailyStatisticData {
        var newStat = DailyStatisticData()

        val total = drinkData.factor * drinkData.amount

        newStat.apply {
            dateString = Date().toString()
            firstDrinkPtr = drinkData.id
            if (total > 0) {
                newStat.total = total
                newStat.dailyNorm = userData.getDefaultDailyNorm()
            } else
                newStat.dailyNorm = addBadDrinkToUserDailyNorm(userData.getDefaultDailyNorm(), abs(total))
        }
        statisticDataDao.insert(newStat)
        newStat = statisticDataDao.getLastDailyStatisticData()
        lastStat.nextDayPtr = newStat.id
        statisticDataDao.update(lastStat)
        return newStat
    }

    private fun isToday(d1: Date, d2: Date, daysOffset: Int = 0): Boolean {
        val c1 = Calendar.getInstance().apply { setTime(d1) }
        val c2 = Calendar.getInstance().apply { setTime(d2) }
        Log.d(
            "addNewDrinkToUser",
            "dayOfWeek: 1 = ${c1.get(Calendar.DAY_OF_WEEK)}, 2 = $${c2.get(Calendar.DAY_OF_WEEK)}"
        )
        Log.d("addNewDrinkToUser", "month: 1 = ${c1.get(Calendar.MONTH)}, 2 = $${c2.get(Calendar.MONTH)}")
        Log.d("addNewDrinkToUser", "year: 1 = ${c1.get(Calendar.YEAR)}, 2 = $${c2.get(Calendar.YEAR)}")

        return (abs(c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH)) == daysOffset) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
    }

    private fun getDaysFromStart(userData: UserData): Int {
        val diff = Date().time - Date.from(Instant.ofEpochMilli(Date.parse(userData.regDate))).time

        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt() + 1
    }

    private fun getAllStatisticForUser(userData: UserData): List<DailyStatisticData>? {
        Log.d("addNewDrinkToUser", "getAllStatisticForUser: user = $userData")
        if (userData.firstDayPtr == null) return null
        val lst = ArrayList<DailyStatisticData>()

        val first = statisticDataDao.getDailyStatistic(userData.firstDayPtr!!)
        lst.add(first)

        var ptr = first.nextDayPtr
        while (ptr != null) {
            val next = statisticDataDao.getDailyStatistic(ptr)
            lst.add(next)
            ptr = next.nextDayPtr
        }

        return lst
    }

    private fun getAllDrinksForDailyStatistic(ds: DailyStatisticData): List<DrinkData>? {
        if (ds.firstDrinkPtr == null) return null
        val lst = ArrayList<DrinkData>()

        val first = drinkDataDao.getDrinkData(ds.firstDrinkPtr!!)
        lst.add(first)

        var ptr = first.nextDrinkId
        while (ptr != null) {
            val next = drinkDataDao.getDrinkData(ptr)
            lst.add(next)
            ptr = next.nextDrinkId
        }

        return lst
    }

    private fun getDailyStat(initDays: List<DailyStatisticData>, dayOfWeek: Int): DailyStatisticData? {
        val stat = initDays.findLast {
            val c = Calendar.getInstance().apply { time = it.getDate() }
            c.get(Calendar.DAY_OF_WEEK) == dayOfWeek
        }

        return stat
    }
}