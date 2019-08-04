package android.spb.waterbalance.presentation.wizard_activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.spb.waterbalance.R
import android.spb.waterbalance.app.App
import android.spb.waterbalance.base.BaseActivity
import android.spb.waterbalance.presentation.new_user_activity.NewUserActivity
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_wizard.*

class WizardActivity: BaseActivity() {

    companion object {
        val instance = {
            ctx: Context ->
            Intent(ctx, WizardActivity::class.java)
        }
    }

    override val layoutId = R.layout.activity_wizard

    private val page = MutableLiveData<Int>().apply { value = 0 }

    override fun onCreated() {
        pager.apply {
            adapter = SectionsPageAdapter(supportFragmentManager)

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {  }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {  }

                override fun onPageSelected(position: Int) {
                    page.value = position
                }
            })
        }

        page.observe(this, Observer {
            pager.currentItem = it
            selectDot(it)
        })

        btn_close.setOnClickListener { showAddUserActivity() }

        btn_next.setOnClickListener {
            val currentPage = page.value ?: 0
            if (currentPage >= 2) {
                btn_close.performClick()
            } else {
                page.postValue(currentPage + 1)
            }
        }
    }


    private fun showAddUserActivity() {
        startActivity(NewUserActivity.instance(baseContext, NewUserActivity.INIT_TYPE))
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
}

class SectionsPageAdapter(fm: FragmentManager, private val itemCount: Int = 3): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = PlaceHolderFragment.getInstance(position)

    override fun getCount() = itemCount
}