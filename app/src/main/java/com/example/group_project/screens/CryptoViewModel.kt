package com.example.group_project

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Crypto(val id: String, val name: String, val symbol: String)

class CryptoViewModel : ViewModel() {
    //save investments
    private val _favorites: SnapshotStateList<Crypto> = mutableStateListOf()
    val favorites: List<Crypto> = _favorites

    //add a crypto to investments
    fun addToFavorites(crypto: Crypto) {
        if (!_favorites.contains(crypto)) {
            _favorites.add(crypto)
        }
    }

    //remove a crypto from investments
    fun removeFromFavorites(crypto: Crypto) {
        _favorites.remove(crypto)
    }

    //check if crypto is a favorite
    fun isFavorite(crypto: Crypto): Boolean {
        return _favorites.contains(crypto)
    }
}
