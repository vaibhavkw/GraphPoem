import nltk
from pprint import pprint
from nltk.corpus import wordnet as wn


print(wn.synsets('car'))
for synset in wn.synsets('car'):
    print(synset.lemma_names())
    #print(synset.hypernyms())


def unusual_words(text):
    text_vocab = set(w.lower() for w in text if w.isalpha())
    english_vocab = set(w.lower() for w in nltk.corpus.words.words())
    unusual = text_vocab - english_vocab
    return sorted(unusual)


print(nltk.corpus.gutenberg.fileids())

print(nltk.corpus.stopwords.words('english'))

print(unusual_words(nltk.corpus.gutenberg.words('austen-sense.txt')))

pprint(len(nltk.corpus.words.words()))
