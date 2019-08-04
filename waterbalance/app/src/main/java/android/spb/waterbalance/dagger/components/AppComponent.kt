package android.spb.waterbalance.dagger.components

import android.spb.waterbalance.app.App
import android.spb.waterbalance.dagger.modules.AppModule
import android.spb.waterbalance.dagger.modules.DatabaseModule
import android.spb.waterbalance.dagger.modules.InteractorsModule
import android.spb.waterbalance.presentation.new_user_activity.NewUserActivityViewModel
import android.spb.waterbalance.presentation.splash_activity.SplashViewModel
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivityViewModel
import android.spb.waterbalance.presentation.startpage_activity.fragement_select_drink_amount.SelectAmountViewModel
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        DatabaseModule::class,
        InteractorsModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(obj: App)

    fun inject(obj: StartPageActivityViewModel)

    fun inject(obj: NewUserActivityViewModel)

    fun inject(obj: SelectAmountViewModel)

    fun inject(obj: SplashViewModel)
}