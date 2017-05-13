from gensim import corpora, models
from pprint import pprint
from collections import defaultdict
import os.path
import gensim
import sys
import numpy as np

texts = []
poemList = []

if os.path.exists('D:/PythonWorkSpace/WordEmbeddings/resources/bncf.model') is False:
    print('no model. initializing')
    f = open('D:/PythonWorkSpace/WordEmbeddings/resources/bncContentfull.txt')
    count = 1
    for line in f:
        print(count)
        count+=1
        #splitLine = line.split(",")
        texts.append(line.split())

    print('end file')
    #print(texts[0])
    #pprint(len(poemList))
    #pprint(len(texts))

    #texts = [['first', 'sentence'], ['second', 'sentence']]

    model = gensim.models.Word2Vec(texts, size=100, window=5, min_count=5, workers=4)

    model.save("D:/PythonWorkSpace/WordEmbeddings/resources/bncf.model")
else:
    print("Loading saved model")
    model = gensim.models.Word2Vec.load("D:/PythonWorkSpace/WordEmbeddings/resources/bncf.model")

#print(sys.argv[1])
#pprint(model[sys.argv[1]])
retStr = ''
for x in np.nditer(model[sys.argv[1]]):
    retStr = retStr + "," + str(x)
print(retStr)

