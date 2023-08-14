package com.consoft.booklibrary.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.consoft.booklibrary.model.Book
import com.consoft.booklibrary.model.BookWithMembers
import com.consoft.booklibrary.model.User

@Dao
interface BookDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg values: Book)

  @Update(onConflict = OnConflictStrategy.IGNORE)
  fun update(value: Book)

  @Delete
  fun delete(vararg values: Book)

  //Lấy sách với ds member đã mượn
  //đổi [BookWithMembers] thành [Book] nếu không muốn có thêm ds member
  @Query("SELECT * FROM books WHERE title LIKE '%' || :title || '%' AND category LIKE '%' || :category || '%'")
  fun getBooks(title: String = "", category: String = ""): List<BookWithMembers>

  @Query("SELECT * FROM books  ORDER BY date(createdAt) DESC LIMIT 5 ")
  fun get5Books(): List<BookWithMembers>
}