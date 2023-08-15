package com.consoft.booklibrary.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Patterns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

@Entity(tableName = "member", indices = [Index(value = ["email"], unique = true)])
data class Member(
  @PrimaryKey(autoGenerate = true)
  val memberId: Int?,
  @ColumnInfo("name")
  val name: String,
  @ColumnInfo("email")
  val email: String,
  @ColumnInfo("dob")
  val birthday: LocalDate
) : Parcelable {

  constructor() : this(null, "", "", LocalDate.now())

  //kiểm tra tên có rỗng hay không
  val isValidName: Boolean
    get() {
      return name.isNotEmpty()
    }


  //kiểm tra email đúng định dạng hay ko
  val isValidEmail: Boolean
    get() {
      return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

  val formattedBirthday: String
    get() = birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))


  constructor(parcel: Parcel) : this(
    parcel.readValue(Int::class.java.classLoader) as? Int,
    parcel.readString().toString(),
    parcel.readString().toString(),
    LocalDate.parse(parcel.readString())
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeValue(memberId)
    parcel.writeString(name)
    parcel.writeString(email)
    parcel.writeString(birthday.toString())
  }

  override fun toString(): String {
    return name
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Member> {
    override fun createFromParcel(parcel: Parcel): Member {
      return Member(parcel)
    }

    override fun newArray(size: Int): Array<Member?> {
      return arrayOfNulls(size)
    }
  }

}