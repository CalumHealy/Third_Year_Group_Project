from flask import Flask, render_template, jsonify
import re

app = Flask(__name__)

# def parse_file(file_path):
#     """
#     Parse the text file and extract a dictionary where each key is the asset name 
#     and each value is a list of dates.
#     """
#     data = {}
#     try:
#         with open(file_path, 'r') as file:
#             for line in file:
#                 # Split the line into the asset name and the rest
#                 asset, details = line.split(' ', 1)

#                 # Extract dates from the details
#                 dates = []
#                 for entry in details.split():
#                     date, _ = entry.split(',')  # Split date and value
#                     dates.append(date)

#                 # Add to the dictionary
#                 data[asset] = dates

#     except Exception as e:
#         return {"error": str(e)}

#     return data

def parse_file(file_path):
    """
    Parse the text file and extract a dictionary where each key is the asset name
    and each value is a list of tuples (date, price).
    """

    data = {}
    try:
        with open(file_path, 'r') as file:
            for line in file:
                # Use regex to match the asset name and the rest of the line (date, price pairs)
                match = re.match(r'^(.+?)\s(\d{1,2}/\d{1,2}/\d{4},.+)', line.strip())
                if match:
                    asset = match.group(1)  # The asset name
                    details = match.group(2)  # The rest of the line with dates and values

                    # Extract dates and prices from the details
                    date_price_pairs = []
                    for entry in details.split():
                        date, price = entry.split(',')
                        date_price_pairs.append((date, price))

                    data[asset] = date_price_pairs
                else:
                    # Handle lines that don't match the expected format
                    print(f"Skipping malformed line: {line.strip()}")

    except Exception as e:
        return {"error": str(e)}

    return data

@app.route('/process-text', methods=['GET'])
def process_text():
    try:
        # Parse the file and return the dictionary
        file_path = 'Prices3.txt'  # Path to your text file
        parsed_data = parse_file(file_path)

        # Check for parsing errors
        if "error" in parsed_data:
            return jsonify({"status": "error", "message": parsed_data["error"]})

        return jsonify({"status": "success", "data": parsed_data})

    except Exception as e:
        return jsonify({"status": "error", "message": str(e)})

if __name__ == '__main__':
    app.run(debug=True)
