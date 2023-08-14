package com.consoft.booklibrary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.consoft.booklibrary.adapter.MemberAdapter
import com.consoft.booklibrary.databinding.FragmentMemberBinding
import com.consoft.booklibrary.db.AppDatabase
import com.consoft.booklibrary.model.Member
import com.consoft.booklibrary.model.MemberWithBooks

class MemberFragment : Fragment() {
  lateinit var binding: FragmentMemberBinding
  lateinit var db: AppDatabase
  lateinit var adapter: MemberAdapter
  var members = mutableListOf<MemberWithBooks>()
  var searchText = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    db = AppDatabase.getDatabase(requireContext())

    binding = FragmentMemberBinding.inflate(inflater, container, false)


    adapter = MemberAdapter(members, onClick = {
      val intent = Intent(requireContext(), AddMemberActivity::class.java)
      intent.putExtra("member", it)
      updateLauncher.launch(intent)
    },
      onLongClick = {
        db.memberDao().delete(it)
        loadMembers()
      }
    )
    binding.rcMember.layoutManager = LinearLayoutManager(requireContext())
    binding.rcMember.adapter = adapter


    binding.searchMember.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
          searchText = query
        } else searchText = ""
        loadMembers()
        return true
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        return false
      }

    })
    binding.btnAddMember.setOnClickListener {
      val intent = Intent(requireContext(), AddMemberActivity::class.java)
      addLauncher.launch(intent)
    }

    loadMembers()
    return binding.root
  }

  //lắng nghe sau khi add member
  val addLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (it.resultCode == Activity.RESULT_OK) {
      loadMembers()
    }
  }
  //lắng nghe sau khi update member
  val updateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (it.resultCode == Activity.RESULT_OK) {
      loadMembers()
    }
  }

  fun loadMembers() {
    members.clear()
    members.addAll(db.memberDao().findMany(searchText))

    adapter.notifyDataSetChanged()
  }

}