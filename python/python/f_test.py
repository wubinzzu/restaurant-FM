from pprint import pprint

from features import reveiws as review_feature
from features import objects as object_feature

from dataManager import fileManager
from calculate import distribute

TYPE = {
        'rating': 'rating',
        'locality': 'reviewerLocation',
        'price': 'price'
        }


print "# load data"
users, objects = fileManager.load_data('top3')


#dist = distribute.review_dist(users, [TYPE['rating'], TYPE['locality']])
#pprint(dist)


"""
for userID in users:
    counter = { 'male': 0, 'female': 0 }

    for review in users[userID]:
        gender = text_feature.predict_gender(review)
        counter[gender] += 1
        ner = text_feature.tag_ner(review)

        f = open('ner.txt', 'a')
        f.write(review['content'].encode('utf8').strip())
        f.write('\n')
        f.write('\t'.join(ner))
        f.close()
"""
