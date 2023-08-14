package com.consoft.booklibrary.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
  @PrimaryKey(true)
  val id: Int?,
  @ColumnInfo("name", defaultValue = "")
  val name: String,
  @ColumnInfo("image", defaultValue = "")
  val image: String,
  @ColumnInfo("username")
  val username: String,
  @ColumnInfo("password")
  val password: String
) {
  constructor(username: String, password: String) : this(null, "", "", username, password) {

  }
}
