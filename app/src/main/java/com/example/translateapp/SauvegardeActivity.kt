package com.example.translateapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.translateapp.database.entity.Dictionnaire
import com.example.translateapp.database.entity.Mot
import com.example.translateapp.databinding.ActivitySauvegardeBinding

class SauvegardeActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySauvegardeBinding.inflate(layoutInflater) }

    private val model by lazy { ViewModelProvider(this)[ViewModel::class.java] }

    private var dicoID: Long = -1
    private lateinit var dicoURL : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val button = binding.button
        button.setOnClickListener { addMot() }

        if (intent.action.equals("android.intent.action.SEND")) {
            val url = intent.extras?.getString("android.intent.extra.TEXT")
            if (url != null) {
                dicoURL = url
            }
        }

        if (savedInstanceState != null) {
            binding.langueSrcToBD.setSelection(savedInstanceState.getInt("langue1", 0))
            binding.langueDestToBD.setSelection(savedInstanceState.getInt("langue2", 0))
            binding.wordToBD.setText(savedInstanceState.getString("word"))
            binding.translateToBD.setText(savedInstanceState.getString("translate"))
        }

        model.certainsDictionnaires.observe(this) {
            Log.i("INSERT TEST", "J'ai ${it.size} dictionnaires")
            val motInit = binding.wordToBD.text.toString().trim()
            val motTrad = binding.translateToBD.text.toString().trim()
            val langueInit = binding.langueSrcToBD.selectedItem.toString()
            val langueTrad = binding.langueDestToBD.selectedItem.toString()
            // si oui mettre a jour les vars
            if (it.isNotEmpty()) {
                dicoID = model.certainsDictionnaires.value!![0].idDico
                model.loadMot(motInit, langueTrad)
            } else {
                // sinon creer un dico + insertDico + mettre a jour les vars
                val dico = Dictionnaire(dicoURL, langueInit, langueTrad)
                val mot = Mot(motInit, motTrad, dicoURL, dicoURL, true, langueInit, langueTrad, 0)
                model.insertMotAndDictionnaireOfMot(mot, dico)
                Log.i("INSERT TEST", "On a pas de dico")
                val act = Intent(this, MainActivity::class.java)
                startActivity(act)
            }
        }

        model.certainsMots.observe(this) {
            Log.i("EXAMEN", "J'ai ${it.size} mots")
            val motInit = binding.wordToBD.text.toString().trim()
            val motTrad = binding.translateToBD.text.toString().trim()
            val langueInit = binding.langueSrcToBD.selectedItem.toString()
            val langueTrad = binding.langueDestToBD.selectedItem.toString()
            if (it.isEmpty()) {
                //Le mot n'a pas encore été ajouté à la bdd
                val mot = Mot(motInit, motTrad, dicoURL, dicoURL, true, langueInit, langueTrad, 0)
                model.insertMot(mot)
                Log.i("EXAMEN", "On a un dico qui existe, et pas de mot existant")
            } else {
                val text = "Ce mot est déjà présent dans nos données."
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
                Log.i("INSERT TEST", "On a un dico qui existe, et un mot existant")
            }
            val act = Intent(this, MainActivity::class.java)
            startActivity(act)
        }
    }

    private fun addMot() {
        val motInit = binding.wordToBD.text.toString().trim()
        val motTrad = binding.translateToBD.text.toString().trim()
        if (motInit != "" && motTrad != "") {
            val langueInit = binding.langueSrcToBD.selectedItem.toString()
            val langueTrad = binding.langueDestToBD.selectedItem.toString()
            model.loadDictionnaire(dicoURL, langueInit, langueTrad)
        } else {
            val text = "Remplissez les champs !"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("langue1", binding.langueSrcToBD.selectedItemPosition)
        outState.putInt("langue2", binding.langueDestToBD.selectedItemPosition)
        outState.putString("word", binding.wordToBD.text.toString())
        outState.putString("translate", binding.translateToBD.text.toString())
    }

}