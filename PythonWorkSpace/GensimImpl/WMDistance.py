



nltkstopwords = []
fstop = open('resources/NLTKStopWords.txt')
for line in fstop:
    nltkstopwords.append(line.strip())

print(len(nltkstopwords))
print(nltkstopwords)

sentence_obama = "Obama speaks to the media in Illinois".lower().split()
sentence_obama = [w for w in sentence_obama if w not in nltkstopwords]

sentence_president = "The president greets the press in Chicago".lower().split()
sentence_president = [w for w in sentence_president if w not in nltkstopwords]

print(sentence_obama)
print(sentence_president)

distance = model.wmdistance(sentence_obama, sentence_president)
print(distance)