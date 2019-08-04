package android.spb.waterbalance.app

import android.app.Application
import android.content.ComponentCallbacks2
import android.spb.waterbalance.dagger.components.AppComponent
import android.spb.waterbalance.dagger.components.DaggerAppComponent
import android.spb.waterbalance.dagger.modules.AppModule
import android.spb.waterbalance.dagger.modules.DatabaseModule
import android.spb.waterbalance.dagger.modules.InteractorsModule

class App: Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = buildComponent()
        component.inject(this)
    }

    private fun buildComponent() = DaggerAppComponent.builder()
        .appModule(AppModule(applicationContext))
        .databaseModule(DatabaseModule())
        .interactorsModule(InteractorsModule())
        .build()

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */
            }

            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */
            }

            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */
            }

            else -> {
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
            }
        }
    }
}