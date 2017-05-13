from gensim import corpora, models
from pprint import pprint
from collections import defaultdict
import os.path
import gensim
import sys

print("Loading saved model")
model = gensim.models.Word2Vec.load("D:/PythonWorkSpace/WordEmbeddings/resources/giga/giga100.model")

#print(sys.argv[1])
print(model.similarity(sys.argv[1], sys.argv[2]))
