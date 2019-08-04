package android.spb.waterbalance.presentation.wizard_activity

import android.os.Bundle
import android.spb.waterbalance.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_wizard.view.*
import java.lang.IllegalArgumentException

class PlaceHolderFragment: Fragment() {
    companion object {
        const val PAGE_KEY = "page_key"

        fun getInstance(page: Int): PlaceHolderFragment = PlaceHolderFragment()
            .apply { arguments = Bundle().apply { putInt(PAGE_KEY, page) } }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_wizard, container, false)

        val index = arguments?.getInt(PAGE_KEY) ?: return null

        with(v) {
            title.text = when (index) {
                0 -> getString(R.string.title1)
                1 -> getString(R.string.title2)
                2 -> getString(R.string.title3)
                else -> throw IllegalArgumentException("Index must be in 0..2, but now $index")
            }

            subtitle.text = when (index) {
                0 -> getString(R.string.subtitle1)
                1 -> getString(R.string.subtitle2)
                2 -> getString(R.string.subtitle2)
                else -> ""
            }

            iv_content.setImageDrawable(context.resources.getDrawable(when (index) {
                0 -> R.drawable.water
                1 -> R.drawable.glass
                2 -> R.drawable.champagne
                else -> throw IllegalArgumentException("Index must be in 0..2, but now $index")
            }))
        }

        return v
    }

}