package android.spb.waterbalance.presentation.startpage_activity.fragment_today.fragment_drinks_placeholder

import android.os.Bundle
import android.spb.waterbalance.R
import android.spb.waterbalance.database.data.DrinkData
import android.spb.waterbalance.database.data.getDrawableRes
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivity
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivityViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_drinks_picker.*
import kotlinx.android.synthetic.main.item_drink.view.*
import java.lang.IllegalArgumentException

class DrinksPlaceHolder: Fragment() {

    companion object {
        const val INSTANCE_1 = 0
        const val INSTANCE_2 = 1
        const val INSTANCE_3 = 2

        private const val TYPE_EXTRA = "type_extra"

        val instance = {
            instanceType: Int ->
            if (instanceType !in 0..2) throw IllegalArgumentException("Parameter instanceType must be in 0..2")
            DrinksPlaceHolder().apply {
                arguments = Bundle().apply {
                    putInt(TYPE_EXTRA, instanceType)
                }
            }
        }

    }

    private val navViewModel by lazy {
        ViewModelProviders.of(activity as StartPageActivity)[StartPageActivityViewModel::class.java]
    }

    private val type by lazy {
        arguments!!.getInt(TYPE_EXTRA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_drinks_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: добавить названия в ресурсы
        when (type) {
            INSTANCE_1 -> {
                bindDrink(DrinkData(name = getString(R.string.water)), drink_1)
                bindDrink(DrinkData(name = getString(R.string.water_s), factor = 0.8f), drink_2)
                bindDrink(DrinkData(name = getString(R.string.black_coffe), factor = 0.3f), drink_3)
                bindDrink(DrinkData(name = getString(R.string.black_tea),factor =  0.8f), drink_4)
            }
            INSTANCE_2 -> {
                bindDrink(DrinkData(name = getString(R.string.strong_alco), factor = -1.8f), drink_1)
                bindDrink(DrinkData(name = getString(R.string.milk), factor = 0f), drink_2)
                bindDrink(DrinkData(name = getString(R.string.juice), factor = 0f), drink_3)
                bindDrink(DrinkData(name = "Энергетик", factor = 0.8f), drink_4)
            }
            INSTANCE_3 -> {
                bindDrink(DrinkData(name = getString(R.string.wine), factor = 0.1f), drink_1)
                bindDrink(DrinkData(name = getString(R.string.soda), factor = 0.2f), drink_2)
                bindDrink(DrinkData(name = getString(R.string.yogurt), factor = 0f), drink_3)
                bindDrink(DrinkData(name = getString(R.string.add)), drink_4)
            }
        }
    }

    private fun bindDrink(data: DrinkData, view: View) {
        with(view) {
            iv_icon.setImageDrawable(resources.getDrawable(data.getDrawableRes()))
            tv_name.text = data.name

            view.setOnClickListener { navViewModel.selectDrink(data) }
            view.setOnLongClickListener { navViewModel.selectDrink(data); true }
        }
    }
}