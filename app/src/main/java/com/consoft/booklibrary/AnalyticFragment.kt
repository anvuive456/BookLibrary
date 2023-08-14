package com.consoft.booklibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.consoft.booklibrary.databinding.FragmentAnalyticBinding

class AnalyticFragment : Fragment() {
  lateinit var binding: FragmentAnalyticBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding =
      FragmentAnalyticBinding.inflate(LayoutInflater.from(requireContext()), container, false)
    return binding.root
  }



}