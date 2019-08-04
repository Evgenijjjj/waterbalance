package android.spb.waterbalance.base

import android.annotation.SuppressLint
import android.spb.waterbalance.R
import android.spb.waterbalance.database.data.DrinkData
import android.spb.waterbalance.database.data.getDrawableRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_drink_stat.view.*

class DrinksAdapter: BaseSearchAdapter<DrinkData>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<DrinkData> {
        return DrinkViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_drink_stat, parent, false))
    }

    private class DrinkViewHolder(v: View): BaseSearchAdapter.BaseViewHolder<DrinkData>(v) {
        @SuppressLint("SetTextI18n")
        override fun bind(model: DrinkData) {
            with(itemView) {
                iv_obj.setImageDrawable(context.getDrawable(model.getDrawableRes()))
                tv_name.text = model.name
                tv_amount.text = """${(model.amount / 1000f).toDecimalsString(2)} л"""
            }
        }
    }
}