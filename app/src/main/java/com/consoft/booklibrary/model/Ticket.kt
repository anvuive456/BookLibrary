package com.consoft.booklibrary.model

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class TicketStatus(val label: String, val color: Int) {
  OverDue("Quá hạn", Color.parseColor("#F9C5BF")),
  Borrowing("Đang mượn", Color.parseColor("#FEE0BE")),
  Returned("Đã trả", Color.parseColor("#C7E1BB"))
}

//bảng phiếu mượn
//chứa mối quan hệ book n-n member
@Entity(tableName = "ticket", primaryKeys = ["book_Id", "member_Id"])
data class Ticket(
  val book_Id: Int,
  val member_Id: Int,
  //này mượn
  val borrowDate: LocalDate,
  //ngày trả
  val dueDate: LocalDate,
  //đang mượn true
  //đã trả false
  val borrowing: Boolean = true
) :Parcelable{
  val status: TicketStatus
    get() {
      if (LocalDate.now() > dueDate) return TicketStatus.OverDue
      if (borrowing) return TicketStatus.Borrowing
      else return TicketStatus.Returned
    }
  val formattedBorrowDate: String get() = borrowDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
  val formattedDueDate: String get() = dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

  constructor():this(-1,-1, LocalDate.now(), LocalDate.now(), true)
  constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readInt(),
    LocalDate.parse(parcel.readString()),
    LocalDate.parse(parcel.readString()),
    parcel.readByte() != 0.toByte()
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(book_Id)
    parcel.writeInt(member_Id)
    parcel.writeString(borrowDate.toString())
    parcel.writeString(dueDate.toString())
    parcel.writeByte(if (borrowing) 1 else 0)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Ticket> {
    override fun createFromParcel(parcel: Parcel): Ticket {
      return Ticket(parcel)
    }

    override fun newArray(size: Int): Array<Ticket?> {
      return arrayOfNulls(size)
    }
  }
}
