package com.consoft.booklibrary

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.consoft.booklibrary.adapter.BookAdapter
import com.consoft.booklibrary.adapter.OverviewAdapter
import com.consoft.booklibrary.adapter.TicketAdapter
import com.consoft.booklibrary.databinding.FragmentBookBinding
import com.consoft.booklibrary.db.AppDatabase
import com.consoft.booklibrary.model.BookWithMembers
import com.consoft.booklibrary.model.Overview
import com.consoft.booklibrary.model.Ticket
import com.consoft.booklibrary.model.TicketStatus
import com.consoft.booklibrary.model.TicketWithBookAndMember
import java.time.LocalDate

class BookFragment : Fragment() {
  lateinit var binding: FragmentBookBinding
  lateinit var bookAdapter: BookAdapter
  lateinit var ticketAdapter: TicketAdapter
  lateinit var overviewAdapter: OverviewAdapter
  lateinit var db: AppDatabase
  var books = mutableListOf<BookWithMembers>()
  var tickets = mutableListOf<TicketWithBookAndMember>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    db = AppDatabase.getDatabase(requireContext())
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentBookBinding.inflate(inflater, container, false)



    initViews()
    initBooks()
    initTickets()
    initOverviews()
    loadBooks()
    loadTickets()

    return binding.root
  }

  fun initBooks() {

    bookAdapter = BookAdapter(books, onCLick = {
      val intent = Intent(requireContext(), DetailBookActivity::class.java)
      intent.putExtra("book", it.book)
      updateBookLauncher.launch(intent)
    })

    val layoutManager = object : LinearLayoutManager(requireContext()) {
      //Khoá không cho scroll được vì đã có scroll ở view root (scrollview)
      override fun canScrollVertically(): Boolean {
        return false
      }
    }
    binding.rcBook.layoutManager = layoutManager
    binding.rcBook.adapter = bookAdapter
  }

  fun initTickets() {

    ticketAdapter = TicketAdapter(tickets, {
      val intent = Intent(requireContext(), AddTicketActivity::class.java)
      intent.putExtra("ticket", it.ticket)
      updateTicketLauncher.launch(intent)
    })

    val layoutManager = object : LinearLayoutManager(requireContext()) {
      //Khoá không cho scroll được vì đã có scroll ở view root (scrollview)
      override fun canScrollVertically(): Boolean {
        return false
      }
    }
    binding.rcTicket.layoutManager = layoutManager
    binding.rcTicket.adapter = ticketAdapter
  }

  //lắng nghe update và load lại list
  val updateTicketLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        loadTickets()
      }
    }

  //lắng nghe update và load lại list
  val updateBookLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        loadBooks()
        loadTickets()
      }
    }

  ///Khởi tạo tổng quan
  fun initOverviews() {
    //lấy số lượng sách
    val bookCount = db.bookDao().getBooks().count()
    //lấy số lượng phiếu mượn
    val ticketCount = db.ticketDao().findMany().count()
    //lấy số lượng thành viên
    val memberCount = db.memberDao().findMany().count()
    //sách sắp đến hạn trả
    val dueCount = db.ticketDao().findMany().count {
      it.ticket.dueDate.compareTo(LocalDate.now()) == 1
              && it.ticket.status != TicketStatus.Returned
    }
    //sách quá hạn
    val overDueCount = db.ticketDao().findMany().count {
      it.ticket.status == TicketStatus.OverDue
    }
    //tính tổng tiền từ phiếu mượn
    val moneyCount = db.ticketDao().findMany().sumOf { it.book.price }

    Log.e("moneyCOunt", db.ticketDao().findMany().map { it.book.price }.toString())
    //nạp vô recylerview của overview
    overviewAdapter = OverviewAdapter(
      listOf(
        Overview("Tổng doanh thu", moneyCount, Color.parseColor("#C9AB69")),
        Overview("Tổng sách", bookCount, Color.parseColor("#C7E1BB")),
        Overview("Tổng phiếu mượn", ticketCount, Color.parseColor("#FEE0BE")),
        Overview("Gần đến hạn", dueCount, Color.parseColor("#C9AB69")),
        Overview("Quá đến hạn", overDueCount, Color.parseColor("#F9C5BF")),
        Overview("Tổng thành viên", memberCount, Color.parseColor("#F9C5BF")),
      )
    )
    binding.rcOverview.layoutManager =
      LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
    binding.rcOverview.adapter = overviewAdapter

  }


  //Load ds 5 sách đầu tiên mới nhất
  //bấm xem thêm thì load hết
  fun loadBooks() {
    val data = db.bookDao().get5Books()
    books.clear()
    books.addAll(data)
    bookAdapter.notifyDataSetChanged()
  }

  //load ds 5 phiếu mượn đầu tiên mới nhất
  fun loadTickets() {
    val data = db.ticketDao().get5Tickets()
    tickets.clear()
    tickets.addAll(data)
    ticketAdapter.notifyDataSetChanged()
    initOverviews()
  }

  ///Khởi tạo lắng nghe các nút
  fun initViews() {
    binding.readMoreBook.setOnClickListener {
      val intent = Intent(requireContext(), BookActivity::class.java)
      startActivity(intent)
    }

    binding.readMoreTicket.setOnClickListener {
      val intent = Intent(requireContext(), TicketActivity::class.java)
      startActivity(intent)
    }
  }

}