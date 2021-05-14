package com.example.parcmarc

import android.graphics.Bitmap
import android.preference.PreferenceManager
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class ParkViewModel(private val parkRepository: ParkRepository): ViewModel() {
    val parks: LiveData<List<ParkWithParkImages>> = parkRepository.parks.asLiveData()
    val numParks: LiveData<Int> = parkRepository.numParks.asLiveData()

    private var _tempImages = MutableLiveData<MutableList<File>>(arrayListOf())
    private var _numTempImages = MutableLiveData<Int>(tempImages.value!!.size)

    val tempImages: MutableLiveData<MutableList<File>>
        get() = _tempImages

    val numTempImages: LiveData<Int>
        get() = _numTempImages

    fun clearTempImages() {
        _tempImages.value?.clear()
        _tempImages.notifyObserver()
        _numTempImages.value = _tempImages.value!!.size
    }

    fun addTempImage(file: File) {
        _tempImages.value?.add(file)
        _tempImages.notifyObserver()
        _numTempImages.value = _tempImages.value!!.size
    }

    // I think its safe to say nobody is going to leave their car parked in the middle of the ocean
    private var _tempLocation = MutableLiveData(LatLng(0.0,0.0))

    val tempLocation: LiveData<LatLng>
        get() = _tempLocation

    fun setLocation(location: LatLng) {
        _tempLocation.value = location
        _tempLocation.notifyObserver()
    }

    private var _tempDuration = MutableLiveData(Pair(0, 0))

    val tempDuration: LiveData<Pair<Int, Int>>
        get() = _tempDuration

    fun setDuration(duration: Pair<Int, Int>) {
        _tempDuration.value = duration
        _tempDuration.notifyObserver()
    }

    fun clearCreateEditTemps() = viewModelScope.launch {
        clearTempImages()
        setDuration(Pair(0, 0))
        setLocation(LatLng(0.0, 0.0))
    }

    fun addPark(park: Park, images: List<File>) = runBlocking {
        val id: Long = parkRepository.insert(park)
        for (image in images) {
            parkRepository.insert(ParkImage(id, image.absolutePath))
        }
    }

    fun updatePark(park: Park) = viewModelScope.launch {
        parkRepository.update(park)
    }

    fun removePark(park: Park) = viewModelScope.launch {
        parkRepository.delete(park)
    }

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

}

class ParkViewModelFactory(private val repository: ParkRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParkViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}