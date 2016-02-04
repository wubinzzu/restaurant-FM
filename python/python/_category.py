from dataManager.features import restaurants as rest_feature
from pprint import pprint

from dataManager import fileManager


if __name__ == '__main__':
    corpus = []

    users, objects = fileManager.load_data('500')

    c_dict = dict()

    for objectKey, o in objects.iteritems():
        o = rest_feature.__category_upscale(o)

        for c in  o['categories']:
            if c not in c_dict:
                c_dict[c] = 0

            c_dict[c] += 1

    f = open('data/category/500.txt', 'a')

    categories = sorted(c_dict.items(), key = lambda x : x[1], reverse=True)
    for (c, cnt) in categories:
        line = c + '\t' + str(cnt)
        f.write(line)
        f.write('\n')

    f.close()


    #review cnt
    review_cnt = 0
    for userID, reviews in users.iteritems():
        review_cnt += len(reviews)

    print review_cnt
