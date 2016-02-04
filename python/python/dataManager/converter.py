import numpy as np
import random

from sklearn.feature_extraction import DictVectorizer
from sklearn import cross_validation

#from features import restaurants as restaurant_feature
from features import reviews as review_feature

def __always_true(r, f):
    return True

def to_dataset(users, objects, filter_method=None, filter_option=None, features = []):
    if filter_method == None:
        filter_method = __always_true

    data = list()

    for userID in users:
        reviews = users[userID]

        # filtering users
        if not filter_method(reviews, filter_option):
            continue

        for review in reviews:
            r = {
                'reviewer_id': review['reviewerID'],
                'restaurant_id': review['sourceObjectKey'],
                'price1': 0,
                'price2': 0,
                'price3': 0,
                'price4': 0,
                'price5': 0
            }

            r['gender'] = review_feature.predict_gender(review)
            #r['local_people'] = 1 if review_feature.__location_group(review) else 0

            o = objects[review['sourceObjectKey']]

            price_len = len(objects[review['sourceObjectKey']]['price'])
            r['price' + str(price_len)] = 1

            for feature in features:
                r[feature] = review[feature]

            data.append((r, review['rating']))

    random.shuffle(data)

    data_x = list()
    data_y = list()
    for d in data:
        data_x.append(d[0])
        data_y.append(d[1])

    return data_x, data_y

def train_test_split(data_x, data_y, test_size=0.4, kFold_size=0):
    train_x, test_x, train_y, test_y = cross_validation.train_test_split(
            data_x, data_y, test_size = test_size)

    if kFold_size == 0:
        kf = None
    else:
        kf = cross_validation.KFold(len(train_x), n_folds=kFold_size)

    return (feature_format(train_x),
            np.array(train_y),
            feature_format(test_x),
            np.array(test_y),
            kf)

def feature_format(d):
    v = DictVectorizer()
    d = v.fit_transform(d)
    return d

