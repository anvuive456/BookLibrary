package com.consoft.booklibrary.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.consoft.booklibrary.model.User

@Dao
interface UserDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg values: User)

  @Update(onConflict = OnConflictStrategy.IGNORE)
  fun update(value: User)

  @Delete
  fun delete(vararg values: User)

  //duyệt tất cả user trừ user hiện tại
  @Query("SELECT * FROM users WHERE name LIKE :name AND id != :currUserId")
  fun findMany(name: String, currUserId: Int): List<User>

  @Query("SELECT * FROM users WHERE username == :username AND password = :password")
  fun findByUsernameAndPassword(username: String, password: String) : List<User>

  @Query("SELECT * FROM users WHERE id == :id LIMIT 1")
  fun findById(id: Int): List<User>

}