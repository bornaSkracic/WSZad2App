package com.example.testapp
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.net.URL

class MainActivity : AppCompatActivity() {

    val PREF_TEAM = "team"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("Team statistics viewer")

        val sharedPreferences = getSharedPreferences(PREF_TEAM, 0)

        if(sharedPreferences.getString(PREF_TEAM, "none") != "none"){
            val intent: Intent = Intent(this@MainActivity, Homepage::class.java)
            intent.putExtra("TEAM", sharedPreferences.getString(PREF_TEAM, "none"))
            finish()
            startActivity(intent)
        }


        //Text message
        val startMessage: String = "Please wait a moment..."
        this.textView.text = startMessage

        val context = this
        //JSON fetching
        val url: URL = URL("https://world-cup-json-2018.herokuapp.com/matches")
        JSONFetcher(this.textView, this.listView_menu, context).execute(url)

        var selectedTeam: String = ""

        listView_menu.onItemClickListener = object: AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            )
            {
                var country = listView_menu.getItemAtPosition(position).toString()
                var teamCountry: String = ""

                for(i in 0 until country.length){
                    if(country[i] == ' ') break
                    teamCountry = teamCountry + country[i]
                }
                //write to preferences
                val editor = sharedPreferences.edit()
                editor.putString(PREF_TEAM, teamCountry)
                editor.apply()
                //start new activity
                val intent: Intent = Intent(this@MainActivity, Homepage::class.java)
                intent.putExtra("TEAM", teamCountry)
                finish()
                startActivity(intent)
            }

        }
    }

}
