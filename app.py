from flask import Flask, jsonify, request
from flask_cors import CORS
import re
import os
import logging
import requests

app = Flask(__name__)

CORS(app, origins="http://localhost:3000")

# Configure logging
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

def parse_file(file_path):
    """
    Parse the text file and extract a dictionary where each key is the asset name
    and each value is a list of tuples (date, price).
    """
    data = {}
    try:
        with open(file_path, 'r') as file:
            for line in file:
                match = re.match(r'^(.+?)\s(\d{1,2}/\d{1,2}/\d{4},.+)', line.strip())
                if match:
                    asset = match.group(1)
                    details = match.group(2)
                    date_price_pairs = [
                        tuple(entry.split(',')) for entry in details.split()
                    ]
                    data[asset] = date_price_pairs
                else:
                    logging.warning(f"Skipping malformed line: {line.strip()}")
    except FileNotFoundError:
        logging.error(f"File not found: {file_path}")
        return {"error": "File not found."}
    except Exception as e:
        logging.error(f"Error parsing file: {e}")
        return {"error": str(e)}
    return data

def fetch_asset_data(asset_name):
    """
    Fetch historical data for an asset from the external API.
    """
    url = f"https://ai-price-generator-production.up.railway.app/historical/{asset_name}"
    try:
        response = requests.get(url)
        response.raise_for_status()  # Raise an error for non-200 status codes
        return {"status": "success", "data": response.json()}
    except requests.exceptions.RequestException as e:
        logging.error(f"Error fetching data for asset {asset_name}: {e}")
        return {"status": "error", "message": str(e)}

@app.route('/api/process-text', methods=['GET'])
def process_text():
    try:
        file_path = request.args.get('file_path', 'Prices3.txt')  # Default file path
        if not os.path.exists(file_path):
            return jsonify({"status": "error", "message": "File does not exist."}), 400

        parsed_data = parse_file(file_path)
        if "error" in parsed_data:
            return jsonify({"status": "error", "message": parsed_data["error"]}), 400

        # Fetch additional data for each asset
        enriched_data = {}
        for asset, details in parsed_data.items():
            api_response = fetch_asset_data(asset)
            if api_response["status"] == "success":
                enriched_data[asset] = {
                    "file_data": details,
                    "api_data": api_response["data"]
                }
            else:
                enriched_data[asset] = {
                    "file_data": details,
                    "api_error": api_response["message"]
                }

        return jsonify({"status": "success", "data": enriched_data}), 200
    except Exception as e:
        logging.error(f"Unhandled error: {e}")
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)

