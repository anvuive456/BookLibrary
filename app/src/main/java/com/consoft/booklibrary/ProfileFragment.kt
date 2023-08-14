package com.consoft.booklibrary

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.consoft.booklibrary.base.DataInstance
import com.consoft.booklibrary.databinding.FragmentProfileBinding
import com.consoft.booklibrary.db.AppDatabase
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class ProfileFragment : Fragment() {
  lateinit var binding: FragmentProfileBinding
  lateinit var db: AppDatabase
  var user = DataInstance.currentUser

  //đăng ký sau khi chọn ảnh sẽ nhận ảnh
  //nếu có ảnh thì đặt vô image của user
  private val startForProfileImageResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      val resultCode = result.resultCode
      val intent = result.data
      Log.w("image intent","$resultCode $intent ${intent?.data}")

      when (resultCode) {
        Activity.RESULT_OK -> {

          //Map hình ảnh sang base64
          val fileUri = intent?.data!!
          val imageStream: InputStream =
            binding.root.context.contentResolver.openInputStream(fileUri)!!
          val selectedImage:Bitmap = BitmapFactory.decodeStream(imageStream)
          val bytes = ByteArrayOutputStream()
          selectedImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
          val byteArr = bytes.toByteArray()
          user = user?.copy(image = Base64.encodeToString(byteArr, Base64.DEFAULT))

          //set hình ảnh vào view
          Glide.with(requireContext()).asBitmap()
            .load(selectedImage)
            .placeholder(R.drawable.outline_image_24)
            .circleCrop()
            .into(binding.image)
        }
        ImagePicker.RESULT_ERROR -> {
          Toast.makeText(this.requireContext(), ImagePicker.getError(intent), Toast.LENGTH_SHORT)
            .show()
        }
        else -> {
          Toast.makeText(this.requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    db = AppDatabase.getDatabase(requireContext())
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentProfileBinding.inflate(inflater, container, false)

    initViews()

    return binding.root
  }

  fun initViews() {
    //Nếu user== null thì hiện nút đăng nhập và không hiện form
    if (user == null) {
      binding.btnLogin.visibility = View.VISIBLE
      binding.btnUpdate.visibility = View.GONE
      binding.btnLogout.visibility = View.GONE
      binding.image.visibility = View.GONE
      binding.edtName.visibility = View.GONE
      binding.edtUsername.visibility = View.GONE
      binding.edtPassword.visibility = View.GONE

      binding.btnLogin.setOnClickListener {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
      }
      return
    }

    //Load hình của user lên view
    val bytes = Base64.decode(user?.image, Base64.DEFAULT)
    Glide.with(binding.root)
      .asBitmap()
      .load(bytes)
      .circleCrop()
      .placeholder(R.drawable.outline_image_24)
      .into(binding.image)
    binding.edtUsername.setText(user?.username ?: "")
    binding.edtName.setText(user?.name ?: "")
    binding.image.setOnClickListener {
      ImagePicker.with(this)
        .crop()
        .compress(350)
        .maxResultSize(350, 350)
        .createIntent { intent ->
          startForProfileImageResult.launch(intent)
        }
    }
    binding.btnUpdate.setOnClickListener {
      if (user != null) {
        //Update user trong db
        db.userDao().update(user!!.copy())
        //Update user trong data instance
        DataInstance.currentUser = user
        Toast.makeText(requireContext(), "Update xong", Toast.LENGTH_SHORT).show()
      }
    }

    //lắng nghe các editable
    binding.edtUsername.addTextChangedListener {
      user = user?.copy(username = it.toString())
    }

    binding.edtName.addTextChangedListener {
      user = user?.copy(name = it.toString())
    }

    binding.edtPassword.addTextChangedListener {
      user = user?.copy(password = it.toString())
    }

    binding.btnLogout.setOnClickListener {
      activity?.finish()
    }
  }


}