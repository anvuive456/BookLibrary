package com.consoft.booklibrary.db

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.consoft.booklibrary.base.TimestampConverter
import com.consoft.booklibrary.dao.BookDao
import com.consoft.booklibrary.dao.MemberDao
import com.consoft.booklibrary.dao.TicketDao
import com.consoft.booklibrary.dao.UserDao
import com.consoft.booklibrary.model.Book
import com.consoft.booklibrary.model.Member
import com.consoft.booklibrary.model.Ticket
import com.consoft.booklibrary.model.User

@Database(
  entities = [User::class, Book::class, Member::class, Ticket::class],
  version = AppDatabase.VERSION,
  exportSchema = AppDatabase.EXPORT_SCHEMA
)
@TypeConverters(TimestampConverter::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun bookDao(): BookDao
  abstract fun memberDao(): MemberDao
  abstract fun ticketDao(): TicketDao


  companion object {
    const val VERSION: Int = 13
    const val EXPORT_SCHEMA: Boolean = false

    //Khởi tạo instance cho class db này
    //ở mọi màn hình chỉ cần gọi getDatabase là đc
    @Volatile
    private var INSTANCE: AppDatabase? = null
    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "librarydb.db"
        ).allowMainThreadQueries()
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        return instance
      }
    }
  }

}