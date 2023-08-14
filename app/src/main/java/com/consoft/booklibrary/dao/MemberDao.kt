package com.consoft.booklibrary.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.consoft.booklibrary.model.Member
import com.consoft.booklibrary.model.MemberWithBooks

@Dao
interface MemberDao {

  @Upsert()
  fun insert(vararg values: Member)

  @Update(onConflict = OnConflictStrategy.IGNORE)
  fun update(value: Member)

  @Delete
  fun delete(vararg values: Member)

  @Query("SELECT * FROM member WHERE name LIKE '%' ||:name || '%'")
  fun findMany(name: String = ""): List<MemberWithBooks>
}