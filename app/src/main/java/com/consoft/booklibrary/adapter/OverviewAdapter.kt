package com.consoft.booklibrary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.consoft.booklibrary.databinding.OverviewCardBinding
import com.consoft.booklibrary.model.Overview

class OverviewAdapter(val overviews: List<Overview>) : RecyclerView.Adapter<OverviewAdapter.OverviewHolder>() {

  inner class OverviewHolder(val binding: OverviewCardBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewHolder {
    val binding =
      OverviewCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return OverviewHolder(binding)
  }

  override fun getItemCount(): Int {
   return  overviews.size
  }

  override fun onBindViewHolder(holder: OverviewHolder, position: Int) {
    with(holder){
      with(overviews[position]){
        binding.title.text = this.title
        binding.value.text = this.value.toString()
        binding.card.setCardBackgroundColor(this.color)
      }
    }
  }

}