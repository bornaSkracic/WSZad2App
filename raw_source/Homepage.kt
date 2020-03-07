package com.example.testapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL

class Homepage : AppCompatActivity() {

    val PREF_TEAM = "team"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val country : String = getIntent().getStringExtra("TEAM")
        setTitle(country)

        val sharedPreferences = getSharedPreferences("PREF_NAME", 0)
        val editor = sharedPreferences.edit()
        editor.putString(PREF_TEAM, country)
        editor.apply()

        val context = this
        val url: URL = URL("https://world-cup-json-2018.herokuapp.com/matches")
        TEAMFetcher(country, this.listView_menu2, context).execute(url)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sharedPreferences = getSharedPreferences(PREF_TEAM, 0)
        val editor = sharedPreferences.edit()
        editor.putString(PREF_TEAM, "none")
        editor.apply()

        val intent = Intent(this@Homepage, MainActivity::class.java)
        finish()
        startActivity(intent)

        return super.onOptionsItemSelected(item)
    }
}
