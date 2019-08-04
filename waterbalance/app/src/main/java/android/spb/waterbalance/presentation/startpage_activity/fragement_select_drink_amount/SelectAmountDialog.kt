package android.spb.waterbalance.presentation.startpage_activity.fragement_select_drink_amount

import android.annotation.SuppressLint
import android.os.Bundle
import android.spb.waterbalance.R
import android.spb.waterbalance.database.data.DrinkData
import android.spb.waterbalance.database.data.UserData
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivity
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivityViewModel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.dialog_select_amount.*

class SelectAmountDialog: DialogFragment() {

    companion object {
        private const val DRINK_EXTRA = "drink_extra"

        val instance = {
            data: DrinkData ->
            SelectAmountDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(DRINK_EXTRA, data)
                }
            }
        }
    }

    private val drink by lazy {
        arguments!!.getParcelable<DrinkData>(DRINK_EXTRA)
    }

    private val navViewModel by lazy {
        ViewModelProviders.of(activity as StartPageActivity)[StartPageActivityViewModel::class.java]
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this)[SelectAmountViewModel::class.java]
    }

    private val crollerProgressLiveData = MutableLiveData<Int>().apply { postValue(1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyleThemeLight)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_select_amount, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_close.setOnClickListener { dismiss() }

        with(drink) {
            tv_title.text = name
            tv_formula.text = "1 мл = $factor"
        }

        croller.setOnProgressChangedListener {
            crollerProgressLiveData.postValue(it)
        }

        crollerProgressLiveData.observe(this, Observer {
            val amount = it * 10
            drink.amount = amount
            tv_calculated_amount.text = "${String.format("%.0f", amount * drink.factor)} мл"
            tv_value.text = "$amount мл"
        })

        btn_add.setOnClickListener {
            val user = navViewModel.userLiveData.value
            if (user == null) {
                navViewModel.refresh()
                Log.d("addNewDrinkToUser", "user : $user")
                return@setOnClickListener
            }

            viewModel.addDrinkToUser(user, drink)
        }

        viewModel.successSingleLiveEvent.observe(this, Observer {
            navViewModel.refresh()
            dismiss()
        })
    }
}