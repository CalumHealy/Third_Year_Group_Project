// polygonService.js
import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

const BASE_URL = "https://api.polygon.io/v3/reference/tickers";
const API_KEY = process.env.POLYGON_API_KEY;
console.log(API_KEY);

export const fetchStocks = async (limit = 50) => {
    try {
        const response = await axios.get(`${BASE_URL}?active=true&sort=ticker&limit=${limit}&apiKey=${API_KEY}`);
        return response.data.results;
    } catch (error) {
        console.error("Error fetching stocks:", error.response?.data || error.message);
        throw error;
    }
};