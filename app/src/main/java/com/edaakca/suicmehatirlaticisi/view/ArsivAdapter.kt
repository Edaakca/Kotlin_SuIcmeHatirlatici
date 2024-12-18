package com.edaakca.suicmehatirlaticisi.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edaakca.suicmehatirlaticisi.R

class ArsivAdapter(private val gunler: List<String>) : RecyclerView.Adapter<ArsivAdapter.GunViewHolder>() {

    class GunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tarihTextView: TextView = itemView.findViewById(R.id.tarihTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hedefe_ulasilan_gun, parent, false)
        return GunViewHolder(view)
    }

    override fun onBindViewHolder(holder: GunViewHolder, position: Int) {
        val tarih = gunler[position]
        holder.tarihTextView.text = tarih
    }

    override fun getItemCount() = gunler.size
}