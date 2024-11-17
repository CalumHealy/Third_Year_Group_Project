import androidx.lifecycle.ViewModel
import com.example.group_project.data.StockRepository
import com.example.group_project.model.Stock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class StockViewModel(private val repository: StockRepository) : ViewModel() {

    private val _stocks = MutableStateFlow<List<Stock>>(emptyList()) // List of available stocks
    val stocks: StateFlow<List<Stock>> = _stocks

    private val _investments = MutableStateFlow<List<Stock>>(emptyList()) // List of added stocks (portfolio)
    val investments: StateFlow<List<Stock>> = _investments

    // Fetch live stocks data from the repository
    fun fetchLiveStocks(date: String, apiKey: String) {
        // Simulating an API call and adding mock data for now
        viewModelScope.launch {
            val mockStocks = listOf(
                Stock("AAPL", 150.0, 1000000),
                Stock("GOOGL", 2800.0, 500000),
                Stock("AMZN", 3400.0, 700000)
            )
            _stocks.value = mockStocks
        }
    }

    // Add selected stock to investments list (portfolio)
    fun addToInvestments(stock: Stock) {
        _investments.value = _investments.value + stock
    }
}
