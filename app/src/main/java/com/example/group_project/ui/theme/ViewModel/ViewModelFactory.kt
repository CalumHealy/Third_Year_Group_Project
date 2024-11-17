package com.example.group_project.ui.theme.ViewModel

import StockViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.group_project.data.StockRepository

class StockViewModelFactory(
    private val repository: StockRepository
) : ViewModelProvider.Factory {

    // Override the 'create' method from ViewModelProvider.Factory
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
