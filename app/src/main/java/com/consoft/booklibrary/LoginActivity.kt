package com.consoft.booklibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.consoft.booklibrary.base.DataInstance
import com.consoft.booklibrary.dao.UserDao_Impl
import com.consoft.booklibrary.databinding.ActivityLoginBinding
import com.consoft.booklibrary.db.AppDatabase
import com.consoft.booklibrary.model.User

class LoginActivity : AppCompatActivity() {
  lateinit var binding: ActivityLoginBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)

    setContentView(binding.root)

    val dao = UserDao_Impl(AppDatabase.getDatabase(applicationContext))

    //đặt onclick cho button đăng nhập
    binding.btnLogin.setOnClickListener {
      val username = binding.edtUsername.text
      val password = binding.edtPassword.text

      //Nếu username, password trống thì hiện thông báo
      if (username == null || password == null
        || username.isEmpty() || password.isEmpty()
      ) {
        Toast.makeText(applicationContext, "Tài khoản/Mật khẩu không đc trống", Toast.LENGTH_LONG)
          .show()
      } else {
        //Kiếm user bằng username
        val users = dao.findByUsernameAndPassword(username.toString(), password.toString())

        //Không thấy thì sẽ thông báo không có tk
        if (users.isEmpty()) {
          Toast.makeText(applicationContext, "Không tìm thấy tk này", Toast.LENGTH_LONG).show()
        }
        //Nếu tìm thấy thì chuyển tới màn hình home và lưu thông tin vào DataInstance
        else {
          val intent = Intent(this, MainActivity::class.java)
          DataInstance.currentUser = users.first()
          startActivity(intent)

        }
      }
    }


    //đặt onclick cho button đăng ký
    binding.btnRegister.setOnClickListener {
      val username = binding.edtUsername.text
      val password = binding.edtPassword.text
      if (username == null || password == null
        || username.isEmpty() || password.isEmpty()
      ) {
        Toast.makeText(applicationContext, "Tài khoản/Mật khẩu không đc trống", Toast.LENGTH_LONG)
          .show()
      } else {
        dao.insert(
          User(
            username = username.toString(),
            password = password.toString(),
          )
        ).apply { Toast.makeText(applicationContext,"Đăng ký thành công",Toast.LENGTH_LONG).show() }
      }

    }
  }


}