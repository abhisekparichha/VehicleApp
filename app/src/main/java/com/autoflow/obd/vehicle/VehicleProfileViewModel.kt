package com.autoflow.obd.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoflow.obd.data.db.VehicleEntity
import com.autoflow.obd.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleProfileViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {

    fun saveProfile(vin: String, nickname: String, manufacturer: String, model: String, year: Int, fuelType: String, transmission: String, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.save(
                VehicleEntity(
                    vin = vin,
                    nickname = nickname,
                    manufacturer = manufacturer,
                    model = model,
                    year = year,
                    fuelType = fuelType,
                    transmission = transmission
                )
            )
            onSaved()
        }
    }
}
