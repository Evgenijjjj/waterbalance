package android.spb.waterbalance.presentation.new_user_activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.spb.waterbalance.R
import android.spb.waterbalance.base.BaseActivity
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_new_user.*


class NewUserActivity: BaseActivity() {

    companion object {
        const val DEFAULT_WEIGHT = 55
        const val DEFAULT_AGE = 27
        const val DEFAULT_GROWTH = 160

        private const val TYPE_ETRA = "new_user_type_etra"

        const val INIT_TYPE = 0

        val instance = {
            ctx: Context, type: Int ->
            Intent(ctx, NewUserActivity::class.java).apply {
                putExtra(TYPE_ETRA, type)
            }
        }
    }

    override val layoutId = R.layout.activity_new_user

    private lateinit var viewModel: NewUserActivityViewModel
    private var type = 0


    @SuppressLint("SetTextI18n")
    override fun onCreated() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
        viewModel = ViewModelProviders.of(this)[NewUserActivityViewModel::class.java]
        type = intent.getIntExtra(TYPE_ETRA, -1)
        if (type < 0) {
            throw IllegalArgumentException("Wrong activity type: $type, please use NewUserActivity.instance")
        }

        bindSegments()
        bindConfirmBtn()
        bindSeekBars()

        viewModel.valueLiveData.observe(this, Observer {
            tv_value.text = "${String.format("%.2f", it)} л"
        })

        viewModel.successLiveData.observe(this, Observer {
            when (type) {
                INIT_TYPE -> {
                    startActivity(StartPageActivity.instance(this).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    finishAffinity()
                }
            }
        })

        viewModel.refresh()
    }

    private fun bindSegments() {
        segments.check(R.id.female)
        segments.setOnCheckedChangeListener { _, checkedId ->
            viewModel.bindSegments(checkedId)
        }
    }

    private fun bindConfirmBtn() {
        btn_confirm.setOnClickListener {
            it.isClickable = false
            it.alpha = 0.3f

            when (type) {
                INIT_TYPE -> viewModel.addMainUser()
            }
        }
    }

    private fun bindSeekBars() {
        val minGrow = 1
        val maxGrow = 240

        seekbar_growth.apply {
            position = (DEFAULT_GROWTH.toFloat()/maxGrow.toFloat())
            bubbleText = DEFAULT_GROWTH.toString()

            positionListener = {
                val value = minGrow + ((maxGrow - minGrow) * it).toInt()
                bubbleText = "$value"
                viewModel.newGrowth(value)
            }
        }


        val minWeight = 1
        val maxWeight = 140

        seekbar_weight.apply {
            position = (DEFAULT_WEIGHT.toFloat()/maxWeight.toFloat())
            positionListener = {
                val value = minWeight + ((maxWeight - minWeight) * it).toInt()
                bubbleText = "$value"
                viewModel.newWeight(value)
            }
        }

        val minAge = 0
        val maxAge = 100

        seekbar_age.apply {
            position = (DEFAULT_AGE.toFloat()/maxAge.toFloat())
            positionListener = {
                val value = minAge + ((maxAge - minAge) * it).toInt()
                bubbleText = "$value"
                viewModel.newAge(value)
            }
        }
    }
}