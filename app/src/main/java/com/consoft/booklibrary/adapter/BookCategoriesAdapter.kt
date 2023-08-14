package com.consoft.booklibrary.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.consoft.booklibrary.databinding.FilterItemBinding
import com.consoft.booklibrary.interfaces.FilterHandler


class BookCategoriesAdapter(val categories: Array<String>, val handler: FilterHandler<String>) :
  RecyclerView.Adapter<BookCategoriesAdapter.CategoryHolder>() {

  var selectedCategory: String = ""

  inner class CategoryHolder(val binding: FilterItemBinding) : RecyclerView.ViewHolder(binding.root)

  companion object {
    const val activated = 1
    const val notActivated = 0
  }

  override fun getItemViewType(position: Int): Int {
    if (categories[position] == selectedCategory)
      return activated
    return notActivated
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
    val binding = FilterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    if (viewType == activated) {
      Log.w("activated", binding.root.isActivated.toString())
      binding.root.setBackgroundColor(0xFFEA7B2A.toInt())
      binding.txtName.setTextColor(Color.WHITE)
    }

    return CategoryHolder(binding)
  }

  override fun getItemCount(): Int {
    return categories.size
  }

  override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
    with(holder) {
      with(categories[position]) {
        binding.txtName.text = this

        binding.root.setOnClickListener {
          //nếu loại đang chọn là cái mình click thì huỷ chọn
          if(selectedCategory == categories[position]){
            selectedCategory = ""

          } else {
            //nếu không thì chọn cái mình click
            selectedCategory = categories[position]
          }
          handler.onItemClicked(selectedCategory)

          notifyDataSetChanged()
        }
      }
    }
  }


}