package android.spb.waterbalance.presentation.splash_activity

import android.spb.waterbalance.app.App
import android.spb.waterbalance.base.SingleLiveEvent
import android.spb.waterbalance.database.data.UserData
import android.spb.waterbalance.interactors.DatabaseInteractor
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class SplashViewModel: ViewModel() {

    private var disposable: Disposable? = null

    @Inject
    lateinit var dbInteractor: DatabaseInteractor

    val userSingleEvent = SingleLiveEvent<UserData?>()

    init {
        App.component.inject(this)
    }

    fun checkForWizrd() {
        disposable = dbInteractor.getMainUser()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                if (userSingleEvent.value == null)
                    userSingleEvent.postValue(null)
            }
            .subscribe({
                userSingleEvent.postValue(it)
            }) {userSingleEvent.postValue(null)}
    }


    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}