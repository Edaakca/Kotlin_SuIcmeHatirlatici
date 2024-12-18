package com.edaakca.suicmehatirlaticisi.view

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import com.edaakca.suicmehatirlaticisi.R
import com.edaakca.suicmehatirlaticisi.databinding.FragmentUykuBinding
import java.util.Calendar

class UykuFragment : Fragment() {

    private var _binding: FragmentUykuBinding? = null
    private val binding get() = _binding!!

    private var uyanmaZamaniMili: Long? = null
    private var uykuZamaniMili: Long? = null

    private lateinit var sharedPreferences: SharedPreferences

    // Bildirim izni isteme sonucu
    private val requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Bildirim izinleri başarıyla alındı.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Bildirim izinleri reddedildi.", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestAlarmPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(requireContext(), "Tam alarm izinleri başarıyla alındı.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Tam alarm izinleri reddedildi.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUykuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("com.edaakca.suicmehatirlaticisi.view", Context.MODE_PRIVATE)

        val cinsiyet = sharedPreferences.getString("cinsiyet", "kadın")

        val genderImageView: ImageView = binding.uyumaImageView

        when (cinsiyet) {
            "kadın" -> genderImageView.setImageResource(R.drawable.kadinuyuma)
            "erkek" -> genderImageView.setImageResource(R.drawable.erkekuyuma)
        }

        // Alarm iznini kontrol et ve iste
        AlarmIzniniIste()
        // Bildirim iznini kontrol et ve iste
        BildirimIzniIste()

        uyanmaZamaniMili = sharedPreferences.getLong("uyanmaZamani", -1L)

        binding.uykuSec.setOnClickListener {
            showTimePicker()
        }
        binding.uyumaIleriButton.setOnClickListener {
            if (uyanmaZamaniMili != null && uykuZamaniMili != null) {
                try {
                    sharedPreferences.edit().putLong("uyumaZamani", uykuZamaniMili!!).apply()
                    saatlikBildirimleriPlanla(uyanmaZamaniMili!!, uykuZamaniMili!!)
                    findNavController().navigate(R.id.action_uykuFragment_to_eklemeFragment)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen bir uyuma zamanı seçin.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.uyumaGeriButton.setOnClickListener {
            findNavController().navigate(R.id.action_uykuFragment_to_uyanmaFragment)
        }
    }

    private fun AlarmIzniniIste() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("İzin Gerekiyor")
                builder.setMessage("Doğru bir şekilde çalışabilmesi için uygulamanın tam alarm planlama iznine ihtiyacı var.")
                builder.setPositiveButton("İzin Ver") { _, _ ->
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    requestAlarmPermissionLauncher.launch(intent)
                }
                builder.setNegativeButton("İptal") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }

    private fun BildirimIzniIste() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun saatlikBildirimleriPlanla(uyanmaZamaniMili: Long, uykuZamaniMili: Long) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), BildirimAlicisi::class.java)
        var simdikiZamanMili = uyanmaZamaniMili

        while (simdikiZamanMili < uykuZamaniMili) {
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                (simdikiZamanMili % Int.MAX_VALUE).toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    simdikiZamanMili,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    simdikiZamanMili,
                    pendingIntent
                )
            }
            simdikiZamanMili += 3600000
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val saat = calendar.get(Calendar.HOUR_OF_DAY)
        val dakika = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(),
            { _, secilenSaat, secilenDakika ->
                kaydetUyumaZamani(secilenSaat, secilenDakika)
            },
            saat,
            dakika,
            true
        )
        timePicker.show()
    }

    private fun kaydetUyumaZamani(saat: Int, dakika: Int) {
        uykuZamaniMili = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, saat)
            set(Calendar.MINUTE, dakika)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        binding.uykuSec.setText(String.format("%02d:%02d", saat, dakika))
        Toast.makeText(requireContext(), "Uyuma zamanı kaydedildi", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
