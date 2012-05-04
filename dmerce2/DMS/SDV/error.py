import exceptions, Image, ImageFont, ImageDraw

#
# Error Class           
#
class Error(exceptions.Exception):
    def __init__(self, args = None, message = '', size = (640,480), font = '/fonts/helvB12.pil'):
        self.args = args
        self.__message = message
        self.__size = size
        self.__colorformat = 'RGB'
        self.__font = font
        self.Draw()
        
    def Draw(self):
        id = Image.new('RGB', self.__size, (0,0,0))
        im = ImageDraw.Draw(id)
        fontt = ImageFont.load(self.__font) 
        im.text((50, self.__size[1]/2), self.__message, (255,255,255), fontt)
        id.show()
