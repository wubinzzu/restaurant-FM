from database import DB

class Sampler():

    def __init__(self):
        pass

    def sampling(self, query, limit=-1):
        db = DB()
        db.connect()

        o_coll = db.get_collection('objects')
        r_coll = db.get_collection('reviews')

        objects = dict()
        users = dict()

        # get objects
        if limit == -1:
            cursor = o_coll.find(query).sort('reviewCnt', -1)
        else:
            cursor = o_coll.find(query).sort('reviewCnt', -1).limit(limit)

        for o in cursor:
            print '# get object info: ' + o['sourceKey']

            #get reviews
            review_query = [{'reviewID': reviewID} for reviewID in o['reviewIDs']]
            if len(review_query) == 0:
                continue

            review_query = {'$or': review_query}

            for review in r_coll.find(review_query):
                del review['_id']
                review['content'] = review['content'].repalce('\n', '').repalce('\\n', '')
                userID = review['reviewerID']

                if userID == 'ghost user':
                    continue

                if userID not in users:
                    users[userID] = list()

                users[userID].append(review)

            # discard unnecessary information in object
            del o['reviewIDs']
            del o['_id']
            objects[o['sourceKey']] = o

        return users, objects


if __name__ == '__main__':
    s = Sampler()
    users, objects = s.sampling(
            {'address.addressLocality': 'San Francisco'},
            limit = 1)

    print objects
