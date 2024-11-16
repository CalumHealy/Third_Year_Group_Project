import axios from 'axios';

// Using CoinGecko API to fetch cryptocurrency data
const BASE_URL = 'https://api.coingecko.com/api/v3/coins/markets';
const CURRENCY = 'eur';
const API_PARAMS = `?vs_currency=${CURRENCY}&order=market_cap_desc&per_page=20&page=1&sparkline=false`;

export const fetchCryptoList = async () => {
    try {
        const response = await axios.get(`${BASE_URL}${API_PARAMS}`);
        return response.data; // This will contain the list of cryptos with their names
      } catch (error) {
        console.error('Error fetching crypto data:', error);
        throw error;
      }
};