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
# from pydantic import BaseModel
from langchain.tools import Tool
# from typing import Dict
# import llama_cpp
# import requests
load_dotenv()

sys.path.append("C:\\Users\\calum\\AppData\\Local\\Packages\\PythonSoftwareFoundation.Python.3.11_qbz5n2kfra8p0\\LocalCache\\local-packages\\Python311\\site-packages") # Will have to change when moving project
search_tool = DuckDuckGoSearchRun()

OpenAI(openai_api_key=os.getenv("OPENAI_API_KEY"))

# Set up DuckDuckGoSearchRun
# class SearchArgs(BaseModel):
#     query: str
# class SearchArgs:
#     def __init__(self, query: str):
#         self.query = query


search_tool = Tool(
    name="DuckDuckGo Search",
    func=DuckDuckGoSearchRun().run,
    description="Search using DuckDuckGo"
    # description="Search using DuckDuckGo",
    # args_schema=SearchArgs
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
    description="OpenAI GPT LLM"
    # description="OpenAI GPT LLM",
    # args_schema=OpenAIArgs
)

# Set up Llama
# class LlamaArgs(BaseModel):
#     prompt: str
# # class LlamaArgs:
# #     def __init__(selfself, prompt: str):
# #         self.prompt = prompt
#
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
    tools=[search_tool, OpenAI_tool]
)

blogger = Agent(
    role=agents['blogger']['role'],
    goal=agents['blogger']['goal'],
    backstory=agents['blogger']['backstory'],
    verbose=True,
    allow_delegation=False,
    tools=[search_tool, OpenAI_tool]
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
