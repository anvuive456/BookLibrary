package com.consoft.booklibrary.adapter

import android.content.Context
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.consoft.booklibrary.R
import com.consoft.booklibrary.databinding.BookItemBinding
import com.consoft.booklibrary.model.Book
import com.consoft.booklibrary.model.BookWithMembers

class BookAdapter(
  val books: List<BookWithMembers>,
  val onCLick: ((book: BookWithMembers) -> Unit)? = null,
  val onLongClick: ((book: BookWithMembers) -> Unit)? = null
) :
  RecyclerView.Adapter<BookAdapter.ItemHolder>() {


  inner class ItemHolder(val binding: BookItemBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
    val binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    return ItemHolder(binding)
  }


  override fun getItemCount(): Int = books.size

  override fun onBindViewHolder(holder: ItemHolder, position: Int) {
    Log.w("position",position.toString())
    with(holder) {
      with(books[position]) {
        binding.title.text = this.book.title
        binding.price.text = this.book.formattedPrice

        if(this.book.image.isNotEmpty()){
          //load hình ảnh từ book lên view
          val bytes = Base64.decode(this.book.image, Base64.DEFAULT)
          Glide.with(binding.root)
            .asBitmap()
            .load(bytes)
            .placeholder(R.drawable.outline_image_24)
            .into(binding.image)
        }
        binding.category.text = this.book.category

        binding.description.text = this.book.description

//        binding.createdDate.text = this.book.formattedCreatedAt

        binding.card.setOnClickListener {
          onCLick?.let { it(books[position]) }
        }
        binding.card.setOnLongClickListener{
          onLongClick?.let { it(books[position]) }
          true
        }
      }
    }
  }



}