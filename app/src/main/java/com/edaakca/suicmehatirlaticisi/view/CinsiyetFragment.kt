package com.edaakca.suicmehatirlaticisi.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.edaakca.suicmehatirlaticisi.R
import com.edaakca.suicmehatirlaticisi.databinding.FragmentCinsiyetBinding


class CinsiyetFragment : Fragment() {

    private var _binding: FragmentCinsiyetBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("com.edaakca.suicmehatirlaticisi.view", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCinsiyetBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireContext().getSharedPreferences("com.edaakca.suicmehatirlaticisi.view", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("bilgiGuncellemesi", true).apply()

        val kadinButton: ImageView = binding.kadinImageView
        kadinButton.setOnClickListener {

            saveCinsiyet("kadın")
            findNavController().navigate(R.id.action_cinsiyetFragment_to_kiloFragment)
        }

        val erkekButton: ImageView = binding.erkekImageView
        erkekButton.setOnClickListener {

            saveCinsiyet("erkek")
            findNavController().navigate(R.id.action_cinsiyetFragment_to_kiloFragment)
        }

    }

    private fun saveCinsiyet(cinsiyet: String) {
        sharedPreferences.edit().putString("cinsiyet", cinsiyet).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}