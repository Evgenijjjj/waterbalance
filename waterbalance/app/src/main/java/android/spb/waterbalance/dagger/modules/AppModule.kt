package android.spb.waterbalance.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
class AppModule(val appContext: Context) {
    @Provides
    fun provideContext() = appContext

    @Provides
    fun provideAppPreferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
        context)
}