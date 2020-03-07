package com.example.testapp

import android.os.AsyncTask
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import org.json.JSONArray
import java.net.URL

data class Event(val index:Int, val teamKey: String, val eventKey: String){

}

class TEAMFetcher(val teamCountry: String, val listView: ListView, val context: Homepage): AsyncTask<URL, Void, MutableMap<String, MutableList<Int>>>() {

    override fun doInBackground(vararg params: URL): MutableMap<String, MutableList<Int>>{

        val matchesText: String = params[0].readText()
        val matches: JSONArray = JSONArray(matchesText)
        var teamMembers: MutableMap<String, MutableList<Int>> = mutableMapOf<String, MutableList<Int>>()
        var eventList: MutableList<Event> = mutableListOf()

        //find events in which the selected team participated in
        for(i in 0 until matches.length() )
        {
            var index: Int = 0
            var teamKey: String = ""
            var eventKey: String = ""
            val event = matches.getJSONObject(i)
            if(event.getString("home_team_country") == teamCountry){
                teamKey = "home_team_statistics"
                eventKey = "home_team_events"
                index = i
                val item = Event(index, teamKey, eventKey)
                eventList.add(item)
            }
            else if(event.getString("away_team_country") == teamCountry){
                teamKey= "away_team_statistics"
                eventKey = "away_team_events"
                index = i
                val item = Event(index, teamKey, eventKey)
                eventList.add(item)
            }
        }

        //add team members to the hash map
        for(teamEvent in eventList) {
            val startingEleven =
                matches.getJSONObject(teamEvent.index).getJSONObject(teamEvent.teamKey).getJSONArray("starting_eleven")
            val substitutes =
                matches.getJSONObject(teamEvent.index).getJSONObject(teamEvent.teamKey).getJSONArray("substitutes")
            for (i in 0 until startingEleven.length()) {
                teamMembers[startingEleven.getJSONObject(i).getString("name")] =
                    mutableListOf(0, 0, 0)
            }
            for (i in 0 until substitutes.length()) {
                teamMembers[substitutes.getJSONObject(i).getString("name")] = mutableListOf(0, 0, 0)
            }
        }

        //"player" : {number_of_goals, number_of_yellow_card, number_of_red_cards}
        //add events to members
        for(teamEvent in eventList) {
            val events = matches.getJSONObject(teamEvent.index).getJSONArray(teamEvent.eventKey)
            for (i in 0 until events.length()) {
                val player = events.getJSONObject(i).getString("player")
                val event = events.getJSONObject(i).getString("type_of_event")
                val info: MutableList<Int> = teamMembers[player]!!
                var infoUpdated: MutableList<Int> = mutableListOf(0, 0, 0)
                if (event == "goal" || event == "goal-penalty") {
                    infoUpdated[0] = info[0] + 1
                    infoUpdated[1] = info[1]
                    infoUpdated[2] = info[2]
                    if (teamMembers != null) teamMembers[player] = infoUpdated
                } else if (event == "yellow-card") {
                    infoUpdated[0] = info[0]
                    infoUpdated[1] = info[1] + 1
                    infoUpdated[2] = info[2]
                    if (teamMembers != null) teamMembers[player] = infoUpdated
                } else if (event == "red-card") {
                    infoUpdated[0] = info[0]
                    infoUpdated[1] = info[1]
                    infoUpdated[2] = info[2] + 1
                    if (teamMembers != null) teamMembers[player] = infoUpdated
                }
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


