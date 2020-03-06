package com.example.testapp

import android.R
import android.os.AsyncTask
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class JSONFetcher(textView: TextView, listView: ListView, context: MainActivity) : AsyncTask<URL, Int, List<String>>(){

    val innerContext = context
    val innerTextView: TextView = textView
    val innerListView: ListView = listView

    override fun doInBackground(vararg params: URL): List<String> {

        val matchesText: String = params[0].readText()
        val matches: JSONArray = JSONArray(matchesText)
        var countries: ArrayList<String> = ArrayList<String>()

        for(i in 0 until matches.length()){
            var event: JSONObject = matches.getJSONObject(i)
            val home_team: JSONObject = event.getJSONObject("home_team")
            val home_team_country: String = home_team.getString("country")
            val home_team_code: String = home_team.getString("code")
            var country: String = home_team_country + " " + home_team_code
            countries.add(country)
        }
        return countries
    }

    override fun onPostExecute(result: List<String>) {

        innerTextView.text = "Choose your favourite team: "
        val adapter = ArrayAdapter(innerContext, android.R.layout.simple_list_item_1, result)
        innerListView.adapter = adapter
        super.onPostExecute(result)
    }

}