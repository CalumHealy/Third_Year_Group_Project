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

import threading
from concurrent.futures import ThreadPoolExecutor


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

assets = [
    "Bitcoin", "Ethereum", "Tether", "Solana", "XRP", "BNB", "Dogecoin", "USDC"#,
    #"Cardano", "Lido Staked Ether", "Shiba Inu", "Avalanche", "TRON", "Toncoin",
    #"Wrapped stETH", "Stellar", "Polkadot", "Wrapped Bitcoin", "Chainlink", "WETH",
    #"Agilent Technologies Inc", "Alcoa Corporation", "Alternative Access First Priority CLO Bond ETF",
    #"Alternative Investment TR", "Goldman Sachs Physical Gold ETF Shares", "Asia Broadband INC",
    #"Aareal Bank AG", "Aberdeen INTL INC", "AAC Techs HLDGS INC ORD", "AAC Techs HLDGS UNSP/ADR",
    #"ATA Creativity Global American Depository Shares", "AMER Commerce SOLTNS INC",
    #"Ares Acquisition Corporation II", "Ares Acquisition Corporation II Units",
    #"Ares Acquisition Corporation II Redeemable Warrants", "AURORA Solar Technologies",
    #"AADI Bioscience Inc Common Stock", "Advisor Shares Dorsey Wright ADR ETF", "AIRTEL Africa PLC",
    #"All American Gold Corp", "Aftermath Silver LTD ORD", "America Great Health", "AIA Group LTD S/ADR",
    #"African AGRI HLDGS INC", "African AGRI HLDGS WTS", "Astra AGRO LESTR UNSP/ADR", "ASAHI CO LTD ORD",
    #"AIA Group LTD ORD", "Alabama Aircraft INDUS", "American AIRES INC", "AAK AB UNSP/ADR",
    #"American Airlines Group Inc", "Aalberts INDUS NV ORD", "AA Mission Acquisition Corp",
    #"AA Mission Acquisition Corp Units", "AA Mision Acquisition Corp Warrants", "ATLAS MARA CO NVEST ORD",
    #"Altisource Asset MGMT", "Atlantic American Corp", "Almadex Minerals LTD", "Armada Mercantile LTD",
    #"Aroundtown SA ORD", "Applied Optoelectronics Inc", "Aaon Inc", "Advance Auto Parts Inc",
    #"GraniteShares ETF Trust GraniteShares 2x Long AAPL Daily ETF", "Direxion Daily AAPL Bear 1X Shares",
    #"Apple iSports Group Inc", "AAP Inc", "Apple Inc."
]


def write_to_file(content: str, filename: str = "/21.12.2024/prices.txt") -> str:
    if not isinstance(content, str):
        raise ValueError(f"Expected content to be a string, but got {type(content)}")
    with open(filename, "w") as file:
        file.write(content)
    return f"Content written to {filename}"

with open('agents.yaml', 'r') as file:
    agents = yaml.safe_load(file)

researcher = Agent(
    role=agents['researcher']['role'],
    goal=agents['researcher']['goal'],
    backstory=agents['researcher']['backstory'],
    # verbose=True,
    verbose=False,
    allow_delegation=False,
    tools=[OpenAI_tool]
)

formatter = Agent(
    role=agents['formatter']['role'],
    goal=agents['formatter']['goal'],
    backstory=agents['formatter']['backstory'],
    verbose=False,
    allow_delegation=False,
    tools=[OpenAI_tool]
)

print("Current working directory: ", os.getcwd())

output = []

with open("tasks.yaml", "r") as file:
    tasks = yaml.safe_load(file)


def process_asset(item):
    task1 = Task(
        description=tasks['research_task']['description'].replace("the given stock or cryptocurrency", item),
        expected_output=tasks['research_task']['expected_output'],
        agent=researcher)
    task2 = Task(
        description=tasks['formatting_task']['description'],
        expected_output=tasks['formatting_task']['expected_output'],
        agent=formatter
    )

    crew = Crew(
        agents=[researcher, formatter],
        tasks=[task1, task2],
        verbose=0,
        process=Process.sequential
    )

    result = crew.kickoff()
    output.append(str(result))
    return str(result)

with ThreadPoolExecutor() as executor:
    results = list(executor.map(process_asset, assets))

final_output = "\n".join(output)

with open("Prices3.txt", "w") as text_file:
    text_file.write(final_output)
