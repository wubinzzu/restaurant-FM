from pyFM import pylibfm
import json
import random
import numpy as np

from sklearn import cross_validation
from sklearn.feature_extraction import DictVectorizer
from sklearn.metrics import mean_squared_error

from dataManager import converter
from calculate import math

def load_data(path):
    print "# load " + path
    data = list()
    f = open(path)
    for line in f:
        data.append(json.loads(line.strip()))
    f.close()
    random.shuffle(data)
    return data

"""
eac data[
    userNumber,
    restaurantNumber,
    userWordFeature1,
    .....,
    userWordFeature100,
    restaurantFeature1,
    .....,
    restaurantFeature100,
    rating
]
"""
def transform_data(data):
    print "# transform data"
    x = list()
    y = list()

    for d in data:
        feature_dic = {
            'user_num': 'user' + str(d[0]),
            'restaurant_num': 'restaurant' + str(d[1])
        }

        for feature_idx in xrange(len(d[2:-1])):
            if d[feature_idx] == 1:
                feature_dic.update({'word_' + str(feature_idx) : True})
            else:
                feature_dic.update({'word_' + str(feature_idx) : False})

        x.append(feature_dic)
        y.append(float(d[-1]/10))

    """
    for d in data:
        x.append(d[:-1])
        y.append(d[-1])
    """

    return x, y

def get_fm(
        num_iter=10,
        initial_learning_rate=0.001,
        ):
    print "# generate FM"
    fm = pylibfm.FM(
            num_factors=10,
            num_iter=num_iter,
            verbose=True,
            task="regression",
            initial_learning_rate=initial_learning_rate,
            learning_rate_schedule="optimal",
            validation_size=0.5)

    return fm

def split_data(x,y):
    print "# split data"
    train_x, train_y, test_x, test_y, kf_index = converter.train_test_split(
            x, y, test_size = 0.2, kFold_size = 10)

    return train_x, train_y, test_x, test_y,

def execute_crossV_fm(fm, train_x, train_y, test_x, test_y, kf_index):
    print "# execute cross validation"
    results = list()

    print "# execute CV"
    for train, test in kf_index:
        rmse = execute_fm(
                fm,
                train_x[train], train_y[train],
                train_x[test],  train_y[test])
        print "cross-validation: FM RMSE: %.4f" % rmse

        results.append(rmse)

    return results

def execute_fm(fm, train_x, train_y, test_x, test_y):
    print "# execute FM"
    fm.fit(train_x, train_y)

    preds = fm.predict(test_x)

    print preds
    rmse = mean_squared_error(preds, test_y)

    return rmse

if __name__ == '__main__':
    data = load_data('data/dumps/50/20151216_201953_featuresVector.txt')
    x, y = transform_data(data)
    v = DictVectorizer()
    d = v.fit_transform(x)
    train_x, train_y, test_x, test_y, kf_index = converter.train_test_split(
            x, y, test_size = 0.2, kFold_size = 10)

    fm = get_fm()
    results = execute_crossV_fm(fm, train_x, train_y, test_x, test_y, kf_index)
    print results
    avg_rmse = math.mean(results)
    print avg_rmse

    #print(execute_fm(fm, train_x, train_y, test_x, test_y))
