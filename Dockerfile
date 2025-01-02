FROM python:3.11-slim

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Add Ollama binary or install steps here (if needed)
# Example: Copy binary to `/usr/local/bin` or use a package manager.

# Copy application files
COPY . .

# Expose port for Railway
EXPOSE 11434

# Start the application
CMD ["python", "ChatBot.py"]
