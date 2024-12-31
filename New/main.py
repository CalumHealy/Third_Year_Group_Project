import sys
import os
import yaml
import time
import openai
from crewai import Agent, Task, Crew, Process
from langchain_openai import OpenAI
from dotenv import load_dotenv
from langchain.tools import Tool
from langchain.tools import StructuredTool
from crewai_tools import FileWriterTool
from pydantic import BaseModel
from litellm.exceptions import RateLimitError, APIError

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

# assets = [
#     "Bitcoin", "Ethereum", "Tether", "Solana", "XRP", "BNB", "Dogecoin", "USDC",
#     "Cardano", "Lido Staked Ether", "Shiba Inu", "Avalanche", "TRON", "Toncoin",
#     "Wrapped stETH", "Stellar", "Polkadot", "Wrapped Bitcoin", "Chainlink", "WETH",
#     "Agilent Technologies Inc", "Alcoa Corporation", "Alternative Access First Priority CLO Bond ETF",
#     "Alternative Investment TR", "Goldman Sachs Physical Gold ETF Shares", "Asia Broadband INC",
#     "Aareal Bank AG", "Aberdeen INTL INC", "AAC Techs HLDGS INC ORD", "AAC Techs HLDGS UNSP/ADR",
#     "ATA Creativity Global American Depository Shares", "AMER Commerce SOLTNS INC",
#     "Ares Acquisition Corporation II", "Ares Acquisition Corporation II Units",
#     "Ares Acquisition Corporation II Redeemable Warrants", "AURORA Solar Technologies",
#     "AADI Bioscience Inc Common Stock", "Advisor Shares Dorsey Wright ADR ETF", "AIRTEL Africa PLC",
#     "All American Gold Corp", "Aftermath Silver LTD ORD", "America Great Health", "AIA Group LTD S/ADR",
#     "African AGRI HLDGS INC", "African AGRI HLDGS WTS", "Astra AGRO LESTR UNSP/ADR", "ASAHI CO LTD ORD",
#     "AIA Group LTD ORD", "Alabama Aircraft INDUS", "American AIRES INC", "AAK AB UNSP/ADR",
#     "American Airlines Group Inc", "Aalberts INDUS NV ORD", "AA Mission Acquisition Corp",
#     "AA Mission Acquisition Corp Units", "AA Mision Acquisition Corp Warrants", "ATLAS MARA CO NVEST ORD",
#     "Altisource Asset MGMT", "Atlantic American Corp", "Almadex Minerals LTD", "Armada Mercantile LTD",
#     "Aroundtown SA ORD", "Applied Optoelectronics Inc", "Aaon Inc", "Advance Auto Parts Inc",
#     "GraniteShares ETF Trust GraniteShares 2x Long AAPL Daily ETF", "Direxion Daily AAPL Bear 1X Shares",
#     "Apple iSports Group Inc", "AAP Inc", "Apple Inc."
# ]

assets1 = [
    "Bitcoin", "Ethereum", "Tether", "Solana", "XRP", "BNB", "Dogecoin", "USDC",
    "Cardano", "Lido Staked Ether"
]
assets2 = [
    "Shiba Inu", "Avalanche", "TRON", "Toncoin",
    "Wrapped stETH", "Sui", "Polkadot", "Wrapped Bitcoin", "Chainlink", "Hedera"
]
# assets3 = [
#     "Agilent Technologies Inc", "Alcoa Corporation", "Alternative Access First Priority CLO Bond ETF",
#     "Alternative Investment TR", "Goldman Sachs Physical Gold ETF Shares", "Asia Broadband INC",
#     "Aareal Bank AG", "Aberdeen INTL INC", "AAC Techs HLDGS INC ORD", "AAC Techs HLDGS UNSP/ADR"
# ]
# assets4 = [
#     "ATA Creativity Global American Depository Shares", "AMER Commerce SOLTNS INC",
#     "Ares Acquisition Corporation II", "Ares Acquisition Corporation II Units",
#     "Ares Acquisition Corporation II Redeemable Warrants", "AURORA Solar Technologies",
#     "AADI Bioscience Inc Common Stock", "Advisor Shares Dorsey Wright ADR ETF", "AIRTEL Africa PLC",
#     "All American Gold Corp"
# ]
# assets5 = [
#     "Aftermath Silver LTD ORD", "America Great Health", "AIA Group LTD S/ADR",
#     "African AGRI HLDGS INC", "African AGRI HLDGS WTS", "Astra AGRO LESTR UNSP/ADR", "ASAHI CO LTD ORD",
#     "AIA Group LTD ORD", "Alabama Aircraft INDUS", "American AIRES INC"
# ]
# assets6 = [
#     "AAK AB UNSP/ADR", "American Airlines Group Inc", "Aalberts INDUS NV ORD", "AA Mission Acquisition Corp",
#     "AA Mission Acquisition Corp Units", "AA Mision Acquisition Corp Warrants", "ATLAS MARA CO NVEST ORD",
#     "Altisource Asset MGMT", "Atlantic American Corp", "Almadex Minerals LTD"
# ]
# assets7 = [
#     "Armada Mercantile LTD", "Aroundtown SA ORD", "Applied Optoelectronics Inc", "Aaon Inc", "Advance Auto Parts Inc",
#     "GraniteShares ETF Trust GraniteShares 2x Long AAPL Daily ETF", "Direxion Daily AAPL Bear 1X Shares",
#     "Apple iSports Group Inc", "AAP Inc", "Apple Inc."
# ]
assets3 = [
    "00 Token", "1inch", "Ancient8", "Aeve", "Arcblock", "Acala", "Alchemy Pay", "Access Protocol", "Across Protocol",
    "Cardano"
]
assets4 = [
    "AdEx", "Aergo", "Aerodome Finance", "Aevo", "Adventure Gold", "AIOZ Network", "AIRian", "Akash Network",
    "Alliance Block", "Alchemix"
]
assets5 = [
    "Aleo", "Aleph.im v2", "Algorand", "MyNeighbourAlice", "Stella", "ALT11M2507", "ALT2612", "AltLayer", "Amp", "Ankr"
]
assets6 = [
    "Aragon", "APENFT", "ApeCoin", "API3", "Aptos", "Arbitrum", "Arkham", "ARPA Chain", "Assemble Protocol", "Astar"
]
assets7 = [
    "AirSwap", "Automata Network", "Star Atlas", "Cosmos", "Bounce Token", "Audius", "Aurora", "aUSDT", "Avalanche",
    "Aventus"
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

with open("tasks.yaml", "r") as file:
    tasks = yaml.safe_load(file)

def wait():
    print("Sleepy time")
    # print("Z z z ...")
    # time.sleep(10)
    # print("Z z z ...")
    # time.sleep(10)
    # print("Z z z ...")
    # time.sleep(10)
    # print("Z z z ...")
    # time.sleep(10)
    # print("Z z z ...")
    # time.sleep(10)
    # print("Z z z ...")
    # time.sleep(10)
    print("Awoken")


def process_asset(item):
    retries = 10
    backoff_factor = 20
    for attempt in range(retries):
        try:
            task1 = Task(
                description=tasks['research_task']['description'].replace("the given stock or cryptocurrency", item),
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
                verbose=0,
                process=Process.sequential
            )

            result = crew.kickoff()
            output1.append(str(result))
            return str(result)

        except RateLimitError as e:
            print(f"Rate limit exceeded: {e}. Retrying in {backoff_factor} seconds...")
            time.sleep(backoff_factor)
        except APIError as e:
            print(f"API error: {e}. Retrying in {backoff_factor} seconds...")
            time.sleep(backoff_factor)
        except Exception as e:
            print(f"Unexpected error: {e}. Retrying in {backoff_factor} seconds...")
            time.sleep(backoff_factor)
    print(f"Failed to process {item} after {retries} retries.")
    return f"Error processing {item}"


# First 10 assets
output1 = []
with ThreadPoolExecutor() as executor:
    results = list(executor.map(process_asset, assets1))
final_output1 = "\n".join(results)
print(final_output1)
wait()


# Second 10 assets
output2 = []
with ThreadPoolExecutor() as executor:
    results = list(executor.map(process_asset, assets2))

final_output2 = "\n".join(results)
print(final_output2)
wait()


# Third 10 assets
output3 = []
with ThreadPoolExecutor() as executor:
    results = list(executor.map(process_asset, assets3))

final_output3 = "\n".join(results)
print(final_output3)
wait()


# Fourth 10 assets
output4 = []
with ThreadPoolExecutor() as executor:
    results = list(executor.map(process_asset, assets4))

final_output4 = "\n".join(results)
print(final_output4)
wait()


# Fifth 10 assets
output5 = []
with ThreadPoolExecutor() as executor:
    results = list(executor.map(process_asset, assets5))
final_output5 = "\n".join(results)
print(final_output5)
wait()


# Sixth 10 assets
output6 = []
with ThreadPoolExecutor() as executor:
    results = list(executor.map(process_asset, assets6))

final_output6 = "\n".join(results)
print(final_output6)
wait()


# Seventh 10 assets
output7 = []
with ThreadPoolExecutor() as executor:
    results = list(executor.map(process_asset, assets7))

final_output7 = "\n".join(results)
print(final_output7)
wait()



final_output = (final_output1 + final_output2 + final_output3 + final_output4 + final_output5 + final_output6 + final_output7)

with open("Prices3.txt", "w") as text_file:
    for result in output1 + output2 + output3 + output4 + output5 + output6 + output7:
        text_file.write(result + "\n")
