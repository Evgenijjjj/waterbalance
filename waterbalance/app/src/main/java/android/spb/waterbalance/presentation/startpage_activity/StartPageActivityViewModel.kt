package android.spb.waterbalance.presentation.startpage_activity

import android.content.SharedPreferences
import android.spb.waterbalance.app.App
import android.spb.waterbalance.base.SingleLiveEvent
import android.spb.waterbalance.database.data.DailyStatisticData
import android.spb.waterbalance.database.data.DrinkData
import android.spb.waterbalance.database.data.UserData
import android.spb.waterbalance.database.data.getDefaultDailyNorm
import android.spb.waterbalance.interactors.DatabaseInteractor
import android.spb.waterbalance.presentation.startpage_activity.fragment_chart.ChartWeek
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class StartPageActivityViewModel: ViewModel() {

    @Inject
    lateinit var sharePrefs: SharedPreferences

    @Inject
    lateinit var dbInteractor: DatabaseInteractor

    val wizardSingleLiveEvent =  SingleLiveEvent<Any>()

    val userLiveData = MutableLiveData<UserData>()

    private val compositeDisposable = CompositeDisposable()

    val drinkSelectedLiveData = MutableLiveData<DrinkData>()

    val titleVisibilityLiveData = MutableLiveData<Boolean>()

    val chooseNewDrinkLiveData = MutableLiveData<Any?>()

    val todayStatisticLiveData = MutableLiveData<Pair<DailyStatisticData, Pair<Int, Int>>>()

    val chartStatisticLiveData = MutableLiveData<ChartWeek>()

    val waterBalanceRecordLiveData = MutableLiveData<Int>()

    val drinksStatLiveData = MutableLiveData<List<DrinkData>>()

    val todoLiveData = SingleLiveEvent<Any?>()

    init {
        App.component.inject(this)
    }

    fun refresh() {
        getMainUserData()
    }

    private fun getMainUserData() {
        compositeDisposable.add(
            dbInteractor.getMainUser()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    if (userLiveData.value == null)
                        wizardSingleLiveEvent.call()
                }
                .subscribe({
                    StartPageActivity.log(it)
                    userLiveData.postValue(it)
                    getTodayStatisticForUser(it)
                    getChartStatistic(it)
                    getWaterBalanceRecord(it)
                    getDrinksStat(it)
                }) { StartPageActivity.log(it) }
        )
    }

    private fun getTodayStatisticForUser(userData: UserData) {
        compositeDisposable.add(
            dbInteractor.getTodayStatisticForUser(userData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    StartPageActivity.log(it.toString())
                    if (it.first == null) {
                        val ds = DailyStatisticData()
                        ds.dailyNorm = userData.getDefaultDailyNorm()
                        todayStatisticLiveData.postValue(ds to it.second)
                    } else {
                        todayStatisticLiveData.postValue(it.first!! to it.second)
                    }
                }){
                    StartPageActivity.log(it)
                    val ds = DailyStatisticData()
                    ds.dailyNorm = userData.getDefaultDailyNorm()
                    todayStatisticLiveData.postValue(ds to (1 to 1))
                }
        )
    }

    private fun getChartStatistic(userData: UserData) {
        compositeDisposable.add(
            dbInteractor.getChartStatisticForUser(userData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    chartStatisticLiveData.postValue(it)
                }) { StartPageActivity.log(it) }
        )
    }

    private fun getWaterBalanceRecord(userData: UserData) {
        compositeDisposable.add(
            dbInteractor.getWaterBalanceRecord(userData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    waterBalanceRecordLiveData.postValue(it)
                }){ StartPageActivity.log(it) }
        )
    }

    private fun getDrinksStat(userData: UserData) {
        compositeDisposable.add(
            dbInteractor.getDrinksStatistic(userData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    drinksStatLiveData.postValue(it)
                }){ StartPageActivity.log(it) }
        )
    }

    fun selectDrink(data: DrinkData) {
        if (data.name != "Добавить") {
            drinkSelectedLiveData.postValue(data)
        } else {
            todoLiveData.call()
            chooseNewDrinkLiveData.postValue(null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}