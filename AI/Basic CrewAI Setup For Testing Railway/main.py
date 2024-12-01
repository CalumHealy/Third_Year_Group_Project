import sys
import os
import yaml
from crewai import Agent, Task, Crew, Process
from langchain_openai import OpenAI
from dotenv import load_dotenv
from langchain.tools import Tool
from langchain.tools import StructuredTool
from crewai_tools import FileWriterTool
from pydantic import BaseModel
load_dotenv()

openai_instance = OpenAI(openai_api_key=os.getenv("OPENAI_API_KEY"))

class OpenAIArgs(BaseModel):
    query: str

class FileWriterArgs(BaseModel):
    content: str
    filename: str = "prices.txt"

OpenAI_tool = Tool(
    name="OpenAI LLM",
    func=openai_instance.predict,
    description="OpenAI GPT LLM",
    args_schema=OpenAIArgs
)

def write_to_file(content: str, filename: str = "prices.txt") -> str:
    with open(filename, "w") as file:
        file.write(content)
    return f"Content written to {filename}"

# FileWriter_tool = Tool(
FileWriter_tool = StructuredTool(
    name="File Writer",
    func=write_to_file,
    description="Writes content to a text file. Requires 'content' and optionally 'filename' as inputs.",
    args_schema=FileWriterArgs
)

with open('agents.yaml', 'r') as file:
    agents = yaml.safe_load(file)

researcher = Agent(
    role=agents['researcher']['role'],
    goal=agents['researcher']['goal'],
    backstory=agents['researcher']['backstory'],
    verbose=True,
    allow_delegation=False,
    tools=[OpenAI_tool]
)

formatter = Agent(
    role=agents['formatter']['role'],
    goal=agents['formatter']['goal'],
    backstory=agents['formatter']['backstory'],
    verbose=True,
    allow_delegation=False,
    tools=[OpenAI_tool, FileWriter_tool]
)

with open('tasks.yaml', 'r') as file: 
    tasks = yaml.safe_load(file)

task1 = Task(
    description=tasks['research_task']['description'],
    expected_output=tasks['research_task']['expected_output'],
    agent=researcher
)
task2 = Task(
    description=tasks['formatting_task']['description'],
    expected_output=tasks['formatting_task']['expected_output'],
    agent=formatter
)

crew = Crew(
    agents=[researcher, formatter],
    tasks=[task1, task2],
    verbose=1,
    process=Process.sequential
)

result = crew.kickoff()
print("Crew result: ", result)
print("Output type: ", type(result))

if hasattr(result, '__str__'):
    result_str = str(result)
    print("1")
else:
    result_str = repr(result)
    print("2")
print(result_str)

formatted_content = "Price List:\n1. Product A: €10\n2. Product B: €20"
# formatter.tools[1].func({"content": formatted_content, "filename": "prices.txt"})
formatter.tools[1].func(content=formatted_content, filename="prices.txt")

# formatter_output = formatter.output
# formatter.tools[1].func(content=formatter_output, filename="prices2.txt")

text_file = open("Prices3.txt", "w")
text_file.write(result_str)
text_file.close()
