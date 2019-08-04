package android.spb.waterbalance.presentation.startpage_activity.fragment_today

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.spb.waterbalance.R
import android.spb.waterbalance.base.toDecimalsString
import android.spb.waterbalance.database.data.DailyStatisticData
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivity
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivityViewModel
import android.spb.waterbalance.presentation.startpage_activity.fragment_today.fragment_drinks_placeholder.DrinksPlaceHolder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_today.*

class TodayFragmentDialog: BottomSheetDialogFragment() {


    private val navViewModel by lazy {
        ViewModelProviders.of(activity as StartPageActivity)[StartPageActivityViewModel::class.java]
    }

    private val page = MutableLiveData<Int>().apply { value = 0 }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        d.setOnShowListener {
            val layout = (dialog as BottomSheetDialog)
                .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            BottomSheetBehavior.from(layout).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
                setHideable(true)

                setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(p0: View, p1: Float) {}

                    override fun onStateChanged(p0: View, p1: Int) {
                        if (p1 == BottomSheetBehavior.STATE_HIDDEN)
                            this@TodayFragmentDialog.dismiss()
                    }
                })
            }
        }

        d.window?.clearFlags(FLAG_DIM_BEHIND)
        return d
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_profile.setOnClickListener { this.dismiss() }


        view_pager.apply {
            adapter = DrinksSectionPageAdapter(childFragmentManager)

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {  }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {  }

                override fun onPageSelected(position: Int) {
                    page.value = position
                }
            })
        }

        page.observe(this, Observer {
            selectDot(it)
        })

        navViewModel.todayStatisticLiveData.observe(this, Observer {
            tv_day.text = "День ${it.second.first}"
            tv_count.text = "${(it.first.total/1000).toDecimalsString()} из ${(it.first.dailyNorm/1000).toDecimalsString()} л"
            tv_percent.text = "${(it.first.total / it.first.dailyNorm * 100).toInt()} %"
            bindProgress(it.first)
        })

        navViewModel.refresh()
        navViewModel.titleVisibilityLiveData.postValue(false)
    }

    private fun selectDot(index: Int) {
        for (i in 0..2) (wizard_dots.getChildAt(i) as ImageView).apply {
            alpha = 0.5f
            scaleX = 0.6f
            scaleY = 0.6f
        }

        (wizard_dots.getChildAt(index) as ImageView).apply {
            alpha = 1f
            scaleX = 1f
            scaleY = 1f
        }
    }

    private fun bindProgress(ds: DailyStatisticData) {
        val percent = ((ds.total / ds.dailyNorm) * 360).toInt()
        wheelprogress.setPercentage(percent)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        navViewModel.titleVisibilityLiveData.postValue(true)
    }

}

class DrinksSectionPageAdapter(fm: FragmentManager, private val itemCount: Int = 3): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = DrinksPlaceHolder.instance(position)

    override fun getCount() = itemCount
}