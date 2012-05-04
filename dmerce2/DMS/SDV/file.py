import string, ImageFont, error

############################################################################
## Font File Class
############################################################################

class Font:
    
    def SetName(self, name):
        self.__name = name

    def SetDir(self, dir = '/fonts/'):
        self.__dir = dir

    def SetPath(self, path):
        self.__path = path
        
    def Name2Path(self):
        self.__path = self.__dir + self.__name

    def Path2Name(self):
        self.__name = string.split(self.__path, '/')[-1:]

    def GetName(self):
        return self.__name

    def GetPath(self):
        return self.__path

    def GetSize(self, text):
        s = ImageFont.load(self.__path)
        return s.getsize(text)


###########################################################################
## Picture File Class
###########################################################################

class File:

    def SetFile(self, file):
        if not isinstance(file, types.StringType):
                raise error.Error, 'Wallpaper-Dateiname ist kein String!'
        f = string.split(file, '.')
        self.__file = string.join(f, '.')
        g = f[-1:][0]
        if not g in ['jpg', 'gif', 'png', 'bmp']:
            raise error.Error, 'Kein gueltiges Dateiformat!'

    def SetDir(self, dir):
        self.__dir = dir

    def SetPath(self, path):
        self.__path = path
        if not isinstance(path, types.StringType):
                raise error.Error, 'Wallpaper-Dateiname ist kein String!'
        f = string.split(file, '.')
        self.__path = string.join(f, '.')
        g = f[-1:][0]
        if not g in ['jpg', 'gif', 'png', 'bmp']:
            raise error.Error, 'Kein gueltiges Dateiformat!'
        
    def File2Path(self):
        self.__path = self.__dir + self.__file

    def Path2File(self):
        self.__file = string.split(self.__path, '/')[-1:]

    def GetFile(self):
        return self.__file

    def GetPath(self):
        return self.__path
        
    def GetDir(self):
        return self.__dir
