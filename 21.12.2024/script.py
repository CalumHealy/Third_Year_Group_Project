import time

# Generate a text file with some content
filename = 'Prices3.txt'
with open(filename, 'w') as file:
    file.write(f"This file was generated at {time.ctime()}.\n")

print(f"File '{filename}' has been created.")
