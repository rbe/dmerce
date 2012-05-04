# FILE METHODS

def File_Read(file):
    "reads the contents of <file> and returns it as a string"
    f = open(file, 'r')
    output = f.read()
    f.close()
    return output

def File_Append(file, data):
    "appends the <data>-string to <file>"
    f = open(file, 'a')
    f.append(data)
    f.close()
    
def File_Write(file, data):
    "writes string <data> into <file>"
    f = open(file, 'w')
    f.write(data)
    f.close()
    
# PURE STRING OPERATIONS

def String_Fillup(string, blocksize):
    "fills up a string to the next multiple of blocksize"
    diff = blocksize - (len(string) % blocksize)
    if diff == 0:
        diff = blocksize
    string = string + (diff * chr(diff))
    return string
                
def String_Filldown(string):
    "cuts the fillup octets off"
    pad = ord(string[len(string)-1:])
    string = string[0:len(string)-pad]
    return string

def String_Chop(string, size):
    "chops a string of appropriate length into pieces of size and returns the pieces as a list"
    output = []
    blocknum = len(string) / size
    for i in range(0, blocknum):
        output.append(string[(i * size):((i * size) + size)])
    return output

def String_Mince(list, number):
    "minces the pieces of a chopped list into number many little pieces and returns a list of lists"
    output = []
    length = len(list[0]) / number
    for i in range(0, len(list)):
        output.append(String_Chop(list[i], length))
    return output

# STRING <-> LONG NUMBER CONVERSION

def S2L_Convert(string):
    "converts a string of arbitrary length into a long integer"
    num = 0L
    for i in range(0, len(string)):
        num = num + (2L**(8*i))*ord(string[len(string)-i-1])
    return num

def XL2S_Convert(num):
    "converts a long number into a string"
    string = ''
    while num > 0:
        j = num % 256
        num = num >> 8 
        string = chr(j) + string
    return string

def L2S_Convert(num, len):
    "converts a long number into a string"
    string = ''
    for i in range(0, len):
        j = num % 256
        num = num >> 8 
        string = chr(j) + string
    return string

# BIT OPERATIONS

def Bit_Rotl(data, m, n):
    "rotates an m-bit integer n bit to the left"
    data = long(data)
    m = long(m)
    n = long(n)
    if n > m:
        n = n % m
    d = m - n
    y = data % (2L**d)
    x = data - y
    z1 = x >> d
    z2 = y * (2**n)
    return z1 + z2

def Bit_Rotr(data, m, n):
    "rotates an m-bit integer n bit to the right"
    d = m - n
    return Bit_Rotl(data, m, d)

def Bit_Leastsig(data):
    "returns the index of the least significant bit of data"
    n = 0
    while (data % 2) == 0:
        data = data >> 1
	n = n + 1
    return n + 1

def Bit_Leastsigval(data):
    "returns the amount given by the least significant bit of data"
    return 2**(Bitleastsig(data) - 1)

def Bit_Log2(data):
    "returns the exponent of powers of 2"
    return (Bit_Length(data) - 1)

def Bit_Length(data):
    "returns the number of the largest significant bit of data"
    n=0
    while data >= 2L**n:
        n = n+1
    return n

def Bitvalue_List(data):
    "returns a list of values of bits in data"
    z = []
    for i in range(0, Bit_Length(data)):
        if data % 2L**(i+1) == 0:
            z.append(0)
        else:
            z.append(2L**i)
            data = data - 2L**i
    return z

def Bit_List(data):
    "returns a list of bits (0 or 1) in data (long number)"
    z = []
    for i in range(0, Bit_Length(data)):
        if data % 2L**(i+1) == 0:
            z.append(0)
        else:
            z.append(1)
            data = data - 2L**i
    return z

def Re_Bit_List(data):
    "converts a Bit_List into a long integer"
    z = 0
    for i in range(0, len(data)):
        z = z + (2L**i * data[i])
    return z

# LONG NUMBER <-> BYTE LIST CONVERSION

def Bytes_2_Number(x):
    "converts a Byte-list into a long number"
    y = 0
    for i in range(0, len(x)):
        y = y + (2L**(8*i) * x[i])
    return y

def Number_2_Bytes(x, l):
    "converts a 32Bit word into a list of l Bytes"
    y = []
    for i in range(0,l):
        y.append(x % 256)
        x = x >> 8
    return y

# BOX READING

def Box_Bit_Permute(box, data, max):
    "reads data into a permutating (possibly compressing) box"
    z = 0L
    l = len(box) - 1
    for i in range(0, l + 1):
        if not (data >> (max-1  - box[i])) % 2:
            pass
        else:
            z = z + 2L**(l - i)
    return z

# MODULAR ARITHMETICS

def Mod_Sub(x, y, m):
    "calculates x - y mod m"
    if x >= y:
        return (x - y) % m
    else: 
        return m - ((y - x) % m)

def Mod_Mult_Inv(num, mod):
    "calculates the multiplicative inverse of num modulo mod"
    fakt = []
    rest = 2
    xmod = mod
    xnum = num
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
        invert[k-1] = mod - invert[k-1]
    return invert[k-1]

# MODULAR ARITHMETICS IN GF(2^8)

def Xtime(data):
    if data == 0:
        return data
    else:
        data = data << 1
        if data >= 256:
            data = data ^ 283
        return data
        
def Xxtime(data, x):
    if data == 0 or x == 0:
        return 0
    else:
        for i in range(0, Bit_Log2(x)):
            data = Xtime(data)
        return data

def GF_Mult(x, y):
    if x == 0 or y == 0:
        return 0
    else:
        z = Bitvalue_List(y)
        zz = 0
        for i in range(0, len(z)):
            z[i] = Xxtime(x, z[i])
            zz = zz ^ z[i]
        return zz

def GF_Pot(x, y):
    if y == 0:
        return 1
    else:
        z = x
        for i in range(1, y):
            z = GF_Mult(z, x)
        return z

def GF_Mult_Inv(x):
    if x == 0:
        return 0
    for i in range(0, 256):
        y = GF_Mult(x, i)
        if y == 1:
            return i

