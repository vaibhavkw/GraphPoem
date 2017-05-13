from gensim import corpora, models, similarities
from pprint import pprint
import gensim
from collections import defaultdict

texts = []
poemList = []

print('no model. initializing')
f = open('D:/PythonWorkSpace/GensimImpl/resources/poemContent.txt')
for line in f:
    # print(line)
    splitLine = line.split(",")
    poemList.append(splitLine[0])
    texts.append(splitLine[1].lower().split())

print('end file')
# print(texts[0])
# pprint(len(poemList))
# pprint(len(texts))

# texts = [['first', 'sentence'], ['second', 'sentence']]

dictionary = corpora.Dictionary(texts)

corpus = [dictionary.doc2bow(text) for text in texts]

lda = models.LdaModel(corpus, id2word=dictionary, num_topics=10)
corpus_lda = lda[corpus]

pprint(lda.print_topics(10))

print(len(corpus_lda))

index = similarities.MatrixSimilarity(lda[corpus])
#index.save("simIndex.index")

docname = "D:/PythonWorkSpace/GensimImpl/resources/testpoem.txt"
doc = open(docname, 'r').read()
vec_bow = dictionary.doc2bow(doc.lower().split())
vec_lda = lda[vec_bow]

sims = index[vec_lda]
print(sims[0])
print(sims[153])
sims = sorted(enumerate(sims), key=lambda item: -item[1])
print(sims)
print(sims[0])
print(sims[153])
#print(sims[154])