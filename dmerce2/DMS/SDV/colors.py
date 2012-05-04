import types
import error, lib

####################################################################################
# Raw List Creation Methods
####################################################################################

def DynamicList(num):
    c1 = []
    c = 255/(num/6 + 2)
    for j in range(0, c, 2):
        c1.append((0, 0, 255))
        c1.append((0, 0, 150))
    for j in range(c, 255, c):
        c1.append((255,j,0))
        c1.append((j,255,0))
        c1.append((j,0,255))
        c1.append((255,0,j))
        c1.append((0,255,j))
        c1.append((0,j,255))
    return c1
        
def RProgression(num):
    c = []
    d = int(200/num) - 1
    for j in range(55,255,d):
        c.append((j,0,0))
    return c

def GProgression(num):
    c = []
    d = int(200/num) - 1
    for j in range(55,255,d):
        c.append((0,j,0))
    return c

def BProgression(num):
    c = []
    d = int(200/num) - 1
    for j in range(55,255,d):
        c.append((0,0,j))
    c.reverse()
    return c
      
def Fiftyfifty(num, color1, color2):
    c1 = []
    c = num + 2
    for j in range(0, c, 2):
        col.append(color1)
        col.append(color2)
    return col

c1 = [(255,0,0), (0,255,0), (0,0,255),
      (176,0,0), (0,176,0), (0,0,176),
      (255,128,0), (0,255,128), (0,128,255), (128,255,0), 
      (192,128,0), (0,192,128), (128,192,0), (0,128,192),
      (255,128,128), (128,255,128), (128,128,255),
      (192,128,128), (128,192,128), (128,128,192),
      (128,128,128), (128,0,128), (0,128,128),
      (160,160,0), (176,0,160), (192,192,192),
      (255,0,0), (0,255,0), (0,0,255),
      (176,0,0), (0,176,0), (0,0,176),
      (255,128,0), (0,255,128), (0,128,255), (128,255,0), 
      (192,128,0), (0,192,128), (128,192,0), (0,128,192),
      (255,128,128), (128,255,128), (128,128,255),
      (192,128,128), (128,192,128), (128,128,192),
      (128,128,128), (128,0,128), (0,128,128),
      (160,160,0), (176,0,160), (192,192,192)]
c2 = [(205,0,0), (0,205,0), (0,0,205),
      (126,0,0), (0,126,0), (0,0,126),
      (205,78,0), (0,205,78), (0,78,205), (78,205,0),
      (142,78,0), (0,142,78), (78,142,0), (0,78,142),
      (205,78,78), (78,205,78), (78,78,205),
      (152,78,78), (78,142,78), (78,78,142),
      (78,78,78), (78,0,78), (0,78,78),
      (110,110,0), (126,0,120), (142,142,142),
      (205,0,0), (0,205,0), (0,0,205),
      (126,0,0), (0,126,0), (0,0,126),
      (205,78,0), (0,205,78), (0,78,205), (78,205,0),
      (142,78,0), (0,142,78), (78,142,0), (0,78,142),
      (205,78,78), (78,205,78), (78,78,205),
      (152,78,78), (78,142,78), (78,78,142),
      (78,78,78), (78,0,78), (0,78,78),
      (110,110,0), (126,0,120), (142,142,142)]
c3 = [(155,0,0), (0,155,0), (0,0,155),
      (76,0,0), (0,76,0), (0,0,76),
      (155,28,0), (0,155,28), (0,28,155), (28,155,0),
      (92,28,0), (0,92,28), (28,92,0), (0,28,92),
      (155,28,28), (28,155,28), (28,28,155),
      (92,28,28), (28,92,28), (28,28,92),
      (28,28,28), (28,0,28), (0,28,28),
      (60,60,0), (76,0,60), (92,92,92),
      (155,0,0), (0,155,0), (0,0,155),
      (76,0,0), (0,76,0), (0,0,76),
      (155,28,0), (0,155,28), (0,28,155), (28,155,0),
      (92,28,0), (0,92,28), (28,92,0), (0,28,92),
      (155,28,28), (28,155,28), (28,28,155),
      (92,28,28), (28,92,28), (28,28,92),
      (28,28,28), (28,0,28), (0,28,28),
      (60,60,0), (76,0,60), (92,92,92)]

############################################################################
# Colormode Class
############################################################################

class Colormode:

    def Init(self, mode = "RGB"):
        if not mode in ["RGB", "RGBA", "CMYK", "1", "P", "L", "XYZ"]:
            raise error.Error, '[Color] unknown color mode'
        self.__mode = mode

    def SetMode(self, mode):
        if not mode in ["RGB", "RGBA", "CMYK", "1", "P", "L", "XYZ"]:
            raise error.Error, '[Color] unknown color mode'
        self.__mode = mode

    def GetMode(self):
        return self.__mode


############################################################################
## Color Class
############################################################################

class Color:

    def Init(self, mode = "RGB"):
        if not mode in ["RGB", "RGBA", "CMYK", "1", "P", "L", "XYZ"]:
            raise error.Error, '[Color] unknown color mode'
        self.__mode = mode
        
    def TestRGB(self, value):
        if (isinstance(value, types.TupleType) or len(c) == 3 
            or (isinstance(value[0], types.IntType) and 0<=value[0] and value[0]<=255) 
            or (isinstance(value[1], types.IntType) and 0<=value[1] and value[1]<=255) 
            or (isinstance(value[2], types.IntType) and 0<=value[2] and value[2]<=255)):
            return 1
        else:
            return 0
        
    def Test(self, value):
        if self.__mode == 'RGB':
            return self.TestRGB(value)
        
    def SetMode(self, mode):
        if not mode in ["RGB", "RGBA", "CMYK", "1", "P", "L", "XYZ"]:
            raise error.Error, '[Color] unknown color mode'
        self.__mode = mode
            
    def SetValue(self, value):
        if not self.Test(value):
            raise error.Error, '[Color] value not appropriate for mode'
        self.__value = value

    def ShadeDown(self, shade):
        if self.__mode == "RGB":
            value = []
            for i in self.__value:
                value.append(lib.Partsub(i, shade))
            value = tuple(value)
            return value

    def ShadeUp(self, shade):
        if self.__mode == "RGB":
            value = []
            for i in self.__value:
                value.append(lib.Partadd(i, shade))
            value = tuple(value)
            return value
        
    def GetMode(self):
        return self.__mode

    def GetValue(self):
        return self.__value
    

############################################################################
## Color List Class
############################################################################

class Colorlist:

    def Init(self, list, mode = "RGB"):
        light = []
        for i in list:
            e = Color()
            e.Init(mode)
            e.SetValue(i)
            light.append(e)
        self.__light = light
        self.__mediumshade = 0
        self.__darkshade = 0
        
    def SetLight(self, list):
        light = []
        for i in list:
            e = Color()
            e.Init(mode)
            e.SetValue(i)
            light.append(e)
        self.__light = light
        self.__mediumshade = 0
        self.__darkshade = 0
        
    def SetMode(self, mode = "RGB"):
        if not mode in ["RGB", "RGBA", "1", "P", "L", "XYZ"]:
            raise error.Error, '[Color] unknown color mode'
        self.__mode = mode
        
    def SetMediumshade(self, shade):
        self.__mediumshade = shade

    def SetDarkshade(self, shade):
        self.__darkshade = shade

    def SetMedium(self):
        medium = []
        for i in self.__light:
            e = Color()
            e.Init(i.GetMode())
            e.SetValue(i.ShadeDown(self.__mediumshade))
            medium.append(e)
        self.__medium = medium

    def SetDark(self):
        dark = []
        for i in self.__light:
            e = Color()
            e.Init(i.GetMode())
            e.SetValue(i.ShadeDown(self.__darkshade))
            dark.append(e)
        self.__dark = dark
        
    def GetLight(self):
        l = []
        for i in self.__light:
            l.append(i.GetValue())
        return l

    def GetMedium(self):
        m = []
        for i in self.__medium:
            m.append(i.GetValue())
        return m

    def GetDark(self):
        d = []
        for i in self.__dark:
            d.append(i.GetValue())
        return d
