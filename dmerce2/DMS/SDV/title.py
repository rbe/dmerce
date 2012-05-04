import types
import Image, ImageDraw, ImageFont
import error, data

#
# Title class
#
class Title:

    def Init(self, size = (10,10), colormode = "RGB", text = '', font = '/fonts/helvB12.pil' ,
                 textcolor = (0,0,0), color = (255,255,255)):
        # size
        self.__size = size
        # colormode
        if not colormode in ["RGB", "RGBA", "1", "L", "P", "XYZ"]:
            raise error.Error, 'Das Farbformat fuer den Titelhintergrund ist nicht korrekt!'
        self.__colormode = colormode
        # text
        if not isinstance(text, types.StringType):
            raise error.Error, 'Die Titelzeile ist kein String!'
        s = ImageFont.load(font)
        if not (s.getsize(text)[0] < self.__size[0]):
            raise error.Error, 'Die Titelzeile ist zu lang!'
        self.__text = text
        # color
        self.__color = color
        # textcolor
        self.__textcolor = textcolor
        # font
        self.__font = ImageFont.load(font)

    def SetSize(self, size):
        self.__size = size

    def SetColormode(self, colormode):
        self.__colormode = colormode

    def SetText(self, text):
        self.__text = text

    def SetColor(self, color):
        self.__color = color

    def SetTextcolor(self, textcolor):
        self.__textcolor = textcolor

    def SetFont(self, font):
        self.__font = ImageFont.load(font)

    def Draw(self):
        self.__pict = Image.new(self.__colormode, self.__size, self.__color)
        self.__pic = ImageDraw.Draw(self.__pict)
        self.__pic.text((self.__size[0]/2 - self.__font.getsize(self.__text)[0]/2, 10),
                      self.__text, self.__textcolor, self.__font)

    def GetPicture(self):
        return self.__pict
