package com.edaakca.suicmehatirlaticisi.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edaakca.suicmehatirlaticisi.databinding.ItemSuKaydiBinding

class SuKaydiAdapter(private val suKayitlari: List<SuKaydi>) :
    RecyclerView.Adapter<SuKaydiAdapter.WaterRecordViewHolder>() {

    class WaterRecordViewHolder(val binding: ItemSuKaydiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterRecordViewHolder {
        val binding = ItemSuKaydiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WaterRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WaterRecordViewHolder, position: Int) {
        val kayit = suKayitlari[position]
        holder.binding.timeTextView.text = kayit.zaman
        holder.binding.amountTextView.text = "${kayit.miktar} ml"
    }

    override fun getItemCount(): Int = suKayitlari.size
}