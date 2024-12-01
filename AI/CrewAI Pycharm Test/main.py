# Use Python 3.10
# Don't use Eduroam

import sys
import os
import yaml
from crewai import Agent, Task, Crew, Process
from langchain_community.tools import DuckDuckGoSearchRun
from langchain_openai import OpenAI
# from langchain_community.models import Llama
from dotenv import load_dotenv
from pydantic import BaseModel
from langchain.tools import Tool
# from typing import Dict
# import llama_cpp
import requests
import google.generativeai as genai
from google.generativeai import GenerativeModel
import subprocess
import json
# from llama_cpp import Llama
from pydantic import Field
from pydantic import model_validator
load_dotenv()

sys.path.append("C:\\Users\\calum\\AppData\\Local\\Packages\\PythonSoftwareFoundation.Python.3.11_qbz5n2kfra8p0\\LocalCache\\local-packages\\Python311\\site-packages") # Will have to change when moving project
search_tool = DuckDuckGoSearchRun()

OpenAI(openai_api_key=os.getenv("OPENAI_API_KEY"))
GROQ_API_KEY = os.getenv("GROQ_API_KEY")

# genai.configure(api_key=os.environ["GEMINI_API_KEY"])
# model = genai.GenerativeModel('gemini-1.5-flash')
# response = model.generate_content("The opposite of hot is")
# print(response.text)

class SearchArgs(BaseModel):
    query: str

class OpenAIArgs(BaseModel):
    prompt: str

class GeminiArgs(BaseModel):
    prompt: str

# class GroqArgs(BaseModel):
#     query: str
class GroqArgs(BaseModel):
    query: str = Field(..., description="Query string to send to Groq")

class LlamaArgs(BaseModel):
    prompt: str = Field(..., description="The prompt string for Llama")
    @model_validator(mode="after")
    def validate_prompt(cls, values):
        if not isinstance(values.prompt, str):
            raise ValueError("Prompt must be a string.")
        return values

def Gemini_Predict(prompt: str) -> str:
    api_key = os.getenv("GEMINI_API_KEY")
    genai.configure(api_key=GEMINI_API_KEY)
    # url = "https://ai.google.dev/api/rest" # This is probably wrong
    model = genai.GenerativeModel('gemini-1.5-pro')

    # headers = {
    #     "Authorization": f"Bearer {api_key}",
    #     "Content-Type": "application/json",
    # }
    #
    # payload = {"prompt": prompt}

    # response = requests.post(url, json=payload, headers=headers)
    response = model.generate_content(prompt)

    if response.status_code == 200:
        return response.json().get("output", "No output provided")
    else:
        raise Exception(f"Gemini API call failed: {response.status_code} {response.txt}")

def Groq_Predict(query: str) -> str:
    api_key = os.getenv("GROQ_API_KEY")
    url = "https://api.groq.com/openai/v1" # Replace with Groq's actual API endpoint
    headers = {"Authorization": f"Bearer {api_key}", "Content-Type": "application/json"}
    payload = {"query": query}
    # response = requests.post(url, json=payload, headers=headers)
    # if response.status_code == 200:
    #     return response.json().get("result", "No result provided")
    # else:
    #     raise Exception(f"Groq API call failed: {response.status_code} {response.txt}")
    try:
        response = requests.post(url, json=payload, headers=headers)
        response.raise_for_status()
        if response.headers['Content-Type'] == 'application/json':
            result = response.json().get('result', 'No result provided')
        else:
            result = response.text
        return result
    except requests.exceptions.RequestException as e:
        raise Exception(f"Groq API call failed: {e}")

# def Llama_Predict(prompt: str) -> str:
#     try:
#         result = subprocess.run(
#             ["ollama", "generate", "--model", "llama3.1", prompt],
#             capture_output=True,
#             text=True
#         )
#         response = json.loads(result.stdout)
#         return response.get("choices", [{}])[0].get("text", "No response")
#     except Exception as e:
#         return f"Error during Llama prediction: {e}"
#
# llama_model = Llama(model_path="C:\\Users\\calum\\.ollama\\models\\manifests\\registry.ollama.ai\\library\\llama3.1") # Specific to my laptop
#
def llama_predict(prompt: str) -> str:
    response = llama_model(prompt)
    return response['choices'][0]['text']

# Set up DuckDuckGoSearchRun
# class SearchArgs(BaseModel):
#     query: str
# class SearchArgs:
#     def __init__(self, query: str):
#         self.query = query


search_tool = Tool(
    name="DuckDuckGo Search",
    func=DuckDuckGoSearchRun().run,
    # description="Search using DuckDuckGo"
    description="Search using DuckDuckGo",
    args_schema=SearchArgs
)

# Set up OpenAI
# class OpenAIArgs(BaseModel):
#     prompt: str
# class OpenAIArgs:
#     def __init__(self, prompt: str):
#         self.prompt = prompt

OpenAI_tool = Tool(
    name="OpenAI LLM",
    func=OpenAI(openai_api_key=os.getenv("OPENAI_API_KEY")).predict,
    # description="OpenAI GPT LLM"
    description="OpenAI GPT LLM",
    args_schema=OpenAIArgs
)

Gemini_tool = Tool(
    name="Gemini LLM",
    func=Gemini_Predict,
    description="Gemini LLM, not sure what version of Gemini",
    args_schema=GeminiArgs
)

Llama_tool = Tool(
    name="Llama LLM",
    func=llama_predict,
    description="Llama 3.1-based LLM",
    agrs_schema=LlamaArgs
)

Groq_tool = Tool(
    name="Groq LLM",
    func=Groq_Predict,
    description="Uses Groq LLM to allow agents to do stuff",
    args_schema=GroqArgs
)

# Set up Llama
# class LlamaArgs(BaseModel):
#     prompt: str
# class LlamaArgs:
#     def __init__(selfself, prompt: str):
#         self.prompt = prompt

# llama_model = Llama(model_path="C:\\Users\\calum\\.ollama\\models\\manifests\\registry.ollama.ai\\library\\llama3.1")
#
# def llama_predict(prompt: str) -> str:
#     response = llama_model(prompt)
#     return response['choices'][0]['text']
#
# Llama_tool = Tool(
#     name="Llama LLM",
#     func=llama_predict,
#     description="Llama-based LLM",
#     args_schema=LlamaArgs
# )

# The following stocks are in the form [company, stock price on date, date]
stocks_and_cryptos = [
    ['Amazon', 20, '19/6/2020'],
    ['Amazon', 25, '2/4/2022'],
    ['Amazon', 34, '7/5/2024'],
    ['Google', 57, '18/9/2019'],
    ['Google', 14, '26/3/2024']
]

with open('agents.yaml', 'r') as file:
    agents = yaml.safe_load(file)

researcher = Agent(
    role=agents['researcher']['role'],
    goal=agents['researcher']['goal'],
    backstory=agents['researcher']['backstory'],
    verbose=True,
    allow_delegation=False,
    tools=[search_tool, OpenAI_tool]
)

accountant = Agent(
    role=agents['accountant']['role'],
    goal=agents['accountant']['goal'],
    backstory=agents['accountant']['backstory'],
    verbose=True,
    allow_delegation=False,
    tools=[search_tool, OpenAI_tool]
)

recommender = Agent(
    role=agents['recommender']['role'],
    goal=agents['recommender']['goal'],
    backstory=agents['recommender']['backstory'],
    verbose=True,
    allow_delegation=False,
    # tools=[search_tool, OpenAI_tool]
    # tools=[search_tool, Llama_tool]
    # tools=[search_tool, Gemini_tool]
    tools=[search_tool, Groq_tool]
    # tools=[Gemini_tool]
)

blogger = Agent(
    role=agents['blogger']['role'],
    goal=agents['blogger']['goal'],
    backstory=agents['blogger']['backstory'],
    verbose=True,
    allow_delegation=False,
    # tools=[search_tool, OpenAI_tool]
    tools=[search_tool, Groq_tool]
)

with open('tasks.yaml', 'r') as file:
    tasks = yaml.safe_load(file)

task1 = Task(
    description=tasks['research_task']['description'],
    expected_output=tasks['research_task']['expected_output'],
    agent=researcher
)
task2 = Task(
    description=tasks['accounting_task']['description'],
    expected_output=tasks['accounting_task']['expected_output'],
    agent=accountant
)
task3 = Task(
    description=tasks['recommendation_task']['description'],
    expected_output=tasks['recommendation_task']['expected_output'],
    agent=recommender
)
task4 = Task(
    description=tasks['blogging_task']['description'],
    expected_output=tasks['blogging_task']['expected_output'],
    agent=blogger
)

crew = Crew(
    agents=[researcher, accountant, recommender, blogger],
    tasks=[task1, task2, task3, task4],
    verbose=1,
    process=Process.sequential
)

result = crew.kickoff()
