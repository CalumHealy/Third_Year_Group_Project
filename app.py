from flask import Flask, jsonify, request
from flask_cors import CORS
import re

app = Flask(__name__)

CORS(app, origins="http://localhost:3000")

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
                    print(f"Skipping malformed line: {line.strip()}")
    except Exception as e:
        return {"error": str(e)}
    return data

@app.route('/api/process-text', methods=['GET'])
def process_text():
    try:
        file_path = 'Prices3.txt'  # Path to your text file
        parsed_data = parse_file(file_path)

        if "error" in parsed_data:
            return jsonify({"status": "error", "message": parsed_data["error"]}), 400

        return jsonify({"status": "success", "data": parsed_data}), 200
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
