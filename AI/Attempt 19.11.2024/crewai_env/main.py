import os
import yaml
from crewai import Agent, Task, Crew, Process
from langchain.tools import Tool
from dotenv import load_dotenv
from langchain_community.tools import DuckDuckGoSearchRun

load_dotenv()

search_tool = Tool(
    name="DuckDuckGo Search",
    func=DuckDuckGoSearchRun().run,
    description="Search the web using DuckDuckGo"
)

def llama_ollama_api(prompt: str) -> str:
    import requests
    response = requests.post(
        "http://localhost:11434/api/completions",
        json={"model": "llama3.1", "prompt": prompt}
    )
    response.raise_for_status()
    return response.json()["cjoices"][0]["text"]

llama_tool = Tool(
    name="Llama LLM",
    func=llama_ollama_api,
    description="Generates responses using Llama 3.1 via Ollama API."
)

with open('agents.yaml', 'r') as file:
    agents_config = yaml.safe_load(file)

researcher = Agent(
    role=agents_config['researcher']['role'],
    goal=agents_config['researcher']['goal'],
    backstory=agents_config['researcher']['backstory'],
    verbose=True,
    tools=[search_tool, llama_tool]
)

accountant = Agent(
    role=agents_config['accountant']['role'],
    goal=agents_config['accountant']['goal'],
    backstory=agents_config['accountant']['backtory'],
    verbose=True,
    tools=[search_tool, llama_tool]
)

recommender = Agent(
    role=agents_config['recommender']['role'],
    goal=agents_config['recommender']['goal'],
    backstory=agents_config['recommender']['backstory'],
    verbose=True,
    tools=[search_tool, llama_tool]
)

blogger = Agent(
    role=agents_config['blogger']['role'],
    goal=agents_config['blogger']['goal'],
    backstory=agents_config['blogger']['backstory'],
    verbose=True,
    tools=[search_tool, llama_tool]
)

with open('tasks.yaml', 'r') as file:
    tasks_config = yaml.safe_load(file)

task1 = Task(
    description=tasks_config['research_task']['description'],
    expected_output=tasks_config['research_task']['expected_output'],
    agent=researcher
)

task2 = Task(
    description=tasks_config['accounting_task']['description'],
    expected_output=tasks_config['accounting_task']['expected_output'],
    agent=accountant
)

task3 = Task(
    description=tasks_config['recommendation_task']['description'],
    expected_output=tasks_config['recommendation_task']['expected_output'],
    agent=recommender
)

task4 = Task(
    description=tasks_config['blogging_task']['description'],
    expected_output=tasks_config['blogging_task']['expected_output'],
    agent=blogger
)

crew = Crew(
    agents=[researcher, accountant, recommender, blogger],
    tasks=[task1, task2, task3, task4],
    verbose=1,
    process=Process.sequential
)

if __name__ == "__main__":
    results = crew.kickoff()
    print(results)