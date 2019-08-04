package android.spb.waterbalance.presentation.startpage_activity.fragment_drinks_stat

import android.os.Bundle
import android.spb.waterbalance.R
import android.spb.waterbalance.base.DrinksAdapter
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivity
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivityViewModel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_drinks_stat.*

class DrinksStatisticFragment: Fragment() {

    private val navViewModel by lazy {
        ViewModelProviders.of(activity as StartPageActivity)[StartPageActivityViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_drinks_stat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DrinksAdapter()
        recycler.layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayout.HORIZONTAL, false)
        recycler.adapter = adapter

        navViewModel.drinksStatLiveData.observe(this, Observer {
            Log.d("DrinksStatisticFragment", "add to adapter: $it")
            adapter.clear()
            adapter.addList(it)
        })
    }
}