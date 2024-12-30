from flask import Flask, jsonify, request
from flask_cors import CORS
import re

app = Flask(__name__)
CORS(app)  # Enable cross-origin requests from your frontend

def parse_file(file_path):
    """
    Parse the text file and extract data.
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
    except Exception as e:
        return {"error": str(e)}
    return data

@app.route('/api/historical/<asset>', methods=['GET'])
def get_historical_data(asset):
    """
    Endpoint for historical data of an asset.
    """
    try:
        file_path = 'Prices3.txt'  # Replace with your actual file path
        parsed_data = parse_file(file_path)
        if "error" in parsed_data:
            return jsonify({"status": "error", "message": parsed_data["error"]}), 400
        asset_data = parsed_data.get(asset)
        if not asset_data:
            return jsonify({"status": "error", "message": f"Asset {asset} not found."}), 404
        return jsonify({"status": "success", "data": asset_data}), 200
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
