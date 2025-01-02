# Use an official Python image as a base
FROM python:3.11-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the local code into the container
COPY . /app

# Install dependencies 
RUN pip install -r requirements.txt

# Install Ollama 
RUN curl -fsSL https://ollama.com/download | bash

# Expose the port for Flask 
EXPOSE 5002

# Run the Flask application
CMD ["python", "ChatBot.py"]
