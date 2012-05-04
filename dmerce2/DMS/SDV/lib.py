def Partsub(x, y):
    if x < y:
        return 0
    else:
        return x - y

def Partadd(x, y, z):
    if x + y > z:
        return z
    else:
        return x + y

def Add(x, y):
    return (x[0] + y[0], x[1] + y[1])
