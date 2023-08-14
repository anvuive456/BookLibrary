package com.consoft.booklibrary.model

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "books")
data class Book(
  @PrimaryKey(autoGenerate = true)
  val bookId: Int?,
  @ColumnInfo("title")
  val title: String,
  @ColumnInfo("description")
  val description: String,
  //Hình ảnh là chuỗi base64
  @ColumnInfo("image")
  val image: String,
  @ColumnInfo("category")
  val category: String,
  @ColumnInfo("price")
  val price: Int,

  //ngày tạo mặc định là hiện tại
  //không được update
  @ColumnInfo("createdAt")
  val createdAt: LocalDate = LocalDate.now(),
) : Parcelable {
  //format ngày tạo thành dd/MM/yyyy
  val formattedCreatedAt get() = createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

  //format giá thành VND
  val formattedPrice get() = "$price VND"

  constructor(parcel: Parcel) : this(
    parcel.readValue(Int::class.java.classLoader) as? Int,
    parcel.readString().orEmpty(),
    parcel.readString().orEmpty(),
    parcel.readString().orEmpty(),
    parcel.readString().orEmpty(),
    parcel.readInt(),
    LocalDate.parse(parcel.readString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))

  ) {
  }


  constructor(name: String) : this(null, name, "", "", "", 0, LocalDate.now())

  override fun toString(): String {
    return title
  }
  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeValue(bookId)
    parcel.writeString(title)
    parcel.writeString(description)
    parcel.writeString(image)
    parcel.writeString(category)
    parcel.writeInt(price)
    parcel.writeString(formattedCreatedAt)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Book> {
    override fun createFromParcel(parcel: Parcel): Book {
      return Book(parcel)
    }

    override fun newArray(size: Int): Array<Book?> {
      return arrayOfNulls(size)
    }
  }


}