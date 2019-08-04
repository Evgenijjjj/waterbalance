package android.spb.waterbalance.presentation.new_user_activity

import android.content.SharedPreferences
import android.spb.waterbalance.R
import android.spb.waterbalance.app.App
import android.spb.waterbalance.database.data.UserData
import android.spb.waterbalance.interactors.DatabaseInteractor
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class NewUserActivityViewModel: ViewModel() {

    @Inject
    lateinit var dbInteractor: DatabaseInteractor

    private val compositeDisposable = CompositeDisposable()

    val successLiveData = MutableLiveData<Boolean>()
    val valueLiveData = MutableLiveData<Float>()

    private var segmentsSelectedPosition = 0
    private var currentGrowth = NewUserActivity.DEFAULT_GROWTH
    private var currentWeight = NewUserActivity.DEFAULT_WEIGHT
    private var currentAge = NewUserActivity.DEFAULT_AGE
    private var newValue = 0f

    init {
        App.component.inject(this)
    }

    fun refresh() {
        updateValue()
    }

    fun addUser() {}

    fun addMainUser() {
        compositeDisposable.add(
            dbInteractor.insertUser(
                UserData(
                    id = null,
                    name = null,
                    isMain = true,
                    isMale = segmentsSelectedPosition == 1,
                    growth = currentGrowth,
                    weight = currentWeight,
                    age = currentAge,
                    firstDayPtr = null,
                    regDate = Date().toString()
                )
            ).observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    successLiveData.postValue(true)
                }
                .subscribe({
                    Log.d("new_user", "success insert")
                }){
                    Log.d("new_user", "$it")
                    // log
                }
        )
    }

    private fun updateValue() {
        newValue = (30 * currentWeight).toFloat() / 1000
        if (currentAge < 14f) {
            newValue /= (currentAge / 15f)
        }

        if (newValue > 5f)
            newValue = 5f

        valueLiveData.postValue(newValue)
    }

    fun bindSegments(checkedId: Int) {
        segmentsSelectedPosition = when (checkedId) {
            R.id.male -> 1
            else -> 0
        }
        updateValue()
    }

    fun newGrowth(v: Int) {
        currentGrowth = v
        updateValue()
    }

    fun newWeight(v: Int) {
        currentWeight = v
        updateValue()
    }

    fun newAge(v: Int) {
        currentAge = v
        updateValue()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}