from gensim import corpora, models
from pprint import pprint
from collections import defaultdict
import os.path
import gensim

texts = []
poemList = []


print("Loading saved model")
model = gensim.models.Word2Vec.load("resources/bnc.model")

fw = open('resources/type1_metaphor_final.arff', 'w')

f = open('resources/type1_metaphor.arff', 'r')
for line in f:
    list = line.split(',')
    strLine = list[1]
    wordList = strLine.split(' ')
    #print(wordList[0] + ' ' + wordList[2])
    score = 0
    try:
        score = model.similarity(wordList[0], wordList[2])
    except KeyError as exc:
        print(str('KeyError in ' + str(exc)))
    print(score)
    fw.write(list[0] + ',\'' + list[1] + '\',' + list[2] + ',' + list[3] + ',' + list[4] +  ',' + list[5] + ',' + list[6] + ',' + str(score) + '\n')

#print(model.similarity('kick', 'bucket'))

