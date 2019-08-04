package android.spb.waterbalance.presentation.splash_activity

import android.content.Intent
import android.os.Bundle
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivity
import android.spb.waterbalance.presentation.wizard_activity.WizardActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class SplashScreenActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProviders.of(this)[SplashViewModel::class.java]

        viewModel.checkForWizrd()

        viewModel.userSingleEvent.observe(this, Observer {
            if (it == null) {
                startActivity(WizardActivity.instance(baseContext))
            } else {
                startActivity(StartPageActivity.instance(baseContext).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
            finish()
            overridePendingTransition(0,0)
        })
    }
}