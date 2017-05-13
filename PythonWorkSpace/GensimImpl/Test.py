from gensim import corpora, models

documents = ["Human machine interface for lab abc computer applications",
              "A survey of user opinion of computer system response time",
              "The EPS user interface management system",
              "System and human system engineering testing of EPS",
              "Relation of user perceived response time to error measurement",
              "The generation of random binary unordered trees",
              "The intersection graph of paths in trees",
              "Graph minors IV Widths of trees and well quasi ordering",
              "Graph minors A survey"]


stoplist = set('for a of the and to in'.split())

texts = [[word for word in document.lower().split() if word not in stoplist]
         for document in documents]

from collections import defaultdict
frequency = defaultdict(int)
for text in texts:
    for token in text:
        frequency[token] += 1


texts = [[token for token in text if frequency[token] > 1]
         for text in texts]

from pprint import pprint
pprint(texts)

dictionary = corpora.Dictionary(texts)
dictionary.save('resources/vk.dict')
print(dictionary)

print(dictionary.token2id)

new_doc = "Trees time time time interaction"
new_vec = dictionary.doc2bow(new_doc.lower().split())
print(new_vec)

corpus = [dictionary.doc2bow(text) for text in texts]
corpora.MmCorpus.serialize('resources/bow1.out', corpus)
print('corpus bow:')
pprint(corpus)

# corpora.BleiCorpus.serialize('resources/bow2.out', corpus)
# corpora.LowCorpus.serialize('resources/bow3.out', corpus)

# TFIDF model
tfidf = models.TfidfModel(corpus)

print(tfidf)
corpus_tfidf = tfidf[corpus]

print('TFIDF scores:')
for doc in corpus_tfidf:
    print(doc)


# LSI model
print('LSI :')
lsi = models.LsiModel(corpus_tfidf, id2word=dictionary, num_topics=2)
corpus_lsi = lsi[corpus_tfidf]

pprint(lsi.print_topics(2))

for doc in corpus_lsi:
    print(doc)



# LDA model
print('LDA :')
lda = models.LdaModel(corpus, id2word=dictionary, num_topics=2)
corpus_lda = lda[corpus]

pprint(lda.print_topics(2))

for doc in corpus_lda:
    print(doc)
