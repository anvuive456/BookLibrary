package com.consoft.booklibrary.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


//duyệt thành viên với danh sách book member đó mượn
data class MemberWithBooks(
  @Embedded
  val member: Member,

  @Relation(
    entity = Book::class,
    parentColumn = "memberId",
    entityColumn = "bookId",
    associateBy = Junction(Ticket::class, parentColumn = "member_Id", entityColumn = "book_Id")
  )
  val books: List<Book>
): Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readParcelable(Member::class.java.classLoader)!!,
    parcel.createTypedArrayList(Book)!!
  ) {
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeParcelable(member, flags)
    parcel.writeTypedList(books)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<MemberWithBooks> {
    override fun createFromParcel(parcel: Parcel): MemberWithBooks {
      return MemberWithBooks(parcel)
    }

    override fun newArray(size: Int): Array<MemberWithBooks?> {
      return arrayOfNulls(size)
    }
  }

}
