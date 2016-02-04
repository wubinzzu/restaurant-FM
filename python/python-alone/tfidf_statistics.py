import csv, sys

csv_file = open('data/tf-idf/top3.csv','rb')
reader = csv.reader(csv_file)

max_tfidf = (-sys.maxint - 1, list())
min_tfidf = (11, list())

tfidf_group = {}
for group_key in xrange(11):
    tfidf_group[group_key] = 0

print tfidf_group

for row in reader:
    for r in row[1:] :
        (w, s) = r.split('//')
        s = float(s)

        group_key = int(round(s* 10))
        tfidf_group[group_key] += 1

        if s > max_tfidf[0]:
            max_tfidf = (s, [w])
        elif s == max_tfidf[0]:
            max_tfidf[1].append(w)

        if s < min_tfidf[0]:
            min_tfidf = (s, [w])
        elif s == min_tfidf[0]:
            min_tfidf[1].append(w)

print '### max tfidf'
print max_tfidf
print '### min tfidf'
print min_tfidf
print '### score grouping'
print tfidf_group
