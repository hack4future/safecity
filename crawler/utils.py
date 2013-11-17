from functools import reduce


def flatten(l):
    return reduce(list.__add__, [flatten(x) if isinstance(x, list) else [x] for x in l], [])
