import axios from 'axios';

const CHART_API = "https://ai-price-generator-production.up.railway.app";

// Function to capitalize the first letter of a string
function capitalizeFirstLetter(str) {
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

// Example of asset name
let assetName = 'dogecoin';

// Capitalizing the first letter of asset name
let capitalizedAssetName = capitalizeFirstLetter(assetName);

export async function fetchHistoricalData(assetName) {
    try {
        const response = await axios.get(`${CHART_API}/api/historical/${encodeURIComponent(capitalizedAssetName)}`);
        return response.data.data; // Adjust this according to the response structure
    } catch (error) {
        throw new Error(`Error fetching historical data for ${assetName}: ${error.message}`);
    }
}