# save_text.py
import os
file_name = "output.txt"
text = "Hello, this is some text saved to a file!"

print("Current working directory: ", os.getcwd())

with open(file_name, "w") as f:
    f.write(text)

print(f"Text saved to {file_name}")
