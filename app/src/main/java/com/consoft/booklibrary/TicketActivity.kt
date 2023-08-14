package com.consoft.booklibrary

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.consoft.booklibrary.adapter.TicketAdapter
import com.consoft.booklibrary.databinding.ActivityTicketBinding
import com.consoft.booklibrary.db.AppDatabase
import com.consoft.booklibrary.model.TicketWithBookAndMember

class TicketActivity : AppCompatActivity() {
  lateinit var binding: ActivityTicketBinding
  lateinit var adapter: TicketAdapter
  lateinit var db: AppDatabase
  var tickets = mutableListOf<TicketWithBookAndMember>()

  var searchText = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    db = AppDatabase.getDatabase(applicationContext)
    binding = ActivityTicketBinding.inflate(layoutInflater)
    setContentView(binding.root)

    adapter = TicketAdapter(tickets, onClick = {
      val intent = Intent(applicationContext, AddTicketActivity::class.java)
      intent.putExtra("ticket", it.ticket)
      updateLauncher.launch(intent)
    })

    binding.rcTicket.layoutManager = LinearLayoutManager(applicationContext)
    binding.rcTicket.adapter = adapter

    binding.btnAddBook.setOnClickListener {
      val intent = Intent(applicationContext, AddTicketActivity::class.java)
      addLauncher.launch(intent)
    }

    binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) searchText = query else searchText = ""

        loadTickets()

        return true
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        return false
      }

    })

    loadTickets()
  }

  //lắng nghe sau khi thêm
  val addLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (it.resultCode == Activity.RESULT_OK) {
      Log.e("ticket", it.data?.extras.toString())
      loadTickets()
    }
  }

  //lắng nghe update và load lại list
  val updateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (it.resultCode == Activity.RESULT_OK) {
      loadTickets()
    }
  }

  fun loadTickets() {
    tickets.clear()
    tickets.addAll(db.ticketDao().findMany(searchText))

    adapter.notifyDataSetChanged()
  }

}