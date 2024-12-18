
package com.edaakca.suicmehatirlaticisi.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edaakca.suicmehatirlaticisi.R
import com.edaakca.suicmehatirlaticisi.databinding.FragmentEklemeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EklemeFragment : Fragment() {
    private var _binding: FragmentEklemeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bugunKayitTextView: TextView

    private lateinit var adapter: SuKaydiAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var toplamSu: Int = 0
    private lateinit var guncelTarih: String
    private val suKayitlari = mutableListOf<SuKaydi>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEklemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bugunKayitTextView = view.findViewById(R.id.bugunKayitTextView)

        // guncelTarih değişkenini burada başlatıyoruz
        guncelTarih = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        
        sharedPreferences = requireContext().getSharedPreferences("com.edaakca.suicmehatirlaticisi.view", Context.MODE_PRIVATE)

        adapter = SuKaydiAdapter(suKayitlari)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Bilgiler güncellenmişse sıfırla
        val bilgiGuncellemesi = sharedPreferences.getBoolean("bilgiGuncellemesi", false)
        if (bilgiGuncellemesi) {
            resetSuMiktari()
            sharedPreferences.edit().putBoolean("bilgiGuncellemesi", false).apply()
        }
        
        yeniGunKontrol()
        toplamSu = sharedPreferences.getInt("totalWater", 0)
        suKayitlariYukle()
        guncelleSuMiktariTextView()

        val waterButton: View = view.findViewById(R.id.waterButton)
        waterButton.setOnClickListener { view ->
            // TextView'i etkin hale getirdim
            bugunKayitTextView.visibility = View.VISIBLE

            showPopupMenu(view)

        }

        binding.resetButton.setOnClickListener {
            resetSuMiktari()
        }
        binding.guncelleButton.setOnClickListener {
            findNavController().navigate(R.id.action_eklemeFragment_to_cinsiyetFragment)
        }
        binding.kayitButton.setOnClickListener {
            findNavController().navigate(R.id.action_eklemeFragment_to_arsivFragment)
        }

    }
    private fun yeniGunKontrol() {
        val kaydedilenTarih = sharedPreferences.getString("sonTarih", null)
        if (kaydedilenTarih != guncelTarih) {
            resetSuMiktari()
            sharedPreferences.edit().putString("sonTarih", guncelTarih).apply()
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.my_popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add_100ml -> {
                    ekleSuMiktari(100)
                    true
                }
                R.id.add_200ml -> {
                    ekleSuMiktari(200)
                    true
                }
                R.id.add_500ml -> {
                    ekleSuMiktari(500)
                    true
                }
                R.id.add_1000ml -> {
                    ekleSuMiktari(1000)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
    private fun ekleSuMiktari(miktar: Int) {
        yeniGunKontrol()
        toplamSu += miktar

        val simdikiZaman = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        suKayitlari.add(SuKaydi(simdikiZaman, miktar))
        adapter.notifyItemInserted(suKayitlari.size - 1)

        kaydetSuKayitlari()
        sharedPreferences.edit().putInt("totalWater", toplamSu).apply()

        guncelleSuMiktariTextView()
        Snackbar.make(binding.root, "$miktar ml Eklendi", Snackbar.LENGTH_LONG).show()

        val gunlukSuIhtiyaci = sharedPreferences.getInt("gunlukSuIhtiyaci", 0)
        if (toplamSu >= gunlukSuIhtiyaci) {
            binding.mesajText.text = "Tebrikler, Hedefe Ulaştınız.!"
            hedefeUlasildi()
        } else {
            binding.mesajText.text = ""
        }

    }
    private fun hedefeUlasildi() {
        val tarih = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val hedefeUlasilanGunler = sharedPreferences.getStringSet("hedefeUlasilanGunler", mutableSetOf())
        hedefeUlasilanGunler?.add(tarih)
        sharedPreferences.edit().putStringSet("hedefeUlasilanGunler", hedefeUlasilanGunler).apply()
    }
    private fun ekleSu(miktar: Int) {
        yeniGunKontrol()
        toplamSu += miktar

        val simdikiZaman = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())


        // SharedPreferences ve RecyclerView güncellemeleri
        kaydetSuKayitlari()
        sharedPreferences.edit().putInt("totalWater", toplamSu).apply()
        guncelleSuMiktariTextView()
        Snackbar.make(binding.root, "$miktar ml Eklendi", Snackbar.LENGTH_LONG).show()

        // Günlük su ihtiyacını kontrol et
        val gunlukSuIhtiyaci = sharedPreferences.getInt("gunlukSuIhtiyaci", 0)
        if (toplamSu >= gunlukSuIhtiyaci) {
            binding.mesajText.text = "Tebrikler, Hedefe Ulaştınız.!"
        } else {
            binding.mesajText.text = ""
        }
    }

    private fun kaydetSuKayitlari() {
        val gson = Gson()
        val json = gson.toJson(suKayitlari)
        sharedPreferences.edit().putString("suKayitlari", json).apply()
    }

    private fun suKayitlariYukle() {
        val gson = Gson()
        val json = sharedPreferences.getString("suKayitlari", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<SuKaydi>>() {}.type
            val kayitlar = gson.fromJson<MutableList<SuKaydi>>(json, type)
            suKayitlari.clear()
            suKayitlari.addAll(kayitlar)
        }
    }

    private fun guncelleSuMiktariTextView() {
        binding.waterText.text = "$toplamSu ml"
    }

    private fun resetSuMiktari() {

        toplamSu = 0
        binding.mesajText.text = ""
        suKayitlari.clear() // Su kayıtlarını temizliyoruz
        adapter.notifyDataSetChanged() // RecyclerView'ı güncelliyor

        sharedPreferences.edit().putInt("totalWater", toplamSu).apply()
        kaydetSuKayitlari()

        // Resete tıklayınca Hedefe ulaşılan kaydın gününü de silmek için
        val hedefeUlasilanGunler = sharedPreferences.getStringSet("hedefeUlasilanGunler", mutableSetOf()) ?: mutableSetOf()
        hedefeUlasilanGunler.remove(guncelTarih)
        sharedPreferences.edit().putStringSet("hedefeUlasilanGunler", hedefeUlasilanGunler).apply()

        guncelleSuMiktariTextView()
        Snackbar.make(binding.root, "Su miktarı sıfırlandı", Snackbar.LENGTH_LONG).show()
        bugunKayitTextView.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}