import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

const BASE_URL = "https://api.polygon.io/v3/reference/tickers";
const API_KEY = process.env.POLYGON_API_KEY;

console.log(API_KEY);  // Ensure that the API key is loaded

export const fetchStocks = async (limit = 50) => {
    try {
        const response = await axios.get(`${BASE_URL}?active=true&sort=ticker&limit=${limit}&apiKey=${API_KEY}`);
        return response.data.results;
    } catch (error) {
        console.error("Error fetching stocks:", error.response?.data || error.message);
        throw error;
    }
};

// If you want to fetch details for stocks, use the appropriate Polygon endpoint
export const fetchStockDetails = async (ticker) => {
    try {
        const response = await axios.get(`https://api.polygon.io/v2/aggs/ticker/${ticker}/prev?apiKey=${API_KEY}`);
        const data = response.data.results?.[0]; // Access the first (latest) aggregation result

        if (!data) {
            throw new Error("No data found for the given stock ticker.");
        }

        const stockDetails = {
            name: ticker, // Replace with metadata if available via another API call
            symbol: ticker,
            price: data.c, // Close price
            open: data.o, // Open price
            high: data.h, // High price of the day
            low: data.l, // Low price of the day
            volume: data.v, // Volume
            market_cap: "N/A", // Placeholder, unless retrieved from another API call
            year_52_high: "N/A", // Placeholder; requires another endpoint
            year_52_low: "N/A", // Placeholder; requires another endpoint
            dividend_yield: "N/A", // Placeholder
            pe_ratio: "N/A", // Placeholder
            eps: "N/A", // Placeholder
            beta: "N/A", // Placeholder
            sector: "N/A", // Placeholder
            currency: "USD", // Hardcoded as USD
            last_trade_time: new Date(data.t).toLocaleString(), // Convert timestamp
            price_change: data.c - data.o, // Close - Open
            ytd_change: "N/A", // Placeholder; requires another endpoint
        };

        return stockDetails;
    } catch (error) {
        console.error("Error fetching stock details:", error.response?.data || error.message);
        throw error;
    }
};