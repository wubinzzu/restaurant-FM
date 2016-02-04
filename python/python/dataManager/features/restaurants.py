# load category mapper
print "# load cateogry mapping data"
category_map = dict()
cm_f = open('data/category_mapper.txt')
for line in cm_f:
    (src, dst) = line.strip().split('\t')
    category_map[src] = dst
cm_f.close()

"""
    restaurant features
"""
def __category_upscale(rest):
    c_result = set()

    for c in rest['categories']:
        c = c.lower()

        if c in category_map:
            c = category_map[c.lower()]

        c_result.add(c)

    rest['categories'] = list(c_result)
    return rest
