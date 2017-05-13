import wikipedia

title = "car"
wikipage = wikipedia.page(title)

content = wikipage.content

#content = 'i am a car person. And you are car but he is not car at all'
index = str(content).split('cars')

#print(index)

for line in index:
    currentLine = line.strip().lower().split(" ")
    print(currentLine[0])

print(content)
