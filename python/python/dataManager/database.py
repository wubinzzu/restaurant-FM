import pymongo

# configuration
config = {
        'host': '127.0.0.1',
        'port': 27017,
        'db': 'restaurant'
        }

class DB():
    def __init__(self):
        self.connection = None
        self.db = None

    def connect(self):
        c = self.connection

        if c is None:
            try:
                c = pymongo.MongoClient(config['host'], config['port'])
                self.db = c[config['db']]
            except:
                print "Database Connection Error"
                print config
                raise

    def get_collection(self, collection_name):
        if self.db == None:
            print "Error: before connect()"
        else:
            return self.db[collection_name]
