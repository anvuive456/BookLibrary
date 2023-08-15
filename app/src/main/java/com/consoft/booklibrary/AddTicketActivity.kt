package com.consoft.booklibrary

import android.app.Activity
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.consoft.booklibrary.databinding.ActivityAddTicketBinding
import com.consoft.booklibrary.db.AppDatabase
import com.consoft.booklibrary.model.Book
import com.consoft.booklibrary.model.Member
import com.consoft.booklibrary.model.Ticket
import java.time.LocalDate
import java.util.Calendar

class AddTicketActivity : AppCompatActivity() {
  lateinit var binding: ActivityAddTicketBinding
  lateinit var ticket: Ticket
  lateinit var db: AppDatabase

  var books = listOf<Book>()
  var members = listOf<Member>()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityAddTicketBinding.inflate(layoutInflater)
    setContentView(binding.root)

    db = AppDatabase.getDatabase(applicationContext)


    if (intent.getParcelableExtra<Ticket>("ticket") == null) {
      ticket = Ticket()
    } else {
      ticket = intent.getParcelableExtra<Ticket>("ticket")!!
    }

    Log.w("ticket id","book: ${ticket.book_Id}member: ${ticket.member_Id}")

    initBooks()
    initMembers()
    initAddUpdateButton()
    initTogglePicker()
    initPickers()
    initToggle()
  }

  fun initToggle() {
    //đặt giá trị ban đầu cho switch và lắng nghe thay đổi
    //khi add thì không hiện nút này
    // chỉ hiện khi update
    if (intent.getParcelableExtra<Ticket>("ticket") == null) {
      binding.status.visibility = View.GONE
    } else {
      binding.status.visibility = View.VISIBLE
      binding.status.isChecked = ticket.borrowing

      //update ticket khi chọn
      binding.status.setOnCheckedChangeListener { _, isChecked ->
        ticket = ticket.copy(borrowing = isChecked)
      }

    }
  }


  fun initBooks() {
    //Lấy danh sách sách
    //dùng map để lấy sách trong  BookWithMembers
    books = db.bookDao().getBooks().map { it.book }

    binding.spnBook.adapter =
      ArrayAdapter<Book>(applicationContext, android.R.layout.simple_spinner_item, books)

    val ids = books.map { it.bookId }
    binding.spnBook.setSelection(ids.indexOf(ticket.book_Id))
  }

  fun initMembers() {
    //Lấy danh sách thành viên
    //dùng map để lấy sách trong  MemberWithBooks
    members = db.memberDao().findMany().map { it.member }

    binding.spnMember.adapter =
      ArrayAdapter<Member>(applicationContext, android.R.layout.simple_spinner_item, members)
   val ids = members.map { it.memberId }
    binding.spnMember.setSelection(ids.indexOf(ticket.member_Id))

  }

  fun initPickers() {
    binding.borrowPicker.init(
      ticket.borrowDate.year,
      ticket.borrowDate.monthValue - 1,
      ticket.borrowDate.dayOfMonth, object : DatePicker.OnDateChangedListener {
        override fun onDateChanged(
          view: DatePicker?,
          year: Int,
          monthOfYear: Int,
          dayOfMonth: Int
        ) {
          ticket = ticket.copy(
            borrowDate = LocalDate.of(
              year, monthOfYear + 1, dayOfMonth
            )
          )
        }

      }
    )
    binding.duePicker.init(
      ticket.dueDate.year,
      ticket.dueDate.monthValue - 1,
      ticket.dueDate.dayOfMonth, object : DatePicker.OnDateChangedListener {
        override fun onDateChanged(
          view: DatePicker?,
          year: Int,
          monthOfYear: Int,
          dayOfMonth: Int
        ) {
          ticket = ticket.copy(
            dueDate = LocalDate.of(
              year, monthOfYear + 1, dayOfMonth
            )
          )
        }

      }
    )
  }

  fun initTogglePicker() {
    binding.btnPickBorrowDate.setOnClickListener {
      if (binding.borrowPicker.visibility == View.GONE) {
        binding.borrowPicker.visibility = View.VISIBLE
      } else
        binding.borrowPicker.visibility = View.GONE
    }
    binding.btnPickDueDate.setOnClickListener {
      if (binding.duePicker.visibility == View.GONE) {
        binding.duePicker.visibility = View.VISIBLE
      } else
        binding.duePicker.visibility = View.GONE
    }
  }

  fun initAddUpdateButton() {


    binding.btnAdd.setOnClickListener {
      //nếu sách rỗng hoặc member rỗng thì thông báo
      if (books.isEmpty() || members.isEmpty()) {
        Toast.makeText(
          applicationContext,
          "Phải có ít nhất 1 thành viên hoặc sách",
          Toast.LENGTH_LONG
        ).show()
      } else {
        //Thêm mới hoặc update cái có sẵn
        ticket = ticket.copy(
          book_Id = books[binding.spnBook.selectedItemPosition].bookId!!,
          member_Id = members[binding.spnMember.selectedItemPosition].memberId!!,
        )
        db.ticketDao().insert(ticket)
        intent.putExtra("ticket", ticket)
        setResult(Activity.RESULT_OK)
        finish()
      }

    }
  }


}