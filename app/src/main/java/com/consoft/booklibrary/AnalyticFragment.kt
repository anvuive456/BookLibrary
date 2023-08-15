package com.consoft.booklibrary

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.consoft.booklibrary.adapter.AnalyticFilterAdapter
import com.consoft.booklibrary.adapter.AnalyticFilterAdapter.Filter
import com.consoft.booklibrary.databinding.FragmentAnalyticBinding
import com.consoft.booklibrary.db.AppDatabase
import com.consoft.booklibrary.model.TicketStatus
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import java.time.LocalDate
import java.util.Calendar


class AnalyticFragment : Fragment() {
  lateinit var binding: FragmentAnalyticBinding
  lateinit var db: AppDatabase

  var filter: Filter = Filter.day

  //khởi tạo bắt đầu từ ngày đầu của tháng
  var startDate: LocalDate = LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1)

  //và ngày hiện tại
  var endDate: LocalDate = LocalDate.now()

  var selectedQuarterId = R.id.quarter_1
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    db = AppDatabase.getDatabase(requireContext())
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding =
      FragmentAnalyticBinding.inflate(LayoutInflater.from(requireContext()), container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initOverview()
    initFilters()
    loadBarChart()
    initQuarterRadio()
  }

  fun initOverview() {
    //lấy số lượng sách cho mượn
    val borrowCount = db.ticketDao().findMany().count { it.ticket.status == TicketStatus.Borrowing }
    //lấy số lượng sách đã trả
    val returnedCount =
      db.ticketDao().findMany().count { it.ticket.status == TicketStatus.Returned }
    //số lượng sách hết hạn
    val overDueCount = db.ticketDao().findMany().count { it.ticket.status == TicketStatus.OverDue }
    val entries = mutableListOf<PieEntry>()
    entries.add(PieEntry(borrowCount.toFloat(), "Đang mượn"))
    entries.add(PieEntry(returnedCount.toFloat(), "Đã trả"))
    entries.add(PieEntry(overDueCount.toFloat(), "Hết hạn"))


    val dataSet = PieDataSet(entries, "")

    dataSet.valueFormatter = PercentFormatter(binding.chartOverview)
    dataSet.valueTextSize = 12f
    binding.chartOverview.setUsePercentValues(true)
    binding.chartOverview.data = PieData(dataSet)
    binding.chartOverview.legend.isEnabled = true
    binding.chartOverview.description.isEnabled = false
    dataSet.colors = (ColorTemplate.COLORFUL_COLORS.asList())
    binding.chartOverview.setDrawEntryLabels(false)

    // Move Legend to the right
    binding.chartOverview.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT;
    binding.chartOverview.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM;

    binding.chartOverview.setOnClickListener {
      if (binding.chartOverview.isUsePercentValuesEnabled) {
        binding.chartOverview.setUsePercentValues(false)
      } else {
        binding.chartOverview.setUsePercentValues(true)
      }
    }
    binding.chartOverview.invalidate()
  }

  fun initFilters() {
    val adapter = AnalyticFilterAdapter(Filter.values(), onClick = {
      filter = it

//      if (it == Filter.quarter) {
//        binding.rgQuarter.visibility = View.VISIBLE
//      } else {
//        binding.rgQuarter.visibility = View.GONE
//      }

//      loadByFilter()
      loadBarChart()
    })

    binding.rcFilter.layoutManager =
      LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    binding.rcFilter.adapter = adapter
  }

  fun loadByFilter() {
    when (filter) {
      Filter.day -> {
        startDate = LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1)
        endDate = LocalDate.now()
      }

      Filter.month -> {
        startDate = LocalDate.of(LocalDate.now().year, 1, 1)
        endDate = LocalDate.now()
      }

      Filter.quarter -> {
        when (selectedQuarterId) {
          R.id.quarter_1 -> {
            startDate = LocalDate.of(LocalDate.now().year, 1, 1)
            endDate = LocalDate.of(LocalDate.now().year, 3, 31)
          }

          R.id.quarter_2 -> {
            startDate = LocalDate.of(LocalDate.now().year, 4, 1)
            endDate = LocalDate.of(LocalDate.now().year, 6, 30)
          }

          R.id.quarter_3 -> {
            startDate = LocalDate.of(LocalDate.now().year, 7, 1)
            endDate = LocalDate.of(LocalDate.now().year, 9, 30)
          }

          R.id.quarter_4 -> {
            startDate = LocalDate.of(LocalDate.now().year, 10, 1)
            endDate = LocalDate.of(LocalDate.now().year, 12, 31)
          }
        }

      }

      Filter.year -> {
        //lấy 3 năm gần nhất
        startDate = LocalDate.of(LocalDate.now().year - 3, 1, 1)
        endDate = LocalDate.now()
      }
    }
  }

  fun initQuarterRadio() {
    binding.rgQuarter.setOnCheckedChangeListener { group, checkedId ->
      selectedQuarterId = checkedId
    }
  }

  //tính số ngày trong tháng
  fun daysInMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1) // Tháng trong Calendar bắt đầu từ 0
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
  }


  //tính tháng bắt đầu và kết thúc theo 1 quý
  fun getQuarterStartAndEndMonths(quarter: Int): Pair<Int, Int> {
    val startMonth = (quarter - 1) * 3 + 1
    val endMonth = startMonth + 2
    return Pair(startMonth, endMonth)
  }

  fun loadBarChart() {
    //danh sách các nhóm bar trong chart
    //1 group là 3 cột theo trạng thái của sách
    // theo ngày thì có 28-31 groups
    //theo tháng thì có
    var groups = mutableListOf<BarDataSet>()

    val borrowingCounts = arrayListOf<Int>()
    val overDueCounts = arrayListOf<Int>()
    val returnedCounts = arrayListOf<Int>()

    val labels = arrayListOf<String>()

    //duyệt và lấy theo trạng thái
    when (filter) {
      Filter.day -> {
        //số lượng ngày từ đầu tháng đến hiện tại
//        val days = daysInMonth(LocalDate.now().year, LocalDate.now().monthValue)
        for (day in 1..LocalDate.now().dayOfMonth) {
          val group = ArrayList<BarEntry>()
          val dayd = LocalDate.now().withDayOfMonth(day).toString()
          Log.d("dayd", dayd)
          val tickets = db.ticketDao()
            .getTicketsByDay(
              dayd
            )

          Log.w("tickets", tickets.toString())

          val borrowingCount = tickets.count { it.status == TicketStatus.Borrowing }
          val overDueCount = tickets.count { it.status == TicketStatus.OverDue }
          val returnedCount = tickets.count { it.status == TicketStatus.Returned }

          borrowingCounts.add(borrowingCount)
          overDueCounts.add(overDueCount)
          returnedCounts.add(returnedCount)
          labels.add("Ngày $day")

        }
      }

      Filter.month -> {
        //lấy số lượng tháng từ đầu năm tới hiện tại
        val months = LocalDate.now().monthValue
        for (month in 1..months) {
          val group = ArrayList<BarEntry>()
          val tickets =
            db.ticketDao().getTicketsByMonth(if (month < 10) "0$month" else month.toString())
          Log.d("month", month.toString())

          Log.w("tickets", tickets.toString())

          val borrowingCount = tickets.count { it.status == TicketStatus.Borrowing }
          val overDueCount = tickets.count { it.status == TicketStatus.OverDue }
          val returnedCount = tickets.count { it.status == TicketStatus.Returned }

          borrowingCounts.add(borrowingCount)
          overDueCounts.add(overDueCount)
          returnedCounts.add(returnedCount)

          labels.add("Tháng $month")

        }
      }

      Filter.quarter -> {
        //duyệt theo 4 quý
        for (quarter in 1..4) {
          //lấy tháng bắt đầu và tháng kết thúc theo quý
          val (startMonth, endMonth) = getQuarterStartAndEndMonths(quarter)
          val group = ArrayList<BarEntry>()
          val tickets = db.ticketDao().getTicketsByQuarter(
            if (startMonth < 10) "0$startMonth" else startMonth.toString(),
            if (endMonth < 10) "0$endMonth" else endMonth.toString(),
            LocalDate.now().year.toString()
          )


          val borrowingCount = tickets.count { it.status == TicketStatus.Borrowing }
          val overDueCount = tickets.count { it.status == TicketStatus.OverDue }
          val returnedCount = tickets.count { it.status == TicketStatus.Returned }

          borrowingCounts.add(borrowingCount)
          overDueCounts.add(overDueCount)
          returnedCounts.add(returnedCount)

          labels.add("Quý $quarter")
        }
      }

      Filter.year -> {
        //lấy số lượng năm từ số lượng năm đến hiện tại
        val years = 3
        for (yearCount in 0..years) {
          val year = LocalDate.now().year - yearCount
          val group = ArrayList<BarEntry>()
          val tickets = db.ticketDao().getTicketsByYear(year.toString())

          val borrowingCount = tickets.count { it.status == TicketStatus.Borrowing }
          val overDueCount = tickets.count { it.status == TicketStatus.OverDue }
          val returnedCount = tickets.count { it.status == TicketStatus.Returned }

          borrowingCounts.add(borrowingCount)
          overDueCounts.add(overDueCount)
          returnedCounts.add(returnedCount)

          labels.add("Năm $year")
        }
      }
    }

    val bar1 = borrowingCounts.mapIndexed { index, i -> BarEntry(index.toFloat(), i.toFloat()) }
    val bar2 = overDueCounts.mapIndexed { index, i -> BarEntry(index.toFloat(), i.toFloat()) }
    val bar3 = returnedCounts.mapIndexed { index, i -> BarEntry(index.toFloat(), i.toFloat()) }

    val set1 = BarDataSet(bar1, "Đang mượn")
    set1.color = Color.BLUE
    val set2 = BarDataSet(bar2, "Quá hạn")
    set2.color = Color.MAGENTA
    val set3 = BarDataSet(bar3, "Đã trả")
    set3.color = Color.GREEN

    val barData = BarData(set1,set2,set3)
    binding.chartByDate.data = barData

    // Đặt vị trí của nhóm cột
    val groupSpace = 0.4f // Space between groups
    val barSpace = 0.06f // Space between individual bars within a group
    val barWidth = 0.14f // Width of each bar


    // Chiều rộng của mỗi cột
    barData.barWidth = barWidth

    binding.chartByDate.groupBars(
      0f,
      groupSpace,
      barSpace
    )
//    binding.chartByDate.isDragEnabled = true
    binding.chartByDate.description.isEnabled = false

    //custom label của trục hoành
    binding.chartByDate.xAxis.valueFormatter =
      IndexAxisValueFormatter(labels)
    binding.chartByDate.xAxis.position = XAxis.XAxisPosition.BOTTOM
    binding.chartByDate.xAxis.isGranularityEnabled = true
    binding.chartByDate.xAxis.setCenterAxisLabels(true)
    binding.chartByDate.xAxis.granularity = 1f
    binding.chartByDate.xAxis.axisMinimum = 0f
    binding.chartByDate.xAxis.axisMaximum = barData.getGroupWidth(groupSpace, barSpace)* labels.size


    //
    binding.chartByDate.axisLeft.axisMinimum = 0f
    binding.chartByDate.axisLeft.setDrawGridLines(false)
    binding.chartByDate.invalidate()
  }

}