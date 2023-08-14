package com.consoft.booklibrary

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.consoft.booklibrary.databinding.ActivityAddMemberBinding
import com.consoft.booklibrary.db.AppDatabase
import com.consoft.booklibrary.model.Member
import java.time.LocalDate
import java.time.LocalDateTime

class AddMemberActivity : AppCompatActivity() {
  lateinit var binding: ActivityAddMemberBinding
  lateinit var db: AppDatabase

  lateinit var member: Member
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityAddMemberBinding.inflate(layoutInflater)
    setContentView(binding.root)

    db = AppDatabase.getDatabase(applicationContext)

    if (intent.getParcelableExtra<Member>("member") != null) {
      member = intent.getParcelableExtra<Member>("member")!!
    } else member = Member()

    binding.edtTitle.setText(member.name)
    binding.dobPicker.init(
      member.birthday.year,
      member.birthday.monthValue - 1,
      member.birthday.dayOfMonth, object : DatePicker.OnDateChangedListener {
        override fun onDateChanged(
          view: DatePicker?,
          year: Int,
          monthOfYear: Int,
          dayOfMonth: Int
        ) {
          member = member.copy(
            birthday = LocalDate.of(
              year, monthOfYear + 1, dayOfMonth
            )
          )
        }

      }
    )

    binding.edtTitle.addTextChangedListener {
      member = member.copy(name = it.toString())
    }

    binding.btnTogglePicker.setOnClickListener {
      if (binding.dobPicker.visibility == View.GONE) {
        binding.dobPicker.visibility = View.VISIBLE
      } else {
        binding.dobPicker.visibility = View.GONE

      }
    }

//    binding.dobPicker.maxDate = LocalDate.now().toEpochDay()
//    binding.dobPicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
//      member = member.copy(
//        birthday = LocalDate.of(
//          year, monthOfYear, dayOfMonth
//        )
//      )
//    }

    binding.btnAdd.setOnClickListener {
      if (member.name.isEmpty() || member.formattedBirthday.isEmpty()) {
        Toast.makeText(applicationContext, "Tên và ngày sinh phải đc chọn", Toast.LENGTH_LONG)
          .show()
      } else {
        //Nếu chưa có data thì thêm mới không thì update data cũ
        db.memberDao().insert(member)
        intent.putExtra("member", member)
        setResult(Activity.RESULT_OK)
        finish()
      }
    }
  }


}