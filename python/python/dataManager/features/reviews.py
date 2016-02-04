from os import path
from nltk.tag.stanford import StanfordNERTagger

# load category mapper
"""
print "# load cateogry mapping data"
category_map = dict()
cm_f = open('data/category_mapper.txt')
for line in cm_f:
    (src, dst) = line.strip().split('\t')
    category_map[src] = dst
cm_f.close()
"""

"""
    user locality
"""
def __location_group(review):
    locality = review['reviewerLocation']

    if 'San Francisco' in locality:
        return True
    else:
        return False

"""
    predict gender
"""
ABS_PATH = path.dirname(path.realpath(__file__))

def predict_gender(review):
    content = review['content'].strip()

    if 'wife' in content:
        return 'male'
    elif 'husband' in content:
        return 'female'
    elif 'love' in content:
        return 'female'
    elif 'boyfriend' in content:
        return 'female'
    elif 'yummy' in content:
        return 'female'
    elif 'she' in content:
        return 'female'
    elif 'our' in content:
        return 'female'
    elif 'cute' in content:
        return 'female'
    elif 'deliciou' in content:
        return 'female'
    else:
        return 'male'

"""
    Named Entity Recognition
"""
# initialize ner tagger
MODEL_PATH = path.join(ABS_PATH,'model', 'food_ver32.ser.gz')
JAR_PATH   = path.join(ABS_PATH,'model', 'stanford-ner-3.5.2.jar')

ner_tagger = StanfordNERTagger(MODEL_PATH, JAR_PATH)

def tag_ner(review):
    content = review['content'].strip()
    ner = ner_tagger.tag(content.split())

    ner_words = []

    buf = []
    buf_idx = -2
    for idx in xrange(len(ner)):
        (word, tag) = ner[idx]

        if tag != 'O':
            if buf_idx + 1 == idx or len(buf) == 0:
                buf.append(word.lower())
                buf_idx = idx
            else:
                ner_words.append(' '.join(buf))
                buf = []

    return ner_words
