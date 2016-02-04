def review_dist(users, dist_type):
    dist = dict()

    for t in dist_type:
        dist[t] = dict()

    for userID in users:
        for review in users[userID]:
            for _type in dist_type:
                _val = review[_type]

                if _type is not 'rating':
                    _val = str(review[_type].encode('utf-8'))

                if _val not in dist[_type]:
                    dist[_type][_val] = 0

                dist[_type][_val] += 1

    return dist
