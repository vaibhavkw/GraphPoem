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
#print(texts[0])
pprint(len(poemList))
pprint(len(texts))

dictionary = corpora.Dictionary(texts)

corpus = [dictionary.doc2bow(text) for text in texts]

# LDA model
print('LDA :')
lda = models.LdaModel(corpus, id2word=dictionary, num_topics=10)
corpus_lda = lda[corpus]

pprint(lda.print_topics(10))

print(len(corpus_lda))
i = -1
for doc in corpus_lda:
    i = i + 1
    print(poemList[i])
    print(doc)

