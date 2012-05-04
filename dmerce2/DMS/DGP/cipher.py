import mainlib

class Cipher:
    "the base class for all blockcipher algorithms"
    def __init__(self, blocklen, wordlen, rounds, times):
        self.__blocklen = blocklen
        self.__wordlen = wordlen
        self.__rounds = rounds
        self.__wpb = self.__blocklen / self.__wordlen
        self.__times = times
        
    def NewData(self, data):
        x = mainlib.String_Mince(mainlib.String_Chop(data, self.__blocklen), self.__wpb)
        for i in range(0, len(x)):
            for j in range(0, len(x[i])):
                x[i][j] = mainlib.S2L_Convert(x[i][j])
        self.__data = x

    def Fillup(self, data):
        return mainlib.String_Fillup(data, self.__blocklen)

    def Filldown(self, data):
        return mainlib.String_Filldown(data)
        
    def OutFormat(self, output):
        x = ''
        for i in range(0, len(output)):
            for j in range(0, len(output[i])):
                x = x + mainlib.L2S_Convert(output[i][j], self.__wordlen)
        return x
        
    # Key Schedule

    def E_Key_Create(self, list):
        k = []
        k.append(self.KeyExpand(list[0]))
        for i in range(1, self.__times, 2):
            k.append(self.KeyInvert(self.KeyExpand(list[i])))
            k.append(self.KeyExpand(list[i+1]))
        self.__ekey = k

    def D_Key_Create(self, list):
        list.reverse()
        k = []
        k.append(self.KeyInvert(self.KeyExpand(list[0])))
        for i in range(1, self.__times, 2):
            k.append(self.KeyExpand(list[i]))
            k.append(self.KeyInvert(self.KeyExpand(list[i+1])))
        self.__dkey = k

    def IVinit(self, data):
        y = []
        data = mainlib.L2S_Convert(data, self.__blocklen)
        for i in range(0, self.__wpb):
            x = mainlib.String_Chop(data, self.__wordlen)
            for j in range(0, len(x)):
                x[j] = mainlib.S2L_Convert(x[j])
            y.append(x)
        y = y[0]
        self.__iv = y

    # ECB

    def ECB_Encrypt(self):
        "Electronic Codebook mode encryption"
        output = []
        for i in range(0, len(self.__data)):
            x = self.Encrypt(self.__data[i], self.__ekey[0])
            for j in range(1, self.__times, 2):
                x = self.Decrypt(x, self.__ekey[j])
                x = self.Encrypt(x, self.__ekey[j+1])
            output.append(x)
        return output
    
    def ECB_Decrypt(self):
        "Electronic Codebook mode decryption"
        output = []
        for i in range(0, len(self.__data)):
            x = self.Decrypt(self.__data[i], self.__dkey[0])
            for j in range(1, self.__times, 2):
                x = self.Encrypt(x, self.__dkey[j])
                x = self.Decrypt(x, self.__dkey[j+1])
            output.append(x)
        return output
    
    # CBC

    def CBC_Encrypt(self):
        "Cipher Block Chaining mode encryption"
        output = []
        output.append(self.__iv)
        for i in range(0, len(self.__data)):
            x = []
            for l in range(0, self.__wpb):
                x.append(self.__data[i][l] ^ output[i][l])
            x = self.Encrypt(x, self.__ekey[0])
            for j in range(1, self.__times, 2):
                x = self.Decrypt(x, self.__ekey[j])
                x = self.Encrypt(x, self.__ekey[j+1])
            output.append(x)
        output[0:1] = []
        return output

    def CBC_Decrypt(self):
        "Cipher Block Chaining mode decryption"
        output = []
        date = self.__data[:]
        date.insert(0, self.__iv)
        for i in range(0, len(self.__data)):
            x = self.__data[i][:]
            x = self.Decrypt(x, self.__dkey[0])
            for j in range(1, self.__times, 2):
                x = self.Encrypt(x, self.__dkey[j])    
                x = self.Decrypt(x, self.__dkey[j+1])
            for l in range(0, self.__wpb):
                x[l] = x[l] ^ date[i][l]
            output.append(x)
        return output
            
    # CFB

    def CFBE(self, data, key, iv):
        pass

    def CFBD(self, data, key, iv):
        pass

    # OFB

    def OFBE(self, data, key, iv):
        pass

    def OFBD(self, data, key, iv):
        pass
    

    # API

    def Encipher(self, data, key):
        self.E_Key_Create(key)
        data = self.Fillup(data)
        self.NewData(data)
        c = self.ECB_Encrypt()
        return self.OutFormat(c)
    
    def Decipher(self, data, key):
        self.D_Key_Create(key)
        self.NewData(data)
        p = self.ECB_Decrypt()
        p = self.OutFormat(p)
        p = self.Filldown(p)
        return p
    
    # Counter

    # BC

    # PCBC

    # CBCC

    # OFBNLF

    # PBC

    # PFB

    # CBCPD











