package com.edaakca.suicmehatirlaticisi.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edaakca.suicmehatirlaticisi.R
import com.edaakca.suicmehatirlaticisi.databinding.FragmentArsivBinding
import com.edaakca.suicmehatirlaticisi.databinding.FragmentEklemeBinding


class ArsivFragment : Fragment() {
    private var _binding: FragmentArsivBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ArsivAdapter
    private val hedefeUlasilanGunler = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArsivBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireContext().getSharedPreferences("com.edaakca.suicmehatirlaticisi.view", Context.MODE_PRIVATE)
        val gunler = sharedPreferences.getStringSet("hedefeUlasilanGunler", mutableSetOf())
        hedefeUlasilanGunler.addAll(gunler ?: emptySet())

        adapter = ArsivAdapter(hedefeUlasilanGunler)
        binding.kayitRecyclerView.adapter = adapter
        binding.kayitRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_arsivFragment_to_eklemeFragment)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}