package com.consoft.booklibrary

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.consoft.booklibrary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  lateinit var binding: ActivityMainBinding
  lateinit var navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    //Khởi tạo màn hình book
    loadFragment(BookFragment())

    //mỗi lần nhấn item trong bottomsheet thay đổi fragment
    binding.bottomNavigation.setOnItemSelectedListener {
      when (it.itemId) {
        R.id.mnu_book -> {
          loadFragment(BookFragment())
          return@setOnItemSelectedListener true
        }
        R.id.mnu_profile -> {
          loadFragment(ProfileFragment())
          return@setOnItemSelectedListener true
        }
        R.id.mnu_member -> {
          loadFragment(MemberFragment())
          return@setOnItemSelectedListener true
        }
        R.id.mnu_analytic -> {
          loadFragment(AnalyticFragment())
          return@setOnItemSelectedListener true
        }
      }
      return@setOnItemSelectedListener false
    }
  }
  //đổi fragment trong main activity
  private  fun loadFragment(fragment: Fragment){
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.container,fragment)
    transaction.commit()
  }
}