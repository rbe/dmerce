import math, Image, ImageDraw, ImageFont, string, types, exceptions
import error, data, colors, file, graphic, legend, title

##########################################################################
## SDV Class
##########################################################################

class SDV:
    
    ######################################################################
    # Init Method
    ######################################################################

    def Init(self, filename = 'foo.bar.png',
             size = (800,600), leftmargin = 30, rightmargin = 30,
             topmargin = 30, bottommargin = 30, dist = 10, colormode = "RGB",
             color = (255,255,255)):

        # filename & -format
        f = string.split(filename, '.')
        self.__filename = string.join(f, '.')
        self.__fileformat = f[-1:][0]

        # size
        self.__size = size
    
        # topmargin
        if not isinstance(topmargin, types.IntType):
            raise error.Error, 'Die Vorgabe fuer den oberen Rand hat keinen Integer-Wert!'
        self.__topmargin = topmargin
        
        # bottommargin
        if not isinstance(bottommargin, types.IntType):
            raise error.Error, 'Die Vorgabe fuer den unteren Rand hat keinen Integer-Wert!'
        self.__bottommargin = bottommargin
        
        # leftmargin
        if not isinstance(leftmargin, types.IntType):
            raise error.Error, 'Die Vorgabe fuer den linken Rand hat keinen Integer-Wert!'
        self.__leftmargin = leftmargin
        
        # rightmargin
        if not isinstance(rightmargin, types.IntType):
            raise error.Error, 'Die Vorgabe fuer den rechten Rand hat keinen Integer-Wert!'
        self.__rightmargin = rightmargin
        
        # dist
        if not isinstance(dist, types.IntType):
            raise error.Error, 'Die Vorgabe fuer die Zwischenraeume der Bildelemente hat keinen Integer-Wert!'
        self.__dist = dist

        # colormode
        self.__colormode = "RGB"

        # color
        self.__color = color

    #################################################################################
    # "Set" Methods
    #################################################################################

    def Setup(self):
        self.__topmost = self.__topmargin
        self.__bottommost = self.__size[1] - self.__bottommargin
        self.__leftmost = self.__leftmargin
        self.__rightmost = self.__size[0] - self.__rightmargin

    def SetFilename(self, filename):
        f = string.split(filename, '.')
        self.__filename = string.join(f, '.')
        self.__fileformat = f[-1:][0]
        
    def SetSize(self, size):
        self.__size = size

    def SetTopmargin(self, topmargin):
        if not isinstance(topmargin, types.IntType):
            raise error.Error, 'Die Vorgabe fuer den oberen Rand hat keinen Integer-Wert!'
        self.__topmargin = topmargin

    def SetBottommargin(self, bottommargin):
        if not isinstance(bottommargin, types.IntType):
            raise error.Error, 'Die Vorgabe fuer den unteren Rand hat keinen Integer-Wert!'
        self.__bottommargin = bottommargin
        
    def SetLeftmargin(self, leftmargin):
        if not isinstance(leftmargin, types.IntType):
            raise error.Error, 'Die Vorgabe fuer den linken Rand hat keinen Integer-Wert!'
        self.__leftmargin = leftmargin
        
    def SetRightmargin(self, rightmargin):
        if not isinstance(rightmargin, types.IntType):
            raise error.Error, 'Die Vorgabe fuer den rechten Rand hat keinen Integer-Wert!'
        self.__rightmargin = rightmargin

    def SetDist(self, dist):
        if not isinstance(dist, types.IntType):
            raise error.Error, 'Die Vorgabe fuer die Zwischenraeume der Bildelemente hat keinen Integer-Wert!'
        self.__dist = dist

    def SetColormode(self, colormode):
        self.__colormode = 'RGB'

    def SetColor(self, color):
        self.__color = color

    def SetWallpaper(self, wallpaper):
        self.__wallpaper = Image.open(wallpaper, 'r')

        
    ###########################################################################
    # Title Methods
    ###########################################################################

    def TopTitle(self, font, text='test'):
        s = ImageFont.load(font)
        self.__titlesize = (self.__size[0] - self.__leftmargin - self.__rightmargin,
                            s.getsize(text)[1] + 20)
        self.__titlelocation = (self.__leftmost, self.__topmost)
        self.__topmost = self.__topmost + self.__titlesize[1] + self.__dist

    def BottomTitle(self, font, text='test'):
        s = ImageFont.load(font)
        self.__titlesize = (self.__size[0] - self.__leftmargin - self.__rightmargin,
                            s.getsize(text)[1] + 20)
        self.__titlelocation = (self.__leftmost, self.__bottommost - self.__titlesize[1] - 10)
        self.__bottommost = self.__bottommost - self.__titlesize[1] - self.__dist

    def GetTitlesize(self):
        return self.__titlesize

    def GetTitlelocation(self):
        return self.__titlelocation
        
    ###########################################################################
    # Legend Methods
    ###########################################################################

    def LeftLegend(self, x, y, font):
        s = ImageFont.load(font)
        h = self.__bottommost - self.__topmost
        wid = []
        for i in range(0, len(x)):
            wid.append(s.getsize(str(x[i]) + str(y[i]))[0] + 20)
        maxwid = max(wid)
        maxhi = s.getsize(str(x[0]))[1] + 10
        vnum = (h - 10) / maxhi
        hnum = len(x) / vnum + 1
        self.__legendsize = (hnum*maxwid, h)
        self.__legendlocation = (self.__leftmost, self.__topmost)
        self.__leftmost = self.__leftmost + self.__legendsize[0] + self.__dist
    
    def RightLegend(self, x, y, font):
        s = ImageFont.load(font)
        h = self.__bottommost - self.__topmost
        wid = []
        for i in range(0, len(x)):
            wid.append(s.getsize(str(x[i]) + str(y[i]))[0] + 20)
        maxwid = max(wid)
        maxhi = s.getsize(str(x[0]))[1] + 10
        vnum = (h - 10) / maxhi
        hnum = len(x) / vnum + 1
        self.__legendsize = (hnum*maxwid, h)
        self.__legendlocation = (self.__rightmost - self.__legendsize[0], self.__topmost)
        self.__rightmost = self.__rightmost - self.__legendsize[0] - self.__dist

    def BottomLegend(self, x, y, font):
        s = ImageFont.load(font)
        w = self.__rightmost - self.__leftmost
        wid = []
        for i in range(0, len(x)):
            wid.append(s.getsize(str(x[i]) + str(y[i]))[0] + 20)
        maxwid = max(wid)
        maxhi = s.getsize(str(x[0]))[1] + 10
        hnum = (w - 10) / maxwid
        vnum = len(x) / hnum + 1
        self.__legendsize = (w, vnum*maxhi)
        self.__legendlocation = (self.__leftmost, self.__bottommost - self.__legendsize[1] - self.__dist)
        self.__bottommost = self.__bottommost - self.__legendsize[1] - self.__dist

    def GetLegendsize(self):
        return self.__legendsize

    def GetLegendlocation(self):
        return self.__legendlocation


    ##########################################################################################
    # Graphic Methods
    ##########################################################################################


    def Graphic(self):
        self.__graphicsize = (self.__rightmost - self.__leftmost,
                              self.__bottommost - self.__topmost)
        self.__graphiclocation = (self.__leftmost, self.__topmost)

    def GetGraphicsize(self):
        return self.__graphicsize

    def GetGraphiclocation(self):
        return self.__graphiclocation
    
    ##########################################################################################
    # Draw Methods
    ##########################################################################################

    def Draw(self):
        self.__pict = Image.new(self.__colormode, self.__size, self.__color)

    def AttachWallpaper(self, alpha = 0.0):
        self.__pict = Image.blend(self.__pict, self.__wallpaper, alpha)

    def Paste(self, part, location):
        self.__pict.paste(part, location)

    ##########################################################################################
    # Save and Show Methods
    ##########################################################################################

    def Save(self):
        self.__pict.save(open(self.__filename, "w"), self.__fileformat)

    def Show(self):
        self.__pict.show()
