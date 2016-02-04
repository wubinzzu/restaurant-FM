from fastFM import mcmc

from sklearn.metrics import mean_squared_error

from dataManager.sampler import Sampler
from dataManager.filter import Filters
from dataManager import converter, fileManager

from calculate import math

import random, json

def loadData_fromDB(limit=500):
    print "# load data from DB"
    sampler = Sampler()

    users, objects = sampler.sampling(
            query ={'address.addressLocality': 'San Francisco'},
            limit = limit
            )

    return users, objects

def loadData_fromLocal(subdir_name):
    print "# load from Local"
    users, objects = fileManager.load_data('50')

    return users, objects

def transform_data(users, objects):
    print "# transform data to fit FM"
    data_x, data_y = converter.to_dataset(
            users, objects,
            Filters['user_review_cnt'],
            {'threshold': 5})

    train_x, train_y, test_x, test_y, kf_index = converter.train_test_split(
            data_x, data_y,
            test_size = 0.2,
            kFold_size= 10)

    return train_x, train_y, test_x, test_y, kf_index

def get_fm(
        n_iter = 200,
        seed = 123,
        rank = 30
        ):
    """
    print "# generate FM"
    fm = pylibfm.FM(
            num_factors=10,
            num_iter=num_iter,
            verbose=False,
            task="regression",
            initial_learning_rate=initial_learning_rate,
            learning_rate_schedule="constant")
    """
    fm = mcmc.FMRegression(n_iter=0, rank=3, random_state=seed)

    return fm

def execute_crossV_fm(fm, train_x, train_y, test_x, test_y, kf_index):
    print "# execute cross validation"
    results = list()

    print "# execute CV"
    for train, test in kf_index:
        rmse = execute_fm(
                fm,
                train_x[train], train_y[train],
                train_x[test],  train_y[test])
        #print "cross-validation: FM RMSE: %.4f" % rmse
        print rmse

        results.append(rmse)

    return results

def execute_fm(fm, train_x, train_y, test_x, test_y):
    result = fm.fit_predict(train_x, train_y, test_x)
    """
    print "# execute FM"
    preds = fm.predict(test_x)

    rmse = mean_squared_error(preds, test_y)
    """

    return result

if __name__ == '__main__':
    """
    users, objects = loadData_fromDB(limit=3)
    fileManager.dump_data(users, objects)
    """
    users, objects = loadData_fromLocal('500')

    train_x, train_y, test_x, test_y, kf_index = transform_data(users, objects)
    fm = get_fm()
    #avg_rmse = execute_fm(fm, train_x, train_y, test_x, test_y)
    results = execute_crossV_fm(fm, train_x, train_y, test_x, test_y, kf_index)
    #avg_rmse = math.mean(results)

    print results
    #print avg_rmse

    """
    option = {
            'num_iter': 1,
            'initial_learning_rate': 0.01
            }

    for num_iter in range(10, 11):
        option['num_iter'] = num_iter

        fm = get_fm(num_iter=option['num_iter'])
        results = execute_crossV_fm(fm, train_x, train_y, test_x, test_y, kf_index)

        avg_rmse = math.mean(results)

        f = open('./results.txt', 'a')
        f.write(json.dumps(option) + '\t' + str(num_iter) + '\t' + str(avg_rmse))
        f.write('\n')
        f.close()
    """
