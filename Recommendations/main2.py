import os
import yaml
from crewai import Agent, Task, Crew, Process
from langchain_openai import OpenAI
from dotenv import load_dotenv
from langchain.tools import Tool
from pydantic import BaseModel

load_dotenv()

# Initialize OpenAI instance
openai_instance = OpenAI(openai_api_key=os.getenv("OPENAI_API_KEY"))

class OpenAIArgs(BaseModel):
    query: str

# Define OpenAI Tool
OpenAI_tool = Tool(
    name="OpenAI LLM",
    func=openai_instance.predict,
    description="OpenAI GPT LLM",
    args_schema=OpenAIArgs
)

# List of stocks and cryptocurrencies
assets = [
    "Bitcoin", "Ethereum", "Tether", "Solana", "XRP", "BNB", "Dogecoin", "USDC",
    "Cardano", "Lido Staked Ether", "Shiba Inu", "Avalanche", "TRON", "Toncoin",
    "Wrapped stETH", "Stellar", "Polkadot", "Wrapped Bitcoin", "Chainlink", "WETH",
    "Agilent Technologies Inc", "Alcoa Corporation", "Alternative Access First Priority CLO Bond ETF",
    "Alternative Investment TR", "Goldman Sachs Physical Gold ETF Shares", "Asia Broadband INC",
    "Aareal Bank AG", "Aberdeen INTL INC", "AAC Techs HLDGS INC ORD", "AAC Techs HLDGS UNSP/ADR",
    # Add remaining stocks/cryptos here
]

# Function to query OpenAI for stock/crypto data
def query_asset_data(asset_name: str) -> str:
    query = f"Provide a detailed historical price analysis for {asset_name}. Include general trends and indicate probable future changes. Include the current price and historical data up to today (2024)."
    try:
        response = OpenAI_tool.func(query)
        return f"{asset_name}:\n{response}"
    except Exception as e:
        return f"{asset_name}: ERROR - {e}"

# Output file
output_file = "prices.txt"

# Clear file contents if it already exists
if os.path.exists(output_file):
    with open(output_file, "w") as f:
        f.write("")

# Loop through each asset and query data
for asset in assets:
    print(f"Querying data for: {asset}")
    result = query_asset_data(asset)
    # Append the result to the file
    with open(output_file, "a") as f:
        f.write(result + "\n\n")  # Add a newline for better formatting

print(f"Data collection completed. Results saved to {output_file}.")
