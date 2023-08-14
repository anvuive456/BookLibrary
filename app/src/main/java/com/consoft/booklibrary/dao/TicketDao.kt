package com.consoft.booklibrary.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.consoft.booklibrary.model.Ticket
import com.consoft.booklibrary.model.TicketWithBookAndMember

@Dao
interface TicketDao {

  @Upsert()
  fun insert(vararg values: Ticket)

  @Update(onConflict = OnConflictStrategy.IGNORE)
  fun update(value: Ticket)

  @Delete
  fun delete(vararg values: Ticket)


  //join bảng book và user và duyệt theo title book hoặc tên member
  @Query(
    "SELECT ticket.*, books.*, member.*  FROM ticket "
            + "INNER JOIN books ON ticket.book_Id = books.bookId "
            + "INNER JOIN member ON ticket.member_Id = member.memberId "
            + "WHERE title LIKE '%' || :searchText || '%'"
            + " OR name LIKE '%' || :searchText || '%'"
  )
  fun findMany(searchText: String = ""): List<TicketWithBookAndMember>

  @Query(
    "SELECT ticket.*, books.*, member.* FROM ticket "
            + "INNER JOIN books ON ticket.book_Id = books.bookId "
            + "INNER JOIN member ON ticket.member_Id = member.memberId "
            + "ORDER BY date(dueDate)"
            + " LIMIT 5"
  )
  fun get5Tickets(): List<TicketWithBookAndMember>

}