import mainlib
import cipher
import idealib

class IDEA(cipher.Cipher):
    def __init__(self, rounds = 8, times = 1):
        cipher.Cipher.__init__(self, blocklen = 8, wordlen = 2, rounds = rounds, times = times)
        self.__blocklen = 8
        self.__wordlen = 2
        self.__wpb = self.__blocklen / self.__wordlen
        self.__keylen = 16
        self.__rounds = rounds
        
    def Blocklen(self):
        return self.__blocklen
    
    def KeyFormat(self, data):
        "formats a long numeric key into a roundkey list of long integers" 
        data = mainlib.L2S_Convert(data)
        data = data + 4 * '0'
        x = mainlib.String_Chop(data, 12)
        y = mainlib.String_Mince(x, 6)
        for i in range(0, len(y)):
            for j in range(0, 6):
                y[i][j] = ord(y[i][j][1]) + (256 * ord(y[i][j][0])) 
        y[len(y)-1][4:] = []
        return y
        
    def KeyExpand(self, data):
        "expands a 128 Bit rawkey-string into a roundkey list of long integers"
        strdata = mainlib.L2S_Convert(data, 16)
        x = data
        for i in range(1, self.__rounds):
            x = mainlib.Bit_Rotl(x, 128, 25)
            strdata = strdata + mainlib.L2S_Convert(x, 16)
        strdata = strdata[0: (self.__rounds*12 + 8)]
        strdata = strdata + 4 * '0'
        x = mainlib.String_Chop(strdata, 12)
        y = mainlib.String_Mince(x, 6)
        for i in range(0, len(y)):
            for j in range(0, 6):
                y[i][j] = ord(y[i][j][1]) + (256 * ord(y[i][j][0])) 
        y[len(y)-1][4:] = []
        return y

    def KeyInvert(self, data):
        "inverts a roundkey list into the matching decryption roundkey list (or vice versa)"
        for i in range(0, self.__rounds + 1):
            for j in [0, 3]:
                data[i][j] = idealib.MultInv(data[i][j])
        for i in range(0, self.__rounds + 1):
            for j in [1, 2]:
                data[i][j] = idealib.AddInv(data[i][j])
        data.reverse()
        for d in range(0, self.__rounds):
            data[d].append(data[d+1][4])
            data[d].append(data[d+1][5])
            data[d+1].remove(data[d][4])
            data[d+1].remove(data[d][5])
        for i in range(1, self.__rounds):
            key2 = data[i][1:3] 
            key2.reverse()
            data[i][1:3] = key2
        return data
        
    def Crypt(self, data, key):
        "The IDEA Algorithm"
        state = []
        state.append(long(data[0]))
        state.append(long(data[1]))
        state.append(long(data[2]))
        state.append(long(data[3]))
        for r in range(0, self.__rounds):

            n1 = idealib.Mult(state[0], key[r][0])
	    n2 = (state[1] + key[r][1]) % 65536
	    n3 = (state[2] + key[r][2]) % 65536
	    n4 = idealib.Mult(state[3], key[r][3])
	    
	    n5 = n1 ^ n3
	    n6 = n2 ^ n4
	    
	    n7 = idealib.Mult(n5, key[r][4])
	    n8 = (n6 + n7) % 65536
	    n9 = idealib.Mult(n8, key[r][5])
	    n10 = (n7 + n9) % 65536
	    
	    n11 = n1 ^ n9
	    n12 = n2 ^ n10
	    n13 = n3 ^ n9
	    n14 = n4 ^ n10
	    	    
	    state[0] = n11
	    state[1] = n13
	    state[2] = n12
	    state[3] = n14
			
        state[0] = idealib.Mult(n11, key[self.__rounds][0])
	state[1] = (n12 + key[self.__rounds][1]) % 65536
	state[2] = (n13 + key[self.__rounds][2]) % 65536
	state[3] = idealib.Mult(n14, key[self.__rounds][3])
        return state

    def Encrypt(self, data, key):
        "IDEA Encryption"
        return self.Crypt(data, key)

    def Decrypt(self, data, key):
        "IDEA Decryption"
        return self.Crypt(data, key)
    
