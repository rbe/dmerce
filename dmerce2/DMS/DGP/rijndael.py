import mainlib
import cipher
import rijndaellib

class Rijndael(cipher.Cipher):
    "The Rijndael blockcipher algorithm"

    def __init__(self, keylen = 16, rounds = 10, blocklen = 16, times = 1):
        cipher.Cipher.__init__(self, blocklen = blocklen, rounds = rounds, times = times, wordlen = 4)
        self.__blocklen = blocklen
        self.__wordlen = 4
        self.__nb = self.__wpb = self.__blocklen / 4
        self.__nr = self.__rounds = rounds
        self.__keylen = keylen
        self.__nk = self.__keylen / 4
        self.__c = rijndaellib.C(self.__nb)
        self.__times = times
        
    def Blocklen(self):
        return self.__blocklen

    def KeyFormat(self, key):
        "formats a long rawkey into an appropriate roundkey list"
        pass

    def KeyExpand(self, key):
        "The Rijndael key Schedule"
        rk = []
        for i in range(0, self.__nk):
            rk.append((key >> (32*(self.__nk - 1 -i))) % 2L**32)
        for i in range(self.__nk, self.__nb*(self.__nr + 1)):
            temp = rk[i-1]
            if i % self.__nk == 0:
                temp = rijndaellib.SubWord(mainlib.Bit_Rotl(temp, 32, 8)) ^ rijndaellib.Rcon(i / self.__nk)
            elif self.__nk == 8 and (i % self.__nk) == 4:
                temp = rijndaellib.SubWord(temp)
            rk.append(rk[i - self.__nk] ^ temp)
        rrk = []
        for i in range(0, self.__nr + 1):
            rrk.append([])
            for j in range(0, self.__nb):
                rrk[i].append(rk[self.__nb * i + j])
        return rrk
        
    def KeyInvert(self, key):
        key.reverse()
        for i in range(1, len(key)-1):
            key[i] = rijndaellib.IMixColumn(key[i])
        return key
    
    def Encrypt(self, data, key):
        "Rijndael encryption"
        for i in range(0, self.__nb):
            data[i] = data[i] ^ key[0][i]
        state = data
        for i in range(0, self.__rounds - 1):
            bstate = []
            for j in state:
                bstate.append(mainlib.Number_2_Bytes(j, 4))
            for m in bstate:
                m.reverse()
            rkey = key[i+1]
            for j in range(0, self.__nb):
                state[j] = rijndaellib.T0(int(bstate[j][0]))
                state[j] = state[j] ^ rijndaellib.T1(int(bstate[(j + self.__c[1]) % self.__nb][1]))
                state[j] = state[j] ^ rijndaellib.T2(int(bstate[(j + self.__c[2]) % self.__nb][2]))
                state[j] = state[j] ^ rijndaellib.T3(int(bstate[(j + self.__c[3]) % self.__nb][3]))
                state[j] = state[j] ^ rkey[j]
        lstate = []
        for i in state:
            i = rijndaellib.SubWord(i)
            lstate.append(i)
        state = lstate
        state = rijndaellib.ShiftRows(state, self.__nb)
        for j in range(0, len(state)):
            state[j] = state[j] ^ key[self.__rounds][j]
        hstate = []
        return state  

    def Decrypt(self, data, key):
        "Rijndael decryption"
        for i in range(0, self.__nb):
            data[i] = data[i] ^ key[0][i]
        state = data
        for i in range(0, self.__rounds - 1):
            bstate = []
            for j in state:
                bstate.append(mainlib.Number_2_Bytes(j, 4))
            for m in bstate:
                m.reverse()
            rkey = key[i+1]
            for j in range(0, self.__nb):
                state[j] = rijndaellib.IT0(int(bstate[j][0]))
                state[j] = state[j] ^ rijndaellib.IT1(int(bstate[(j + (self.__nb - self.__c[1])) % self.__nb][1]))
                state[j] = state[j] ^ rijndaellib.IT2(int(bstate[(j + (self.__nb - self.__c[2])) % self.__nb][2]))
                state[j] = state[j] ^ rijndaellib.IT3(int(bstate[(j + (self.__nb - self.__c[3])) % self.__nb][3]))
                state[j] = state[j] ^ rkey[j]
        lstate = []
        for i in state:
            i = rijndaellib.ISubWord(i)
            lstate.append(i)
        state = lstate
        state = rijndaellib.IShiftRows(state, self.__nb)
        for j in range(0, len(state)):
            state[j] = state[j] ^ key[self.__rounds][j]
        return state  
