package com.consoft.booklibrary.adapter

import android.graphics.Typeface.ITALIC
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.consoft.booklibrary.R
import com.consoft.booklibrary.databinding.TicketItemBinding
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
        binding.borrowDate.text = "Ngày mượn:"+this.ticket.formattedBorrowDate
        binding.dueDate.text = "Hạn trả:"+this.ticket.formattedDueDate
        binding.status.text = this.ticket.status.label
        binding.status.setTextColor(this.ticket.status.color)
        binding.title.text = this.book.title

        //dùng spannable để hiển thị 2 style text khác nhau trong 1 textview
        val span = SpannableString("${this.member.name}(${this.member.email})")
        span.setSpan(
          StyleSpan(ITALIC),
          this.member.name.length,
          span.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(
          AbsoluteSizeSpan(30),
          this.member.name.length,
          span.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.memberName.text =span

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