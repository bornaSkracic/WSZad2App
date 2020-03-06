package com.example.testapp

import android.os.AsyncTask
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import org.json.JSONArray
import java.net.URL

class TEAMFetcher(val teamCountry: String, listView: ListView, context: Homepage): AsyncTask<URL, Void, MutableMap<String, MutableList<Int>>>() {
    val listView = listView
    val context = context

    override fun doInBackground(vararg params: URL): MutableMap<String, MutableList<Int>>{
        val matchesText: String = params[0].readText()
        val matches: JSONArray = JSONArray(matchesText)
        var teamMembers: MutableMap<String, MutableList<Int>> = mutableMapOf<String, MutableList<Int>>()

        var index: Int = 0
        var teamKey: String = ""
        var eventKey: String = ""
        for(i in 0 until matches.length() )
        {
            val event = matches.getJSONObject(i)
            if(event.getString("home_team_country") == teamCountry){
                teamKey = "home_team_statistics"
                eventKey = "home_team_events"
                index = i
                break
            }
            else if(event.getString("away_team_country") == teamCountry){
                teamKey= "away_team_statistics"
                eventKey = "away_team_events"
                index = i
                break
            }
        }

        //add team members to the hash map
        val startingEleven = matches.getJSONObject(index).getJSONObject(teamKey).getJSONArray("starting_eleven")
        val substitutes = matches.getJSONObject(index).getJSONObject(teamKey).getJSONArray("substitutes")
        for(i in 0 until startingEleven.length()) {
                teamMembers.put(startingEleven.getJSONObject(i).getString("name"), mutableListOf(0, 0, 0))
        }
        for(i in 0 until substitutes.length()){
            teamMembers.put(substitutes.getJSONObject(i).getString("name"), mutableListOf(0, 0, 0))
        }

        //"player" : {number_of_goals, number_of_yellow_card, number_of_red_cards}
        //add events to members
        val events = matches.getJSONObject(index).getJSONArray(eventKey)
        val info = mutableListOf<Int>()
        for(i in 0 until events.length()) {
            val player = events.getJSONObject(i).getString("player")
            val event = events.getJSONObject(i).getString("type_of_event")
            val info: MutableList<Int> = teamMembers[player]!!
            var infoUpdated: MutableList<Int> = mutableListOf(0, 0, 0)
            if (event == "goal" || event == "goal-penalty" || event == "goal-own") {
                infoUpdated[0] = info[0] + 1
                infoUpdated[1] = info[1]
                infoUpdated[2] = info[2]
            } else if (event == "yellow-card") {
                infoUpdated[0] = info[0]
                infoUpdated[1] = info[1] + 1
                infoUpdated[2] = info[2]
            } else if (event == "red-card") {
                infoUpdated[0] = info[0]
                infoUpdated[1] = info[1]
                infoUpdated[2] = info[2] + 1
            }

            if (teamMembers != null) {
                teamMembers[player] = infoUpdated
            }

        }

        return teamMembers
    }

    override fun onPostExecute(result: MutableMap<String, MutableList<Int>>) {
        var list: MutableList<String> = mutableListOf()

        val goalPic = 0x26BD
        val yellowCardPic = 0x1F7E8
        val redCardPic = 0x1F7E5
        for(player in result){
            var item: String = player.key + "        "
            if(player.value[0] > 0){
                item += player.value[0].toString() + " " + String(Character.toChars(goalPic)) + "  "
            }
            if(player.value[1] > 0){
                item += player.value[1].toString() + " " + String(Character.toChars(yellowCardPic)) + "  "
            }
            if(player.value[2] > 0){
                item += player.value[2].toString() + " " + String(Character.toChars(redCardPic))
            }
            list.add(item)
        }

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
        listView.visibility = View.VISIBLE
        super.onPostExecute(result)
    }
}


