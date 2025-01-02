import os
import yaml
import requests
from crewai import Agent, Task, Crew, Process
from langchain.tools import Tool
from dotenv import load_dotenv
from langchain_community.tools import DuckDuckGoSearchRun
from flask import Flask, request, jsonify, render_template

import ollama

load_dotenv()
app = Flask(__name__, template_folder="templates")
app.jinja_env.add_extension('jinja2.ext.loopcontrols')


search_tool = Tool(
    name="DuckDuckGo Search",
    func=DuckDuckGoSearchRun().run,
    description="Search the web using DuckDuckGo"
)

def call_llama(user_input):
    model = "llama3.1"
    prompt = user_input
    stream = ollama.chat(
        model=model,
        messages=[{'role': 'user', 'content': prompt}],
        stream=True
    )
    response_text = ""
    for chunk in stream:
        print(chunk['message']['content'], end='')
        response_text += chunk['message']['content']
    return response_text

def llama_ollama_api(prompt: str) -> str:
    response = requests.post(
        "http://localhost:11434/api",
        json={"model": "llama3.1", "prompt": prompt}
    )
    response.raise_for_status()
    return response.json()["choices"][0]["text"]

llama_tool = Tool(
    name="Llama LLM",
    func=llama_ollama_api,
    description="Generates responses using Llama 3.1 via Ollama API."
)

@app.route('/api', methods=['POST'])
def ollama_api():
    # Use the Ollama library to process the request
    data = request.json
    model = data.get("model", "llama3.1")
    prompt = data.get("prompt")
    
    # Example Ollama interaction
    response = ollama.chat(
        model=model,
        messages=[{"role": "user", "content": prompt}],
    )
    
    return jsonify({"choices": [{"text": response['message']['content']}]})


# A route to render the EJS template
@app.route('/')
def index():
    return "Ollama API is running!"
    return render_template('index.ejs')

# A route to handle incoming data from the frontend
@app.route('/send-data', methods=['POST'])
def send_data():
    data = request.json  # Parse JSON data from the request
    user_input = data.get('user_input')  # Get the input text
    print(f"Received from frontend: {user_input}")

    # Process the input or store it as needed
    # response_text = f"Processed: {user_input}"  # Example processing
    # response_text = llama_ollama_api(user_input)
    response_text = call_llama(user_input)

    # Send back a response to the frontend
    return jsonify({'response': response_text})

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 11434))
    app.run(host="0.0.0.0", port=port)