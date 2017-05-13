from gensim import corpora, models
from pprint import pprint
from collections import defaultdict
import os.path
import gensim


class WordEmb:

    texts = []
    stopwords = []


    def processLine(self, line):
        #print(line)
        if line is '':
            return ''
        retStr = ''
        strArr = str(line).lower().split(' ')
        #print(WordEmb.stopwords)
        for word in strArr:
            if word in WordEmb.stopwords:
                continue
            else:
                retStr = retStr + word + " "
        #print(retStr)
        return retStr


    def main(self):
        if os.path.exists('resources/bnc.models') is False:
            print('no model. initializing')
            stoplistfile = open('resources/StopWords.txt')
            for line in stoplistfile:
                line = line.replace('\n', '')
                WordEmb.stopwords.append(line)
                #print(line)

            #stoplist = set('for a of the and to in'.split())
            dirpath = 'D:/EclipseWorkSpace/GraphPoem/resources/diction/bnc/extractedBNC'
            count = 0
            for root,dirs,files in os.walk(dirpath):
                for file in files:
                    filepath = os.path.join(root, file)
                    f = open(filepath, 'r')
                    count = count + 1
                    print('Processing ' + str(count) + ' : ' + file)
                    singleLine = ''
                    #f = open('D:/EclipseWorkSpace/GraphPoem/resources/diction/bnc/extractedBNC/A00.txt')
                    for line in f:
                        #print(line)
                        singleLine = singleLine + ' ' + WordEmb.processLine(self, line)
                        #splitLine = line.split(",")
                        #poemList.append(splitLine[0])
                    WordEmb.texts.append(singleLine.lower().split())
                    #texts.append([word for word in singleLine.lower().split() if word not in stopwords])
            print('end file')
            #print(texts[0])
            #pprint(len(poemList))
            #pprint(len(texts))

            #texts = [['first', 'sentence'], ['second', 'sentence']]

            model = gensim.models.Word2Vec(WordEmb.texts, size=100, window=5, min_count=2, workers=4)

            model.save("resources/bnc.model")
        else:
            print("Loading saved model")
            model = gensim.models.Word2Vec.load("resources/bnc.model")

        pprint(model.similar_by_word('marathon'))

        #pprint(model.most_similar(positive=['army', 'soldier'], negative=['river']))

        #print(model.n_similarity(['sun', 'love'], ['rain', 'soldier']))

        #print(model.similarity('glass', 'mirror'))

        #print(model.doesnt_match("army soldier tree fight".split()))


        print("finished")

        return



obj = WordEmb()
obj.main()