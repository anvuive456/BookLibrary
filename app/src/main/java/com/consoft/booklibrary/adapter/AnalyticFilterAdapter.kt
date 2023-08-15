package com.consoft.booklibrary.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.consoft.booklibrary.databinding.FilterItemBinding

class AnalyticFilterAdapter(val filters: Array<Filter>, val onClick: (filter: Filter) -> Unit) :
  RecyclerView.Adapter<AnalyticFilterAdapter.FilterHolder>() {

  enum class Filter(val title: String) {
    day("Theo ngày"),
    month("Theo tháng"),
    quarter("Theo quý"),
    year("Theo năm")
  }

  var selectedCategory = Filter.day

  companion object {
    const val activated = 1
    const val notActivated = 0
  }

  override fun getItemViewType(position: Int): Int {
    if (filters[position] == selectedCategory)
      return BookCategoriesAdapter.activated
    return BookCategoriesAdapter.notActivated
  }

  inner class FilterHolder(val binding: FilterItemBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterHolder {
    val binding = FilterItemBinding.inflate(LayoutInflater.from(parent.context))

    if (viewType == activated) {
      Log.w("activated", binding.root.isActivated.toString())
      binding.root.setBackgroundColor(0xFFEA7B2A.toInt())
      binding.txtName.setTextColor(Color.WHITE)
    }
    return FilterHolder(binding)
  }

  override fun getItemCount(): Int {
    return filters.size
  }

  override fun onBindViewHolder(holder: FilterHolder, position: Int) {
    with(holder) {
      with(filters[position]) {
        binding.txtName.text = this.title

        binding.root.setOnClickListener {
          //nếu loại đang chọn là cái mình click thì huỷ chọn

          //nếu không thì chọn cái mình click
          selectedCategory = filters[position]

          notifyDataSetChanged()
          onClick(selectedCategory)
        }

      }
    }
  }
}