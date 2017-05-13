from gensim import corpora, models
from pprint import pprint
from collections import defaultdict
import os.path
import gensim
import sys
import numpy as np

print("Loading saved model")
model = gensim.models.Word2Vec.load("D:/PythonWorkSpace/WordEmbeddings/resources/giga/giga100.model")

#print(sys.argv[1])
#pprint(model[sys.argv[1]])
retStr = ''
for x in np.nditer(model[sys.argv[1]]):
    retStr = retStr + "," + str(x)
print(retStr)