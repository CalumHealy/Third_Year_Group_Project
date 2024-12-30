const fetchHistoricalData = async (assetName) => {
    try {
        const response = await fetch(`https://ai-price-generator-production.up.railway.app/api/process-text/${assetName}`);
        if (!response.ok) {
            throw new Error(`Error fetching historical data for ${assetName}: ${response.statusText}`);
        }
        const data = await response.json();
        return data; // Adjust according to the API response structure
    } catch (error) {
        console.error(`Error in fetchHistoricalData: ${error.message}`);
        return [];
    }
};

module.exports = { fetchHistoricalData };
