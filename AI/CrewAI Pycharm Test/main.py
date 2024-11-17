# Use Python 3.10

import sys
import os
import yaml
from crewai import Agent, Task, Crew, Process
from langchain_community.tools import DuckDuckGoSearchRun
from langchain_openai import OpenAI
from dotenv import load_dotenv
load_dotenv()

sys.path.append("C:\\Users\\calum\\AppData\\Local\\Packages\\PythonSoftwareFoundation.Python.3.11_qbz5n2kfra8p0\\LocalCache\\local-packages\\Python311\\site-packages")
search_tool = DuckDuckGoSearchRun()

llm = OpenAI(openai_api_key=os.getenv("OPENAI_API_KEY"))

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
    # role='Researcher',
    role=agents['researcher']['role'],
    # goal='Research a particular stock',
    goal=agents['researcher']['goal'],
    # backstory='You are a stock research assistant',
    backstory=agents['researcher']['backstory'],
    verbose=True,
    allow_delegation=False,
    tools=[search_tool]
)

accountant = Agent(
    # role='Accountant',
    role=agents['accountant']['role'],
    # goal='Calculate various accounting ratios',
    goal=agents['accountant']['goal'],
    # backstory='You are an accountant who specialises in analysing stock prices',
    backstory=agents['accountant']['backstory'],
    verbose=True,
    allow_delegation=False,
    tools=[search_tool]
)

recommender = Agent(
    # role='Recommender',
    role=agents['recommender']['role'],
    # goal='Make recommendations based on the accountants ratios',
    goal=agents['recommender']['goal'],
    # backstory='You are an advisor who makes buy and sell recommendations based on the accountants ratios',
    backstory=agents['recommender']['backstory'],
    verbose=True,
    allow_delegation=False,
    tools=[search_tool]
)

blogger = Agent(
    # role='Blogger',
    role=agents['blogger']['role'],
    # goal='Format the outputted information and recommendations nicely',
    goal=agents['blogger']['goal'],
    # backstory='You are a blogger who takes the information from the resercher and recommender, and formats it nicely for presentation',
    backstory=agents['blogger']['backstory'],
    verbose=True,
    allow_delegation=False,
    tools=[search_tool]
)

with open('tasks.yaml', 'r') as file:
    tasks = yaml.safe_load(file)

task1 = Task(
    # description='Look through the stocks_and_cryptos variable and make a table showing each stock/crypto price at each point in time',
    # description='Search the internet for the current and past prices of four popular stocks and four popular cryptocurrencies',
    description=tasks['research_task']['description'],
    # expected_output='A table with current stock and crypto names and current prices, as well as prices through history with the dates for each price',
    expected_output=tasks['research_task']['expected_output'],
    agent=researcher
    # agent=tasks['research_task']['agent']
)
task2 = Task(
    # description='Use the researchers findings to create appropriate ratios to represent the recent changes of each stocks prices',
    description=tasks['accounting_task']['description'],
    # expected_output='A table with current stock and crypto names and ratios of current price vs previous price',
    expected_output=tasks['accounting_task']['expected_output'],
    agent=accountant
    # agent=tasks['accounting_task']['agent']
)
task3 = Task(
    # description='Use the accountants ratios to make buy and sell recommendations for different stocks and cryptos',
    description=tasks['recommendation_task']['description'],
    # expected_output='A list of recommendations on whether somebody should buy or sell each crypto and stock',
    expected_output=tasks['recommendation_task']['expected_output'],
    agent=recommender
    # agent=tasks['recommendation_task']['agent']
)
task4 = Task(
    # description='Output the price data and the recommenders recommendations in a suitable and attractive format',
    description=tasks['blogging_task']['description'],
    # expected_output='A table with stock and crypto names, their current prices, ratios of price now vs prices at point in the past, and recommendations of whether somebody should buy or sell that stock or crypto, all in a single table',
    expected_output=tasks['blogging_task']['expected_output'],
    agent=blogger
    # agent=tasks['blogging_task']['agent']
)

crew = Crew(
    agents=[researcher, accountant, recommender, blogger],
    tasks=[task1, task2, task3, task4],
    verbose=1,
    process=Process.sequential
)

result = crew.kickoff()
