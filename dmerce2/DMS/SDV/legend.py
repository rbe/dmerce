import data
import Image, ImageDraw, ImageFont

class Legend:

    def Init(self, x = [0], y = [0], names = [], size = (10,10), text = '', color = (255,255,255),
                 colorlist = [], font = '/fonts/helvB10.pil',
                 textcolor = (0,0,0), colormode='RGB'):

        self.__x = x
        self.__y = y
        self.__names = names
        self.__size = size
        s = ImageFont.load(font)
        wid = []
        for i in range(0, len(self.__x)):
            wid.append(s.getsize(str(self.__x[i]) + str(self.__y[i]))[0] + 20)
        maxwid = max(wid)
        maxhi = s.getsize(str(x[0]))[1] + 10
        self.__rows = (self.__size[1] - 10) / maxhi + 1
        self.__cols = len(self.__x) /  self.__rows + 1
        self.__text = text
        self.__color = color
        self.__colorlist = colorlist
        self.__colormode = 'RGB'
        self.__colorlist = colorlist
        self.__font = ImageFont.load(font)

    def SetX(self, x):
        self.__x = x

    def SetY(self, y):
        self.__y = y

    def SetSize(self, type):
        self.__size = size

    def SetText(self, text):
        self.__text = text

    def SetColor(self, color):
        self.__color = color

    def SetTextcolor(self, textcolor):
        self.__textcolor = textcolor

    def SetColorlist(self, colorlist):
        self.__colorlist = colorlist
        
    def SetFont(self, font):
        self.__font = ImageFont.load(font)

    def ListDraw(self):
        self.__pict = Image.new(self.__colormode, self.__size, self.__color)
        self.__pic = ImageDraw.Draw(self.__pict)
        c = 0
        h = self.__size[1] / self.__rows
        w = self.__size[0] / self.__cols
        for i in range(0, self.__cols):
            for j in range(0, self.__rows):
                if c < len(self.__x):
                    self.__pic.text((i*w + 2, j*h), str(self.__x[c]) + '   ' + str(self.__y[c]),
                                  self.__colorlist[c], self.__font)
                    c = c+1
    
    def NameDraw(self):
        self.__pict = Image.new(self.__colormode, self.__size, self.__color)
        self.__pic = ImageDraw.Draw(self.__pict)
        c = 0
        h = self.__size[1] / self.__rows
        w = self.__size[0] / self.__cols
        for i in range(0, self.__cols):
            for j in range(0, self.__rows):
                if c < len(self.__yy):
                    self.__pic.text((i*w, j*h), self.__yy[c][2],
                                  self.__yy[c][3], self.__font)
                    c = c+1

    def GetPicture(self):
        return self.__pict
