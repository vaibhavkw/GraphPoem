from whoosh.analysis import *
from whoosh.support.charset import accent_map

f1 = PassFilter()
f2 = BiWordFilter(sep='@')
ana = (RegexTokenizer(r"\S+") | f2 | LowercaseFilter())

import csv
with open('D:/study/graph_poem/Tools/Metaphor/Wikipedia/raw.en/raw/englishText_0_10000') as f:
    reader = csv.reader(f)
    for row in reader:
        if (str(row).find("<doc id=") == -1 and str(row).find("</doc>") == -1 and str(row).find("ENDOFARTICLE") == -1):
            #print(row)
            row = str(row)
            row = row.strip("[]")
            result = " ".join([t.text for t in ana(row)])
        #print(result)

exit()

target = "ALPHA BRAVO CHARlie"
f1 = PassFilter()
f2 = BiWordFilter(sep='@')
ana = (RegexTokenizer(r"\S+") | f2 | LowercaseFilter())
result = " ".join([t.text for t in ana(target)])
print(target)
print(result)