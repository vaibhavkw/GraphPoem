from gensim import corpora, models
from pprint import pprint
from collections import defaultdict
import os.path
import gensim
import numpy as np
from scipy import spatial

#model = gensim.models.Word2Vec.load_word2vec_format("D:/study/graph_poem/Tools/Metaphor/WordEmbeddings/glove.6B/glove.6B.100d_gensim.txt", binary=False)

#model.save("D:/PythonWorkSpace/WordEmbeddings/resources/giga/giga100.model")

print("Loading saved model")
model = gensim.models.Word2Vec.load("D:/PythonWorkSpace/WordEmbeddings/resources/giga/giga100.model")

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

print(model.similarity('image', 'adversary'))

print(model.similarity('darkness', 'hour'))

#pprint(model['club'])

print(model.similarity('night', 'chill'))

sum = (model['world'] + model['plate'])
avg = sum / 2

print(1-spatial.distance.cosine(model['sport'], avg))

#print(model.similarity('he', 'dreads'))

#pprint(model.similar_by_word('uproot', topn=5))


