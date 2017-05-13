from gensim import corpora, models
from pprint import pprint
from collections import defaultdict
import os.path
import gensim

texts = []
poemList = []

if os.path.exists('resources/poemwordembed.model') is False:
    print('no model. initializing')
    f = open('resources/poemContentProcess.txt')
    for line in f:
        #print(line)
        splitLine = line.split(",")
        poemList.append(splitLine[0])
        texts.append(splitLine[1].lower().split())

    print('end file')
    #print(texts[0])
    #pprint(len(poemList))
    #pprint(len(texts))

    #texts = [['first', 'sentence'], ['second', 'sentence']]

    model = gensim.models.Word2Vec(texts, size=100, window=5, min_count=5, workers=4)

    model.save("resources/poemwordembed.model")
else:
    print("Loading saved model")
    model = gensim.models.Word2Vec.load("resources/poemwordembed.model")

pprint(model.most_similar(positive=['army', 'soldier'], negative=['river']))

print(model.n_similarity(['sun', 'love'], ['rain', 'soldier']))

print(model.similarity('glass', 'mirror'))

print(model.doesnt_match("army soldier tree fight".split()))

pprint(model.similar_by_word('car'))

texts2 = []
poemList2 = []
f = open('resources/poemContentProcess.txt')
for line in f:
    # print(line)
    splitLine2 = line.split(",")
    poemList2.append(splitLine2[0])
    texts2.append(splitLine2[1].lower().split())

counti = -1
for poemi in texts2:
    counti += 1
    print("Processing : " + poemList2[counti])
    countj = -1
    maxsimindex = -100.0
    maxpoemname = ""
    for poemj in texts2:
        countj += 1
        if counti == countj:
            continue
        simindex = 0.0
        #try:
        simindex = model.n_similarity(poemi, poemj)
        if str(simindex) == "1.0":
            print("Error 1.0 " + str(countj) + " : " + poemList2[countj] + " : " + str(simindex))
            continue
        #print(poemList[countj] + " " + str(countj) + " : " + str(simindex))
        #except Exception as err:
        #    print('Handling run-time error:', err)
        if simindex > maxsimindex:
            maxsimindex = simindex
            maxpoemname = poemList2[countj]
            #print(str(countj) + " : " + maxpoemname + " : " + str(maxsimindex))
    print(poemList2[counti] + " " + str(maxsimindex) + " : " + maxpoemname)
    if counti == 50:
        break
print("finished")
