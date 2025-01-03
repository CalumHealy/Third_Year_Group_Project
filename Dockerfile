FROM python:3.11-slim

WORKDIR /app

# Install dependencies
COPY requirements.txt .

RUN pip install -r requirements.txt
RUN pip install ollama

# Add Ollama binary or install steps here (if needed)
# Example: Copy binary to `/usr/local/bin` or use a package manager.

# Copy application files
COPY . /app
WORKDIR /app

# Expose port for Railway
EXPOSE 5000

# Start the application
CMD ["python", "ChatBot.py"]
