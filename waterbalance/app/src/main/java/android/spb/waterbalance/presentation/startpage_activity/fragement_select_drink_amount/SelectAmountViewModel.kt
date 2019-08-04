package android.spb.waterbalance.presentation.startpage_activity.fragement_select_drink_amount

import android.spb.waterbalance.app.App
import android.spb.waterbalance.base.SingleLiveEvent
import android.spb.waterbalance.database.data.DailyStatisticData
import android.spb.waterbalance.database.data.DrinkData
import android.spb.waterbalance.database.data.UserData
import android.spb.waterbalance.interactors.DatabaseInteractor
import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SelectAmountViewModel: ViewModel() {

    @Inject
    lateinit var dbInteractor: DatabaseInteractor

    val successSingleLiveEvent = SingleLiveEvent<DailyStatisticData?>()

    val compositeDisposable = CompositeDisposable()

    init { App.component.inject(this) }

    fun addDrinkToUser(userData: UserData, drinkData: DrinkData) {
        compositeDisposable.add(
            dbInteractor.addDrinkToUser(userData, drinkData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("addNewDrinkToUser", it.toString())
                    successSingleLiveEvent.postValue(it)
                }){
                    Log.d("addNewDrinkToUser", it.toString())
                    successSingleLiveEvent.postValue(null)
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}