import json

def dump_data(users, objects):
    print "# write data files"
    f = open('data/users.json', 'a')
    f.write(json.dumps(users))
    f.close()

    f = open('data/dumps/objects.json', 'a')
    f.write(json.dumps(objects))
    f.close()

def load_data(subdir=None):
    print "# load data files in " + subdir
    if subdir==None:
        root = 'data/'
    else:
        root = 'data/dumps/' + subdir + '/'

    f = open(root + 'users.json')
    users = json.loads(f.read())
    f.close()

    f = open(root + 'objects.json')
    objects = json.loads(f.read())
    f.close()

    return users, objects
