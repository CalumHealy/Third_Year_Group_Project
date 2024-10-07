from dotenv import load_dotenv
load_dotenv()

import sys
sys.path.append("C:\Users\calum\AppData\Local\Packages\PythonSoftwareFoundation.Python.3.11_qbz5n2kfra8p0\LocalCache\local-packages\Python311\site-packages")
# sys.path.appent("")
from crewai import Agent, Task, Crew, Process
import os

os.environ["OPENAI_API_KEY"] = "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"

researcher = Agent(
    role = 'Researcher', 
    goal = 'Research a particular stock', 
    backstory = 'You are a stock research assistant', 
    verbose = True, 
    allow_delegation = False
)

accountant = Agent(
    role = 'Accountant', 
    goal = 'Calculate various accounting ratios', 
    backstory = 'You are an accountant who specialises in analysing stock prices', 
    verbose = True, 
    allow_delegation = False
)

recommender = Agent(
    role = 'Recommender', 
    goal = 'Make recommendations based on the accountants ratios', 
    backstory = 'You are an advisor who makes buy and sell recomendations based on the accountants ratios',
    verbose = True, 
    allow_delegation = False
)

blogger = Agent(
    role = 'Blogger', 
    goal = 'Format the outputted information and recommendations nicely', 
    backstory = 'You are a blogger who takes the information from the resercher and recommender, and formats it nicely for presentation', 
    verbose = True, 
    allow_delegation = False
)

task1 = Task(description = 'Research current stock and crypto prices, showing current and recent prices', agent = researcher)
task2 = Task(description = 'Use the researchers findings to create appropraite ratios to represent the recent changes of each stocks prices', agent = accountant)
task3 = Task(description = 'Use the accountants ratios to make buy and sell recommendations for different stocks and cryptos', agent = recommender)
task4 = Task(description = 'Output the price data and the recommenders recommendations in a suitable and attractive format', agent = blogger)

crew = Crew(
    agents = [researcher, accountant, recommender, blogger], 
    tasks = [task1, task2, task3, task4], 
    verbose = 2, 
    process = Process.sequential
)

result = crew.kickoff()
