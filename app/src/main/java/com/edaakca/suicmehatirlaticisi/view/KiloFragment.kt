package com.edaakca.suicmehatirlaticisi.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.edaakca.suicmehatirlaticisi.R
import com.edaakca.suicmehatirlaticisi.databinding.FragmentKiloBinding

class KiloFragment : Fragment() {

    private var _binding: FragmentKiloBinding? = null
    private val binding get() = _binding!!


    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences("com.edaakca.suicmehatirlaticisi.view", Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKiloBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cinsiyeti almak için sharedPreferences'tan veri çektim
        val cinsiyet = sharedPreferences.getString("cinsiyet", "kadın") // Varsayılan değer 'kadın'

        val genderImageView: ImageView = binding.cinsiyetImageView

        // Cinsiyete göre resim seçimi
        when (cinsiyet) {
            "kadın" -> genderImageView.setImageResource(R.drawable.kadinkilo)
            "erkek" -> genderImageView.setImageResource(R.drawable.erkekkilo)

        }
        binding.kiloIleriButton.setOnClickListener {
            val kilo = binding.kgText.text.toString().toIntOrNull() ?: 0
            saveKilo(kilo)
            findNavController().navigate(R.id.action_kiloFragment_to_hesapFragment)
        }
        binding.kiloGeriButton.setOnClickListener {
            findNavController().navigate(R.id.action_kiloFragment_to_cinsiyetFragment)
        }
    }

    private fun saveKilo(kilo: Int) {
        sharedPreferences.edit().putInt("kilo", kilo).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}