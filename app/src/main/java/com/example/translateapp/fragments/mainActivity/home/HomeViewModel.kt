package com.example.translateapp.fragments.mainActivity.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.translateapp.database.DicoBD
import com.example.translateapp.database.entity.Dictionnaire
import com.example.translateapp.database.entity.Mot
import kotlin.concurrent.thread

class HomeViewModel(application: Application) : AndroidViewModel(application) {


    private val dao = DicoBD.getDatabase(application).MyDao()

    /*
    ---------------
    */

    var certainsMots = MutableLiveData<List<Mot>>()

    fun loadMot(mot: String, endLang: String) = thread{ certainsMots.postValue(dao.loadMot(mot, endLang))}

    /*
    ---------------
     */

    var certainsDictionnaires = MutableLiveData<List<Dictionnaire>>()

    var currentLangDictionnaires = MutableLiveData<List<Dictionnaire>>()


    fun loadDictionnaire(url: String, startLang: String, endLang: String) = thread{ certainsDictionnaires.postValue(dao.loadDictionnaire(url, startLang, endLang))}

    fun loadSameLangDictionnaires(startLang: String, endLang: String) =thread{ currentLangDictionnaires.postValue(dao.loadSameLangDictionnaires(startLang, endLang))}
}