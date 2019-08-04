package android.spb.waterbalance.presentation.startpage_activity

import android.content.Context
import android.content.Intent
import android.spb.waterbalance.R
import android.spb.waterbalance.base.BaseActivity
import android.spb.waterbalance.presentation.startpage_activity.fragement_select_drink_amount.SelectAmountDialog
import android.spb.waterbalance.presentation.startpage_activity.fragment_chart.ChartFragment
import android.spb.waterbalance.presentation.startpage_activity.fragment_drinks_stat.DrinksStatisticFragment
import android.spb.waterbalance.presentation.startpage_activity.fragment_today.TodayFragmentDialog
import android.spb.waterbalance.presentation.todo_activity.ToDoActivity
import android.spb.waterbalance.presentation.wizard_activity.WizardActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_start_page.*

class StartPageActivity : BaseActivity() {

    companion object {
        fun log(m: Any) { Log.d("start_page_log", m.toString()) }

        fun instance(ctx: Context) =
            Intent(ctx, StartPageActivity::class.java)
    }

    private lateinit var viewModel: StartPageActivityViewModel

    override val layoutId = R.layout.activity_start_page

    private val todayDialog by lazy { TodayFragmentDialog() }

    override fun onCreated() {
        viewModel = ViewModelProviders.of(this)[StartPageActivityViewModel::class.java]

        viewModel.wizardSingleLiveEvent.observe(this, Observer {
            startActivity(WizardActivity.instance(baseContext))
            fastFinish()
        })

        viewModel.userLiveData.observe(this, Observer {

        })

        viewModel.titleVisibilityLiveData.observe(this, Observer {
            _profile.apply {
//                visibility = if (it) View.VISIBLE else View.INVISIBLE
                text = if (it) getString(R.string.profile) else "Сегодня"
            }
        })

        viewModel.drinkSelectedLiveData.observe(this, Observer {
            SelectAmountDialog.instance(it).show(supportFragmentManager, "")
        })

        viewModel.todayStatisticLiveData.observe(this, Observer {
            tv_day_active.text = "Активынй период (дней): ${it.second.second}"
        })

        viewModel.waterBalanceRecordLiveData.observe(this, Observer {
            tv_days_record.text = "$it"
        })

        viewModel.todoLiveData.observe(this, Observer {
            Toast.makeText(baseContext, "coming soon...", Toast.LENGTH_LONG).show()
//            startActivity(Intent(this.baseContext, ToDoActivity::class.java))
        })

        showBottomDialog()

        btn_show.setOnClickListener {
            showBottomDialog()
        }

        btn_settings.setOnClickListener {
            viewModel.todoLiveData.call()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chart_container, ChartFragment(), ChartFragment::class.java.name)
            .replace(R.id.drinks_stat_container, DrinksStatisticFragment(), DrinksStatisticFragment::class.java.name)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun showBottomDialog() {
        if (!todayDialog.isAdded)
            todayDialog.show(supportFragmentManager, TodayFragmentDialog::class.java.name)
    }

}