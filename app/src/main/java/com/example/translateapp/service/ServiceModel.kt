package com.example.translateapp.service

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.translateapp.database.DicoApplication
import com.example.translateapp.database.entity.Mot

class ServiceModel(application: Application) : AndroidViewModel(application) {

    val dao = (application as DicoApplication).database.MyDao()

    var updateInfo = MutableLiveData(0)
    fun updateMot(mot: Mot) {
        Thread {
            val l = dao.updateMot(mot)
            Log.d("REMOVE", "dans update mot, value $l")
            updateInfo.postValue(l)
        }.start()
    }

    var loadMotsLearn = MutableLiveData<List<Mot>>()
    fun loadAllMotNeedToBeLearn(bool: Boolean) {
        Thread {
            Log.d("REMOVE", "load all mots")
            loadMotsLearn.postValue(dao.loadAllMotsNeedToBeLearn(bool))
        }.start()
    }

}