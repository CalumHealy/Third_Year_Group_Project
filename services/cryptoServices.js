import axios from 'axios';

// CoinGecko API to fetch cryptocurrency data
const BASE_URL = 'https://api.coingecko.com/api/v3/coins';

export const fetchCryptoList = async () => {
    try {
        const response = await axios.get(`${BASE_URL}/markets`, {
            params: {
                vs_currency: 'eur',
                order: 'market_cap_desc',
                per_page: 20,
                page: 1,
                sparkline: false
            }
        });
        return response.data.map(crypto => ({
            id: crypto.id, // CoinGecko ID, used to get more details later
            name: crypto.name,
            symbol: crypto.symbol,
            current_price: crypto.current_price,
        }));
    } catch (error) {
        console.error('Error fetching crypto data:', error);
        throw error;
    }
};

// Corrected function for fetching crypto details
export const fetchCryptoDetails = async (id) => {
    try {
        const response = await axios.get(`${BASE_URL}/${id}`);
        const data = response.data;

        // Log the data to check its structure
        console.log(data); // Check what the response looks like in the console

        // Handle the case where `market_data` might not be available
        if (!data || !data.market_data) {
            throw new Error("Market data is missing for this cryptocurrency.");
        }

        const cryptoDetails = {
            name: data.name,
            symbol: data.symbol,
            price: data.market_data?.current_price?.usd || 'N/A', // Safe access with fallback value
            market_cap: data.market_data?.market_cap?.usd || 'N/A', 
            volume_24h: data.market_data?.total_volumes?.usd || 'N/A',
            price_change_24h: data.market_data?.price_change_percentage_24h || 'N/A',
            circulating_supply: data.market_data?.circulating_supply || 'N/A',
            ath: data.market_data?.ath?.usd || 'N/A',
            atl: data.market_data?.atl?.usd || 'N/A',
            market_cap_rank: data.market_cap_rank || 'N/A',
            ath_date: data.market_data?.ath_date || 'N/A',
            hashing_algorithm: data.hashing_algorithm || 'N/A',
            categories: data.categories || ['N/A'],
            total_supply: data.market_data?.total_supply || 'N/A',
            max_supply: data.market_data?.max_supply || 'N/A',
            launch_date: data.genesis_date || 'N/A',
            description: data.description?.en || 'No description available'
        };

        return cryptoDetails;
    } catch (error) {
        console.error('Error fetching crypto details:', error.response?.data || error.message);
        throw error;
    }
};