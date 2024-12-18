package com.edaakca.suicmehatirlaticisi.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.edaakca.suicmehatirlaticisi.R
import com.edaakca.suicmehatirlaticisi.databinding.FragmentHesapBinding


class HesapFragment : Fragment() {

    private var _binding: FragmentHesapBinding? = null
    private val binding get() = _binding!!


    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("com.edaakca.suicmehatirlaticisi.view", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHesapBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cinsiyet = sharedPreferences.getString("cinsiyet", "kadın")
        val kilo = sharedPreferences.getInt("kilo", 0)

        val sonuc = if (cinsiyet == "kadın") {
            kilo * 35
        } else {
            kilo * 40
        }
        binding.hesapTextView.text = "Günlük Su İhtiyacınız \n$sonuc ml"

        sharedPreferences.edit().putInt("gunlukSuIhtiyaci", sonuc).apply()

        binding.takipButton.setOnClickListener {
            findNavController().navigate(R.id.action_hesapFragment_to_uyanmaFragment)
        }
        binding.geriButton.setOnClickListener {
            findNavController().navigate(R.id.action_hesapFragment_to_cinsiyetFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}