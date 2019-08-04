package android.spb.waterbalance.presentation.startpage_activity.fragment_chart

import android.os.Bundle
import android.spb.waterbalance.R
import android.spb.waterbalance.database.data.DailyStatisticData
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivity
import android.spb.waterbalance.presentation.startpage_activity.StartPageActivityViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_chart.*

class ChartFragment : Fragment() {
    private val navViewModel by lazy {
        ViewModelProviders.of(activity as StartPageActivity)[StartPageActivityViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navViewModel.chartStatisticLiveData.observe(this, Observer {
            tv_date_range.text = it.chartDateRange
            if (it.listDays.size == 7) {
                printChart(it.listDays)
            } else {
                // log / throw
            }

        })
    }

    private fun printChart(data: List<ChartDay>) {
        val labels = data.map { it.dayTitle }
        val yVals = ArrayList<BarEntry>()
        val limit = 5000f

        for (day in data.withIndex()) {
            val current = day.value.dailyStatisticData?.total ?: 0f
            yVals.add(BarEntry(day.index.toFloat(), floatArrayOf(current, limit - current)))
        }

        val dataSet = BarDataSet(yVals, "")
        dataSet.setColors(ContextCompat.getColor(context!!, R.color.chartFilled),
            ContextCompat.getColor(context!!, R.color.chartNotFilled))

        val barData = BarData(dataSet).apply {
            setValueTextSize(0f)
        }

        bar_chart.apply {
            setData(barData)

            legend.isEnabled = false
            description.isEnabled = false
            setScaleEnabled(false)

            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                isEnabled = false
            }

            axisRight.apply {
                setDrawAxisLine(false)
                valueFormatter = IAxisValueFormatter { value, _ -> "${(value/1000).toInt()} л" }
                gridColor = ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
                gridLineWidth = 0.5f
            }

            with(xAxis) {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textSize = 10f
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(labels)
                granularity = 1f
            }

            setOnChartValueSelectedListener(object: OnChartValueSelectedListener {
                override fun onNothingSelected() {
                }

                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    navViewModel.todoLiveData.call()
                    highlightValue(null)
                }
            })

            invalidate()
        }
    }
}

data class ChartWeek(
    val listDays: List<ChartDay>,
    val chartDateRange: String
)

data class ChartDay(
    val dailyStatisticData: DailyStatisticData?,
    val dayOfWeek: Int,
    val dayTitle: String
)