package com.example.group_project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.group_project.CryptoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun InvestmentDetailsPage(name: String, symbol: String) {
    val viewModel: CryptoViewModel = viewModel()

    // Observe the list of cryptos and stocks from the ViewModel
    val cryptos = viewModel.cryptos
    val stocks = viewModel.stocks

    // Local state for the recommendation
    var recommendation by remember { mutableStateOf<String?>(null) }
    var isSupporter by remember { mutableStateOf(false) } // Assume non-supporter by default

    // Find the selected crypto or stock based on the symbol
    val selectedCrypto = cryptos.find { it.symbol == symbol }
    val selectedStock = stocks.find { it.s == symbol }

    // Firebase Check for Account Type
    LaunchedEffect(Unit) {
        // Simulate Firebase check
        isSupporter = checkUserAccountType() // Replace with actual Firebase logic

        // If user is a supporter, fetch the recommendation
        if (isSupporter) {
            recommendation = fetchRecommendation(name)
        }
    }

    // Show a loading spinner while data is loading
    if (cryptos.isEmpty() && stocks.isEmpty()) {
        Text(text = "Loading...", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Investment Details for $name",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show the details for the selected crypto
        if (selectedCrypto != null) {
            Text(
                text = "Symbol: ${selectedCrypto.symbol}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: $${"%.2f".format(selectedCrypto.currentPrice)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Market Cap: $${"%.2f".format(selectedCrypto.marketCap)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "24h Volume: $${"%.2f".format(selectedCrypto.volume24h)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price Change 24h: $${"%.2f".format(selectedCrypto.priceChange24h)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Circulating Supply: ${"%.2f".format(selectedCrypto.circulatingSupply)} ${selectedCrypto.symbol}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "All Time High (ATH): $${"%.2f".format(selectedCrypto.ath)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "All Time Low (ATL): $${"%.2f".format(selectedCrypto.atl)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hashing Algorithm: ${selectedCrypto.hashingAlgorithm}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        } else if (selectedStock != null) {
            // Show details for stock if available
            Text(
                text = "Name: ${selectedStock.n}",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: $${"%.2f".format(selectedStock.c)} USD",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Market Cap: $${"%.2f".format(selectedStock.marketCap)} USD", // If applicable
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "24h Volume: $${"%.2f".format(selectedStock.v)} USD", // If applicable
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            // Add any other stock-specific fields here
        } else {
            Text(
                text = "No data available for this investment.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Recommendation Section for Supporter Account
        if (isSupporter) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AI Recommendation:",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recommendation ?: "Fetching recommendation...",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Function to simulate Firebase account type check
suspend fun checkUserAccountType(): Boolean {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        val accountType = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .get()
            .await()
            .getString("accountType")
        return accountType == "Supporter"
    }
    return false
}

// Function to fetch AI recommendation
suspend fun fetchRecommendation(assetName: String): String? {
    return try {
        val url = URL("https://ai-recommendations-production.up.railway.app/api/recommendations/$assetName")
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        // Log the URL to check if it's correct
        println("Requesting URL: ${url.toString()}")

        if (connection.responseCode == 200) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }

            // Log the response to see if it contains data
            println("API Response: $response")

            val recommendationResponse = Json.decodeFromString<RecommendationResponse>(response)
            recommendationResponse.recommendation
        } else {
            "Failed to fetch recommendation. Please try again later."
        }
    } catch (e: Exception) {
        "Error fetching recommendation: ${e.localizedMessage}"
    }
}

@Serializable
data class RecommendationResponse(
    val recommendation: String
)
