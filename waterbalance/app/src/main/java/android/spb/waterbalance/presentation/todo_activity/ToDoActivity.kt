package android.spb.waterbalance.presentation.todo_activity

import android.spb.waterbalance.R
import android.spb.waterbalance.base.BaseActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_to_do.*

class ToDoActivity: BaseActivity() {
    override val layoutId = R.layout.activity_to_do
    override fun onCreated() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }
}