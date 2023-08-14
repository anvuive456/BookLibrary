package com.consoft.booklibrary

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.SpinnerAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.consoft.booklibrary.adapter.BookAdapter
import com.consoft.booklibrary.adapter.BookCategoriesAdapter
import com.consoft.booklibrary.databinding.ActivityBookBinding
import com.consoft.booklibrary.db.AppDatabase
import com.consoft.booklibrary.interfaces.FilterHandler
import com.consoft.booklibrary.model.Book
import com.consoft.booklibrary.model.BookWithMembers

class BookActivity : AppCompatActivity() {
  lateinit var binding: ActivityBookBinding
  lateinit var adapter: BookAdapter
  lateinit var categoryAdapter: BookCategoriesAdapter
  lateinit var db: AppDatabase
  var books = mutableListOf<BookWithMembers>()

  //loại được chọn
  var selectedCategory: String = ""
  var searchText: String = ""


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityBookBinding.inflate(layoutInflater)
    setContentView(binding.root)
    db = AppDatabase.getDatabase(applicationContext)
    adapter = BookAdapter(books, onCLick= {
      val intent = Intent(applicationContext, DetailBookActivity::class.java)
      intent.putExtra("book",it.book)
      updateLauncher.launch(intent)
    }, onLongClick = {
      db.bookDao().delete(it.book)
      loadBooks()
    })
    binding.rcBook.layoutManager = LinearLayoutManager(applicationContext)
    binding.rcBook.adapter = adapter


    //khởi tạo list filter
    categoryAdapter = BookCategoriesAdapter(
      resources.getStringArray(R.array.categories),
      handler = object : FilterHandler<String> {
        //mỗi lần nhấn thay đổi loại và load lại sách
        override fun onItemClicked(value: String) {
          selectedCategory = value
          loadBooks()
        }
      })
    binding.rcFilterCategory.layoutManager =
      LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL,false)
    binding.rcFilterCategory.adapter = categoryAdapter
    loadBooks()
    initSearch()
    initAddButton()
    initToggleFilter()
  }

  //khởi tạo bật tắt filter
  private fun initToggleFilter(){
    binding.btnToggleFilter.setOnClickListener {
      //nếu đang hiện thì đặt là gone
      //ngược lại đặt visible
      if(binding.rcFilterCategory.visibility == View.VISIBLE){
        binding.rcFilterCategory.visibility = View.GONE
      } else {
        binding.rcFilterCategory.visibility = View.VISIBLE

      }
    }
  }

  private fun initAddButton() {
    binding.btnAddBook.setOnClickListener {
      val intent = Intent(applicationContext, AddBookActivity::class.java)

      addLauncher.launch(intent)
    }
  }

  //lắng nghe sau khi thêm
  val addLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (it.resultCode == Activity.RESULT_OK) {
      loadBooks()
    }
  }

  //lắng nghe update và load lại list
  val updateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
    if (it.resultCode == Activity.RESULT_OK) {
      loadBooks()
    }
  }


  //Khởi tạo lắng nghe khi search
  //load lại view khi search
  private fun initSearch() {
    binding.searchBook.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
          searchText = query
        } else {
          searchText = ""
        }
        loadBooks()
        return true
      }

      override fun onQueryTextChange(newText: String?): Boolean {

        return false
      }
    })
  }

//  private fun initCategorySpinner() {
//    val spinnerAdapter = ArrayAdapter.createFromResource(
//      applicationContext,
//      R.array.categories,
//      android.R.layout.simple_spinner_item
//    )
//
//    binding.spnCategory.adapter = spinnerAdapter
//    binding.spnCategory.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
//      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        selectedCategory = resources.getStringArray(R.array.categories)[position]
//        loadBooks()
//      }
//
//      override fun onNothingSelected(parent: AdapterView<*>?) {
//      }
//    })
//  }


  fun loadBooks() {
    val data = db.bookDao().getBooks(title = searchText, category = selectedCategory)
    books.clear()
    books.addAll(data)
    adapter.notifyDataSetChanged()
  }
}