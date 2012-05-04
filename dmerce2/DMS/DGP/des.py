import mainlib, cipher, deslib

class DES(cipher.Cipher):
    def __init__(self, rounds = 16, times = 1):
        cipher.Cipher.__init__(self, blocklen = 8, wordlen = 8,
                               rounds = rounds, times = times) 
        self.__blocklen = 8
        self.__wordlen = 8
        self.__wpb = 1
        self.__keylen = 8
        self.__rounds = rounds

    def Blocklen(self):
        return self.__blocklen
    
    def KeyFormat(self, data):
        "formats a rawkey-string into a roundkey list of long integers"
        x = mainlib.String_Chop(data, 6)
        x = mainlib.String_Mince(x, 2)
        for i in range(0, len(x)):
            for j in range(0, 2):
                x[i][j] = mainlib.S2L_Convert(x[i][j])
        return x
        
    def KeyExpand(self, data):
        "expands a 64 Bit rawkey-string into a roundkey list of long integers"
        
        z = mainlib.Box_Bit_Permute(deslib.pc1, data, 64)
        y = []
        y.append((z - (z % 2L**28)) >> 28)
        y.append(z % 2L**28)
        key = []
        for i in range(0, self.__rounds):
            y[0] = mainlib.Bit_Rotl(y[0], 28, deslib.shift[i])
            y[1] = mainlib.Bit_Rotl(y[1], 28, deslib.shift[i])
            z = 2L**28 * y[0] + y[1]
            key.append(z)
        xkey = []
        for i in range(0, len(key)):
            xkey.append(0)
            xkey[i] = mainlib.Box_Bit_Permute(deslib.pc2, key[i], 56)
        return xkey
        
    def KeyInvert(self, key):
        "inverts a DES-encryption key to be a decryption key"
        key.reverse()
        return key
        
    def Crypt(self, data, key):
        "The DES Cipher Algorithm"
        x = data
        x[0] = mainlib.Box_Bit_Permute(deslib.ip, data[0], 64)
        state = []
        state.append((data[0]-(data[0] % 2L**32)) >> 32)
        state.append(data[0] % 2L**32)
        for j in range(0, self.__rounds):
            newstate = state[1]
            state[1] = mainlib.Box_Bit_Permute(deslib.ebox, state[1], 32)
            state[1] = state[1] ^ key[j]
            xstate = 0
            for i in range(0, 8):
                xstate = xstate + (16L**(7-i))*(deslib.sbox[i][int((state[1] >> (7-i)*6) % 64)])
            state[1] = xstate
            state[1] = mainlib.Box_Bit_Permute(deslib.pbox, state[1], 32)
            state[1] = state[1] ^ state[0]
            state[0] = newstate
        state.reverse()
        output = []
        output.append(state[1] + 2L**32 * state[0])
        output[0] = mainlib.Box_Bit_Permute(deslib.ip_1, output[0], 64)
        return output

    def Encrypt(self, data, key):
        return self.Crypt(data, key)

    def Decrypt(self, data, key):
        return self.Crypt(data, key)
