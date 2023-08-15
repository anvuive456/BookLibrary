package com.consoft.booklibrary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.consoft.booklibrary.databinding.MemberItemBinding
import com.consoft.booklibrary.model.Member
import com.consoft.booklibrary.model.MemberWithBooks

class MemberAdapter(
  val members: List<MemberWithBooks>,
  val onClick: (member: Member) -> Unit,
  val onLongClick: (member: Member) -> Unit
) :
  RecyclerView.Adapter<MemberAdapter.MemberHolder>() {


  inner class MemberHolder(val binding: MemberItemBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
    val binding = MemberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return MemberHolder(binding)
  }

  override fun getItemCount(): Int {
    return members.size
  }

  override fun onBindViewHolder(holder: MemberHolder, position: Int) {
    with(holder) {
      with(members[position]) {
        binding.title.text = this.member.name
        binding.mail.text = this.member.email
        binding.createdDate.text = this.member.formattedBirthday
        binding.card.setOnClickListener {
          onClick(this.member)
        }
        binding.card.setOnLongClickListener {
          onLongClick(this.member)
          true
        }
      }
    }
  }
}