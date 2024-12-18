package com.edaakca.suicmehatirlaticisi.view

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import com.edaakca.suicmehatirlaticisi.R
import com.edaakca.suicmehatirlaticisi.databinding.FragmentUyanmaBinding
import java.util.Calendar

class UyanmaFragment : Fragment() {

    private var _binding: FragmentUyanmaBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private var uyandirmaZamaniMili: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUyanmaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(
            "com.edaakca.suicmehatirlaticisi.view",
            Context.MODE_PRIVATE
        )

        val cinsiyet = sharedPreferences.getString("cinsiyet", "kadın") // Varsayılan değer 'kadın'
        val genderImageView: ImageView = binding.uyanmaImageView

        when (cinsiyet) {
            "kadın" -> genderImageView.setImageResource(R.drawable.kadinuyanma)
            "erkek" -> genderImageView.setImageResource(R.drawable.erkekuyanma)
        }

        // Alarm iznini kontrol et ve iste
        AlarmIzniIste()

        binding.saatSec.setOnClickListener {
            showTimePicker()
        }

        binding.uyanmaIleriButton.setOnClickListener {
            if (uyandirmaZamaniMili != null) {
                sharedPreferences.edit().putLong("uyanmaZamani", uyandirmaZamaniMili!!).apply()
                findNavController().navigate(R.id.action_uyanmaFragment_to_uykuFragment)
            } else {
                Toast.makeText(requireContext(), "Lütfen bir uyanma zamanı seçin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun AlarmIzniIste() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("İzin Gerekiyor")
                builder.setMessage("Uygulamanın tam alarm planlama iznine ihtiyacı var.")
                builder.setPositiveButton("İzin Ver") { _, _ ->
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    startActivity(intent)
                }
                builder.setNegativeButton("İptal") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val saat = calendar.get(Calendar.HOUR_OF_DAY)
        val dakika = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(),
            { _, secilenSaat, secilenDakika ->
                kaydetUyanmaZamani(secilenSaat, secilenDakika)
            },
            saat,
            dakika,
            true
        )
        timePicker.show()
    }

    private fun kaydetUyanmaZamani(saat: Int, dakika: Int) {
        uyandirmaZamaniMili = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, saat)
            set(Calendar.MINUTE, dakika)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        binding.saatSec.setText(String.format("%02d:%02d", saat, dakika))
        Toast.makeText(requireContext(), "Uyanma zamanı kaydedildi", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
