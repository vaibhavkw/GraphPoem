from gensim import corpora, models
from pprint import pprint
from collections import defaultdict
import os.path
import gensim
import numpy as np
import scipy
from scipy import *
from scipy import spatial

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

#pprint(model.most_similar(positive=['army', 'soldier'], negative=['river']))

#print(model.n_similarity(['sun', 'love'], ['rain', 'soldier']))



#print(model.doesnt_match("army soldier tree fight".split()))

pprint(model.similar_by_word('britain', topn=20))

print(model.similarity('kick', 'bucket'))

print(model.similarity('pigs', 'fly'))

print(model.similarity('birds', 'fly'))

print(model.similarity('car', 'drive'))

print(model.similarity('car', 'fly'))

print(model.similarity('plane', 'fly'))

print(model.similarity('bike', 'rides'))

print(model.similarity('car', 'drinks'))

print(model.similarity('car', 'gasoline'))

print(model.similarity('drinks', 'gasoline'))

print(model.similarity('bones', 'dust'))

sum = (model['fair'] + model['game'] ) / 2

#print(sum)

#print(sp. distance.cosine(sum, sum))

#spatial.distance.cdist(sum, sum, 'cosine')
print(1-spatial.distance.cosine(sum, model['game']))

#print(model.similarity('books', 'toys'))

#print(model['christ'])

sum = (model['humanity'] + model['enraged'] ) / 2

print(1-spatial.distance.cosine(model['nature'], sum))

#print(model.similarity('down', 'martini'))

pprint(model.similar_by_word('martini', topn=20))