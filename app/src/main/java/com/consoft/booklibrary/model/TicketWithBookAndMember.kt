package com.consoft.booklibrary.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded

data class TicketWithBookAndMember(
  @Embedded val ticket: Ticket,
  @Embedded val book: Book,
  @Embedded val member: Member
):Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readParcelable(Ticket::class.java.classLoader)!!,
    parcel.readParcelable(Book::class.java.classLoader)!!,
    parcel.readParcelable(Member::class.java.classLoader)!!
  ) {
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeParcelable(ticket, flags)
    parcel.writeParcelable(book, flags)
    parcel.writeParcelable(member, flags)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<TicketWithBookAndMember> {
    override fun createFromParcel(parcel: Parcel): TicketWithBookAndMember {
      return TicketWithBookAndMember(parcel)
    }

    override fun newArray(size: Int): Array<TicketWithBookAndMember?> {
      return arrayOfNulls(size)
    }
  }

}