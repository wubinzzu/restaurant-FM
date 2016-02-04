from glob import glob
import json

files = glob('/Users/yuminhwan/Documents/extract_usa_expand/ca\\/san_francisco\\/*.json')

reviewerIDs = set()

for file_path in files:
	f = open(file_path)
	text = f.read()
	if len(text) == 0:
		continue

	data = json.loads(text)
	for review in data['reviews']:
		reviewerID = review['reviewerID']
		reviewerIDs.add(reviewerID)

	f.close()

print len(reviewerIDs)
f = open('data/trip_sanf_reviewerIDs','a')
for reviewerID in reviewerIDs:
	f.write(reviewerID)
	f.write('\n')
f.close()
