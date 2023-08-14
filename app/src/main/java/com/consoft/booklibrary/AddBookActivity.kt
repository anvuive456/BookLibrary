package com.consoft.booklibrary

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.consoft.booklibrary.databinding.ActivityAddBookBinding
import com.consoft.booklibrary.db.AppDatabase
import com.consoft.booklibrary.model.Book
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AddBookActivity : AppCompatActivity() {
  lateinit var binding: ActivityAddBookBinding
  lateinit var db: AppDatabase
  var book: Book = Book("")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityAddBookBinding.inflate(layoutInflater)
    setContentView(binding.root)
    db = AppDatabase.getDatabase(applicationContext)
    initViews()
  }

  //lắng nghe sau khi lấy hình
  private val launchImagePick =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      val resultCode = result.resultCode
      val intent = result.data
      Log.w("image intent", "$resultCode $intent ${intent?.data}")

      when (resultCode) {
        Activity.RESULT_OK -> {

          //Map hình ảnh sang base64
          val fileUri = intent?.data!!
          val imageStream: InputStream =
            binding.root.context.contentResolver.openInputStream(fileUri)!!
          val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
          val bytes = ByteArrayOutputStream()
          selectedImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
          val byteArr = bytes.toByteArray()
          book = book.copy(image = Base64.encodeToString(byteArr, Base64.DEFAULT))

          //set hình ảnh vào view
          Glide.with(applicationContext).asBitmap()
            .load(selectedImage)
            .placeholder(R.drawable.outline_image_24)
            .circleCrop()
            .into(binding.image)
        }

        ImagePicker.RESULT_ERROR -> {
          Toast.makeText(applicationContext, ImagePicker.getError(intent), Toast.LENGTH_SHORT)
            .show()
        }

        else -> {
          Toast.makeText(applicationContext, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
      }
    }


  fun initViews() {
    binding.image.setOnClickListener {
      ImagePicker.with(this)
        .crop()
        .compress(350)
        .maxResultSize(350, 350)
        .createIntent { intent ->
          launchImagePick.launch(intent)
        }
    }

    binding.edtTitle.addTextChangedListener {
      book = book.copy(title = it.toString())
    }

    binding.edtDescription.addTextChangedListener {
      book = book.copy(description = it.toString())
    }

    binding.edtPrice.addTextChangedListener {
      if (it.toString().isEmpty() || it.toString() == "null") {
        book = book.copy(price = 0)
      } else
        book = book.copy(price = it.toString().toInt())
    }
    val spinnerAdapter =
      ArrayAdapter.createFromResource(
        applicationContext,
        R.array.categories,
        android.R.layout.simple_spinner_item
      )

    binding.spnCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        book = book.copy(category = resources.getStringArray(R.array.categories)[position])
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
      }

    }
    binding.spnCategory.adapter = spinnerAdapter

    binding.btnAdd.setOnClickListener {
      db.bookDao().insert(book)
      intent.putExtra("book", book)
      setResult(Activity.RESULT_OK, intent)

      finish()
    }
  }
}