import os
import yaml
import requests
from crewai import Agent, Task, Crew, Process
from langchain.tools import Tool
from dotenv import load_dotenv
from langchain_community.tools import DuckDuckGoSearchRun
from flask import Flask, request, jsonify, render_template

load_dotenv()
app = Flask(__name__, template_folder="templates")
app.jinja_env.add_extension('jinja2.ext.loopcontrols')

search_tool = Tool(
    name="DuckDuckGo Search",
    func=DuckDuckGoSearchRun().run,
    description="Search the web using DuckDuckGo"
)

def llama_ollama_api(prompt: str) -> str:
    try:
        response = requests.post(
            "https://bbcc-78-135-167-175.ngrok-free.app/api",
            json={"model": "llama3.1", "prompt": prompt}
        )
        response.raise_for_status()  # This will raise an error if the status code is 4xx or 5xx
        return response.json()["choices"][0]["text"]
    except requests.exceptions.HTTPError as http_err:
        print(f"HTTP error occurred: {http_err}")
        return "There was an error processing your request."
    except Exception as err:
        print(f"Other error occurred: {err}")
        return "An unknown error occurred."


def call_llama(user_input):
    response_text = llama_ollama_api(user_input)
    print(f"LLM Response: {response_text}")
    return response_text


@app.route('/api', methods=['POST'])
def ollama_api():
    # Use the Ollama library to process the request
    data = request.json
    prompt = data.get("prompt", "")
    
    # Call the Ollama API
    response_text = llama_ollama_api(prompt)
    
    return jsonify({"choices": [{"text": response_text}]})


# A route to render the EJS template
@app.route('/')
def index():
    return render_template('index.ejs')

# A route to handle incoming data from the frontend
@app.route('/send-data', methods=['POST'])
def send_data():
    data = request.json  # Parse JSON data from the request
    user_input = data.get('user_input')  # Get the input text
    print(f"Received from frontend: {user_input}")

    # Call the external API to process the input
    try:
        response_text = call_llama(user_input)  # Make an external API call to process input
    except requests.exceptions.RequestException as e:
        print(f"Error calling external API: {e}")
        response_text = "There was an error processing your request."

    # Send back a response to the frontend
    return jsonify({'response': response_text})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", 5000)))
