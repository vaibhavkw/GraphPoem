import nltk
from nltk.corpus import wordnet as wn
from nltk.wsd import lesk


sentence = "I went to the bank to see the water flow."
tokens = nltk.word_tokenize(sentence)
print(tokens)

print(lesk(tokens, 'bank', 'n'))
#print(lesk(tokens, 'bank'))
print('All results:')
for ss in wn.synsets('bank'):
    print(ss, ss.definition())

