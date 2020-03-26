package com.test.pokedex.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import com.test.pokedex.R
import java.util.*

class ActivityInfo : AppCompatActivity() {
    private var context: Context = this
    private var num: String = ""
    private lateinit var data: JsonObject
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var imagePokemon: ImageView
    private lateinit var namePokemon: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        checkIntent()
        initializeComponents()
        initializeData()

    }

    private fun initializeComponents() {
        imagePokemon = findViewById(R.id.PokemonImage)
        namePokemon = findViewById(R.id.nombre)
    }

    private fun checkIntent() {
        if (intent != null)
            num = intent.getStringExtra("pokemon")


    }

    private fun addInfo(infoText: String, layoutId: Int) {
        val linearLayout: LinearLayout = findViewById(layoutId)
        var info = TextView(this)
        info.textSize = 13f
        info.text = infoText
        info.gravity = Gravity.CENTER
        linearLayout.addView(info)
    }

    private fun initializeData() {
        Ion.with(context)
            .load("https://pokeapi.co/api/v2/pokemon/"+num+"/")
            .asJsonObject()
            .done { e, result ->
                if (e == null) {
                    data = result
                    if (!data.get("sprites").isJsonNull) {
                        if (data.get("sprites").asJsonObject.get("front_default") != null) {
                            //Pintar
                            Glide
                                .with(context)
                                .load(data.get("sprites").asJsonObject.get("front_default").asString)
                                .placeholder(R.drawable.pokemon_logo_min)
                                .error(R.drawable.pokemon_logo_min)
                                .into(imagePokemon)

                        } else {
                            imagePokemon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pokemon_logo_min))
                        }
                    }else{
                        imagePokemon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pokemon_logo_min))
                    }

                    if (data.get("name") != null) {
                        val nombre: String = num + " " + (this.data.get("name").toString().replace("\"", "").toUpperCase(Locale.ROOT))
                        namePokemon.text = nombre

                    }

                    if (!data.get("types").isJsonNull) {
                        val types = data.get("types").asJsonArray
                        for (i in 0.until(types.size())) {
                            val poke = types.get(i).asJsonObject.get("type").asJsonObject
                            val typeName = poke.get("name").toString().replace("\"", "")
                            addInfo(typeName,R.id.PokemonTypes)
                        }
                    }

                    if (!data.get("stats").isJsonNull) {
                        val stats = data.get("stats").asJsonArray
                        for (i in 0.until(stats.size())) {
                            val baseStats = stats.get(i).asJsonObject.get("base_stat")
                            val nameBaseStats = stats.get(i).asJsonObject.get("stat").asJsonObject.get("name")
                            val final = baseStats.toString() + " - " + nameBaseStats.toString().replace("\"", "")
                            addInfo(final,R.id.PokemonBaseStats)
                        }
                    }

                    if (!data.get("moves").isJsonNull) {
                        val pokemonMovements = data.get("moves").asJsonArray
                        for (i in 0.until(pokemonMovements.size())) {
                            val movements = pokemonMovements.get(i).asJsonObject.get("move").asJsonObject.get("name")
                            val final = movements.toString().replace("\"", "")
                            addInfo(final, R.id.PokemonMovements)
                        }
                    }
                }
                initializeList()
            }

    }

    private fun initializeList() {
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

    }
}
