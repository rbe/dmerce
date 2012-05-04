def Mult(x, y):
    "calculates x times y mod 65537"
    if x == 0:
        x = 65536
    if y == 0:
        y = 65536
    return (x * y) % 65537
        
def MultInv(xnum):
    "calculates the multiplicative inverse of -num- modulo -mod-"
    if xnum == 0:
        return 65536
    else:
        fakt = []
        rest = 2
        xmod = 65537
        while rest > 1:
            rest = xmod % xnum 
            mult = (xmod - rest) / xnum
            fakt.append(mult)
            xmod = xnum
            xnum = rest
            
        m = len(fakt) - 1
        invert = [1]
        invert.append(fakt[m])
        k = len(invert)
        while m >= 1:
            invert.append((fakt[m-1] * invert[k-1]) + invert[k-2])   
            m=m-1
            k = len(invert)
            
        t = len(fakt) % 2
        if t > 0:
            invert[k-1] = 65537 - invert[k-1]
        
    return invert[k-1]

def AddInv(x):
    x = 65536 - x
    return x

