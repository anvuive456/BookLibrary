package com.consoft.booklibrary.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation


//duyệt sách với ds member đã mượn
data class BookWithMembers(
  @Embedded
  val book: Book,

  @Relation(
    entity = Member::class,
    parentColumn = "bookId",
    entityColumn = "memberId",
    associateBy = Junction(Ticket::class, parentColumn = "book_Id", entityColumn = "member_Id")
  )
  val members: List<Member>
): Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readParcelable(Book::class.java.classLoader)!!,
    parcel.createTypedArrayList(Member)!!
  ) {
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeParcelable(book, flags)
    parcel.writeTypedList(members)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<BookWithMembers> {
    override fun createFromParcel(parcel: Parcel): BookWithMembers {
      return BookWithMembers(parcel)
    }

    override fun newArray(size: Int): Array<BookWithMembers?> {
      return arrayOfNulls(size)
    }
  }

}
