
#LC_ALL=C sort -t , -n -k1 input > sorted_input1

from HTMLParser import HTMLParser
from glob import glob
from collections import defaultdict
import os.path
import re

# create a subclass and override the handler methods
class MyHTMLParser(HTMLParser):
    
    def resetFlags(self):
        
        self.topicsFlag = 0;
        self.bodyFlag = 0;
        self.oneTopicArticle = 0;
        self.topicsForArticle = [];
        self.articleData = [];
        self.bodyData = '';
    
    
    def setFlags(self,attrs, tag):
        if(tag == 'reuters'):
            self.resetFlags(); # call reset at the start of each article
            for i in attrs:
                if(i[0] == "newid"):
                    self.articleData.append(i[1]); # storing newid
                    
        elif(tag == 'topics'):
            self.topicsFlag = 1;
        elif(tag == 'body'):
            self.bodyFlag = 1;
    
    def __init__(self):
        HTMLParser.__init__(self);
        self.resetFlags();
        self.singleTopicCount = 0;
        self.dict = defaultdict(list);
        self.topicCountHashTable = defaultdict(int);
        
    def handle_starttag(self, tag, attrs):
        self.setFlags(attrs, tag);
            
    def handle_endtag(self, tag):
        if(tag == 'topics'):
            if(len(self.topicsForArticle) == 1):
                self.singleTopicCount += 1;
                self.oneTopicArticle = 1;
#                 print(self.topicsForArticle);
        if(tag == 'body' and self.oneTopicArticle == 1):
            self.topicCountHashTable[self.topicsForArticle[0]] += 1; # topic -> articleData 
            self.articleData.append(self.bodyData);
            self.dict[self.topicsForArticle[0]].append(self.articleData);
            
    
    def handle_data(self, data):
        if(self.topicsFlag == 1):
            self.topicsForArticle.append(data);
        if(self.bodyFlag == 1 and self.oneTopicArticle == 1):
            self.bodyData += data.replace('\n', ' ');

def getFrequent20(parser):
    frequent20 = set();
    i = 0;
    for w in sorted(parser.topicCountHashTable, key=parser.topicCountHashTable.get, reverse=True):
        frequent20.add(w);
        i += 1;
        if (i == 20):  # TODO : change to 20
            break
    return frequent20

def retainOnlyFrequent(parser, frequent):
    for t in parser.dict.keys():
        if(not( t in frequent)):
            del(parser.dict[t])

def is_number(s):
    try:
        int(s)
        return True
    except ValueError:
        return False


def performFiltering(parser, classFile):
    globalMap = defaultdict(int)
    globalListofDict = []
    for t in parser.dict.keys():  # valus is list of lists
        outer_l = parser.dict[t];
        for l in outer_l:  # l is inner list
            l[1] = re.sub(r'[^\x00-\x7f]', r'', l[1]);
            l[1] = l[1].lower();
            l[1] = re.sub('[^0-9a-zA-Z]+', ' ', l[1]);
            tokensArticle = l[1].split();
            dictArticle = defaultdict(int)
            for i in tokensArticle:
                if (not is_number(i)):
                    dictArticle[i] += 1;
            for k in dictArticle.keys():
                globalMap[k] += 1;
            temp = [0, 0];
            temp[0] = l[0];
            temp[1] = dictArticle;
            globalListofDict.append(temp);
            s = "";
            s += str(l[0]) + "," + str(t) + "\n"
            classFile.write(s);

    return globalMap, globalListofDict

def getDimensions(globalMap):
    dimension = {};
    i = 0;
    for art in globalMap:
        dimension[art] = i;
        i += 1;
    return dimension

def generateInputFile(globalListofDict, dimension, inpFile):
    for l in globalListofDict: # i -> [newid, dict of articles-count]
        for k in l[1].keys():
            op = ""+l[0]+",";
            op += str(dimension[k])+ ",";   #  '('+ str(k) +')'+
            op += str(l[1][k])+"\n";
            inpFile.write(op);  #article ID (i.e., the NEWID), j is the dimension #, and v is frequency


def main():
    parser = MyHTMLParser()
    data_path = 'reuters21578'

    for filename in glob(os.path.join(data_path, "reut2-*.sgm")):
        fd = open(filename, 'rb')
        for chunk in fd:
            parser.feed(chunk)

    print "Num of articles :", parser.singleTopicCount;


    frequent20 = getFrequent20(parser);

    # print "topic count is ", parser.topicCountHashTable;
    print(frequent20);
    # print "dict len is", len(parser.dict);

    retainOnlyFrequent(parser, frequent20)

    #print "dict is", parser.dict
    # print "now dict len is", len(parser.dict);



    classFile = open("bag.clabel","w+");
    globalMap, globalListofDict = performFiltering(parser, classFile);


    #print "after dict is", parser.dict
    # print "globalMap is ", globalMap
    #print "globalListofDict is", globalListofDict


    lowest5frequent = set();
    for w in sorted(globalMap, key=globalMap.get, reverse=False):
        if(globalMap[w] < 5):
            lowest5frequent.add(w)

    # print "lowest5frequent is ",lowest5frequent;

    for t in lowest5frequent:
        del(globalMap[t]);

    for d in globalListofDict:
        for tok in d[1].keys():
            if(tok in lowest5frequent):
                del(d[1][tok])


    #print "after deleting lowest5frequent", globalListofDict

    dimension = getDimensions(globalMap);


    # to generate input file for words
    inpFile = open('bag.csv','w+')
    generateInputFile(globalListofDict, dimension, inpFile)

    print("Done")

main();