from whoosh.fields import Schema, TEXT, KEYWORD, ID, STORED
from whoosh.analysis import StemmingAnalyzer
from whoosh.qparser import QueryParser
from whoosh import index
from whoosh import scoring
import os, os.path

indexPath = "D:/PythonWorkSpace/GensimImpl/resources/wikiindexes/"
schema = Schema(filename=ID(stored=True),
                body=TEXT(analyzer=StemmingAnalyzer()))

if not os.path.exists(indexPath):
    os.mkdir(indexPath)

ix = index.create_in(indexPath, schema)
writer = ix.writer()

writer.add_document(filename=u"Mydocument", body=u"This document my document document is my document document")
writer.add_document(filename=u"Mydocument2", body=u"This is my document ")
writer.add_document(filename=u"Mydocument2", body=u"This is my newmean!")
writer.commit()

qp = QueryParser("body", schema=ix.schema)
q = qp.parse(u"my document")

print(ix.reader().frequency("body", "my document"))
print(ix.reader().term_info("body", "document").weight())

with ix.searcher() as s:
    results = s.search(q, terms=True)
    print(s.search_page(q, 1))
    print(results.docnum(1))
    for hit in results:
        print(hit)
    #print(results)
    #print(results.scored_length())
    #print('ppp:' + str(s.subsearchers))


#print(len(results))