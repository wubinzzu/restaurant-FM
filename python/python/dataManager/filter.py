#Filtersd
def __user_review_cnt(reviews, option={'threshold': 5}):
    return len(reviews) > option['threshold']

def __all_reviews(reviews, option=None):
    return True

Filters = {
        'user_review_cnt': __user_review_cnt
        }
