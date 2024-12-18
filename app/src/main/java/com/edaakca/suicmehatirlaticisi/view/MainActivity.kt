package com.edaakca.suicmehatirlaticisi.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.edaakca.suicmehatirlaticisi.R

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val sharedPreferences = getSharedPreferences("com.edaakca.suicmehatirlaticisi.view", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("ilkBaslatma", true)

        if (isFirstLaunch) {
            // İlk kez açılıyorsa BaşlangıçFragment'e yönlendir
            sharedPreferences.edit().putBoolean("ilkBaslatma", false).apply()
            navController.navigate(R.id.baslangicFragment)
        } else {
            // Daha önce açılmışsa EklemeFragment'a yönlendir
            navController.navigate(R.id.eklemeFragment)
        }
    }
}
