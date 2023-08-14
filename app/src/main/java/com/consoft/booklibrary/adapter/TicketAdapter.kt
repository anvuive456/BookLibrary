package com.consoft.booklibrary.adapter

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.consoft.booklibrary.R
import com.consoft.booklibrary.databinding.TicketItemBinding
import com.consoft.booklibrary.model.BookWithMembers
import com.consoft.booklibrary.model.Ticket
import com.consoft.booklibrary.model.TicketWithBookAndMember

class TicketAdapter(
  val tickets: List<TicketWithBookAndMember>,
  val onClick: ((ticket: TicketWithBookAndMember) -> Unit),
) :
  RecyclerView.Adapter<TicketAdapter.TicketHolder>() {

  inner class TicketHolder(val binding: TicketItemBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketHolder {
    val binding = TicketItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return TicketHolder(binding)
  }

  override fun getItemCount(): Int {
    return tickets.size
  }

  override fun onBindViewHolder(holder: TicketHolder, position: Int) {
    with(holder) {
      with(tickets[position]) {
        binding.borrowDate.text = this.ticket.formattedBorrowDate
        binding.dueDate.text = this.ticket.formattedDueDate
        binding.status.text = this.ticket.status.label
        binding.status.setTextColor(this.ticket.status.color)
        binding.title.text = this.book.title
        binding.memberName.text = this.member.name

        val bytes = Base64.decode(this.book.image, Base64.DEFAULT)

        Glide.with(binding.root)
          .asBitmap()
          .load(bytes)
          .placeholder(R.drawable.outline_image_24)
          .into(binding.image)

        binding.card.setOnClickListener {
          onClick(this)
        }
      }
    }
  }


}