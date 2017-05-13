from gensim import corpora, models
from pprint import pprint
from collections import defaultdict

texts = []
poemList = []

f = open('resources/poemContent2.txt')
for line in f:
    #print(line)
    splitLine = line.split(",")
    poemList.append(splitLine[0])
    texts.append(splitLine[1].lower().split())

print('end file')
print(texts[0])
#pprint(len(poemList))
#pprint(len(texts))

dictionary = corpora.Dictionary(texts)

corpus = [dictionary.doc2bow(text) for text in texts]

# TFIDF model
print('TFIDF :')
tfidf = models.TfidfModel(corpus)

print(tfidf)
corpus_tfidf = tfidf[corpus]

print(len(corpus_tfidf))

# LSI model
print('LSI :')
lsi = models.LsiModel(corpus_tfidf, id2word=dictionary, num_topics=9)
corpus_lsi = lsi[corpus_tfidf]

pprint(lsi.print_topics(9, num_words=99))

print('LSI scores:')
i = -1
for doc in corpus_lsi:
    i = i + 1
    print(poemList[i])
    print(doc)
    if(i == 2):
        break
