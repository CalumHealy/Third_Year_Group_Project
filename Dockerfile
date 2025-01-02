# Use a lightweight Python image
FROM python:3.11-slim

# Install dependencies
RUN pip install flask ollama

# Copy your application code into the container
WORKDIR /app
COPY . /app

# Expose the port the app will run on
EXPOSE 11434

# Run the application
CMD ["python", "ChatBot.py"]
