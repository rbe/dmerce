import types, math
import Image, ImageDraw, ImageFont
import lib, error, data 


class Graphic:

    def Init(self, x, y, size = (10, 10), color = (255, 255, 255),
                 light = [], medium = [], dark = [], textcolor = (0, 0, 0),
                 font = '/fonts/helvB14.pil', linecolor = (0, 0, 0),
                 backlinecolor = (150, 150, 150), mc = 1.1, devc = (1, 1), xres = 1,
                 yres = 1, xname = '', yname = '', colormode='RGB'):
        # x and y
        self.__x = x
        self.__y = y
        # ymax
        self.__ymax = max(self.__y)
        # size
        self.__size = size
        # colormode
        self.__colormode = colormode
        # background color
        self.__color = color
        # colorlists
        self.__light = light
        self.__medium = medium
        self.__dark = dark
        # text color
        self.__textcolor = textcolor
        # font
        self.__font = ImageFont.load(font)
        # line color
        self.__linecolor = linecolor
        # background line color
        self.__backlinecolor = backlinecolor
        # mc
        if not (isinstance(mc, types.IntType) or
                isinstance(mc, types.FloatType)):
            raise error.Error, 'Der Overspill Koeffizient ist nicht korrekt angegeben!'
        self.__mc = mc
        # perspectivical deviation coefficient
        if not (isinstance(devc, types.TupleType) and
                (isinstance(devc[0], types.IntType) or
                 isinstance(devc[0], types.FloatType)) and
                (isinstance(devc[1], types.IntType) or
                 isinstance(devc[1], types.FloatType)) and
                len(devc) == 2 and
                devc[0] >= 0 and
                devc[1] >= 0):
            raise error.Error, 'Der Koeffizient fuer die perspektivische Abweichung ist nicht korrekt angegeben!'
        self.__devc = devc
        # unitnames
        if not isinstance(xname, types.StringType):
            raise error.Error, 'Der Name fuer die x-Argumente ist kein String!'
        self.__xname = xname
        if not isinstance(yname, types.StringType):
            raise error.Error, 'Der Name fuer die y-Argumente ist kein String!'
        self.__yname = yname
        # res
        if not (isinstance(xres, types.IntType) or
                isinstance(xres, types.FloatType)):
            raise Error, 'Die Darstellungsaufloesung fuer die x-Achse ist nicht in korrektem Format angegeben!'
        self.__xres = xres
        if not (isinstance(yres, types.IntType) or
                isinstance(yres, types.FloatType)):
            raise Error, 'Die Darstellungsaufloesung fuer die y-Achse ist nicht in korrektem Format angegeben!'
        self.__yres = yres
        
    ##################################################################################
    # 'Set' Methods
    ##################################################################################

    def SetUp(self):
        # dev
        self.__dev = ((self.__size[0] / 100) * self.__devc[0], - (self.__size[1] / 100) * self.__devc[1])

        # leftmargin
        h = []
        for i in range(0, len(self.__y)):
            h.append(self.__font.getsize(str(self.__y[i]))[0])
        self.__namexmax = int(max(h))
        self.__leftmargin = max(self.__font.getsize(str(self.__ymax))[0] + 5,
                                self.__namexmax) + 10

        # textscales
        self.__textscale = self.__font.getsize('Tg')[1] + 10

        #xwidth
        lf = self.__font.getsize(str(self.__x[len(self.__x)-1]))[0]/2
        self.__xwidth = (self.__size[0] - self.__dev[0] - self.__leftmargin - lf - 30 - self.__font.getsize(self.__xname)[0])

        # xscale
        self.__xscale = 10.0 ** int(math.log10(len(self.__x)/3 + 1)) * self.__xres
        
        # yheight
        self.__yheight = self.__mc * (self.__ymax) +1

        # yscale
        self.__yscale = 10.0 ** (int(math.log10(self.__ymax/3 + 1))) * self.__yres

        # yunit
        self.__yunit = (self.__size[1] - 2*self.__textscale - 5 - self.__dev[1] - 2*self.__font.getsize('test')[1]) / self.__yheight

        # ysteps
        self.__ysteps = self.__yunit * self.__yscale
        

    def SetX(self, x):
        self.__x = x

    def SetY(self, y):
        self.__y = y

    def SetSize(self, size):
        self.__size = size

    def SetYmax(self, ymax):
        self.__ymax = ymax

    def SetYsum(self, ysum):
        self.__ysum = ymax

    def SetColormode(self, colormode):
        self.__colormode = colormode

    def SetColor(self, color):
        self.__color = color

    def SetLight(self, light):
        self.__light = light

    def SetMedium(self, medium):
        self.__medium = medium

    def SetDark(self, dark):
        self.__dark = dark

    def SetTextcolor(self, textcolor):
        self.__textcoloror = textcolor

    def SetFont(self, font):
        self.__font = ImageFont.load(font)

    def SetLinecolor(self, linecolor):
        self.__linecoloror = linecolor

    def SetBacklinecolor(self, backlinecolor):
        self.__backlinecolor = backlinecolor

    def SetMc(self, mc):
        self.__mc = mc

    def SetDevc(self, devc):
        self.__devc = devc

    def SetDev(self):
        self.__dev = ((self.__size[0] / 100) * self.__devc[0], - (self.__size[1] / 100) * self.__devc[1])

    def SetXname(self, xname):
        self.__xname = xname

    def SetYname(self, yname):
        self.__yname = yname

    def SetXres(self, xres):
        self.__xres = xres

    def SetYres(self, yres):
        self.__yres = yres

    def ColumnMeasures(self):
        self.__xunit = self.__xwidth / (len(self.__x)+1)
        self.__zero = (30 + self.__leftmargin, self.__size[1] - 2*self.__textscale - 5)
        self.__xaxis = [self.__zero, lib.Add(self.__zero, (self.__xunit * (len(self.__x)),0))]
        xfields = []
        for i in range(1, len(self.__x)+1):
                xfields.append(lib.Add(self.__zero, (i*self.__xunit, 0)))
        self.__xfields = xfields
        xmarks = []
        for i in range(1, len(self.__x)):
            xmarks.append(lib.Add(self.__zero, (i*self.__xunit*self.__xscale, 0)))
        self.__xmarks = xmarks
        self.__xsteps = self.__xunit * self.__xscale
        self.__yaxis = [self.__zero, lib.Add(self.__zero, (0, - (self.__yunit * self.__yheight)))]
        ypoints = []
        i = 1
        while i*self.__ysteps <= self.__yheight * self.__yunit:
            ypoints.append(lib.Add(self.__zero, (0, - i*self.__ysteps)))
            i = i + 1
        self.__ypoints = ypoints
        
    def GraphMeasures(self):
        self.__xunit = self.__xwidth / (len(self.__x))
        self.__zero = (self.__xunit + self.__leftmargin, self.__size[1] - 2*self.__textscale - 5)
        self.__xaxis = [self.__zero, (self.__zero[0] + (len(self.__x)-1)*self.__xunit, self.__zero[1])]
        xfields = []
        for i in range(1, len(self.__x)):
                xfields.append(lib.Add(self.__zero, (i*self.__xunit, 0)))
        self.__xfields = xfields
        xmarks = []
        for i in range(1, len(self.__x)):
            xmarks.append(lib.Add(self.__zero, (i*self.__xunit*self.__xscale, 0)))
        self.__xmarks = xmarks
        self.__xsteps = self.__xunit * self.__xscale
        self.__yaxis = [self.__zero, lib.Add(self.__zero, (0, - (self.__yunit * self.__yheight)))]
        ypoints = []
        i = 1
        while i*self.__ysteps <= self.__yheight * self.__yunit:
            ypoints.append(lib.Add(self.__zero, (0, - i*self.__ysteps)))
            i = i + 1
        self.__ypoints = ypoints
        
    #########################################################################################
    # Draw Methods
    #########################################################################################


    def Draw(self):
        self.__pict = Image.new(self.__colormode, self.__size, self.__color)
        self.__pic = ImageDraw.Draw(self.__pict)

    def GetPicture(self):
        return self.__pict
        
    def BackgroundLining(self):
        self.__pic.line([self.__zero, lib.Add(self.__zero, self.__dev)], self.__backlinecolor)
        self.__pic.line([lib.Add(self.__zero, self.__dev),
                       lib.Add(self.__xaxis[1], self.__dev)], self.__backlinecolor)
        self.__pic.line([self.__xaxis[1], lib.Add(self.__xaxis[1], self.__dev)], self.__backlinecolor)
        self.__pic.line([lib.Add(self.__zero, self.__dev), lib.Add(lib.Add(self.__zero, self.__dev),
                                                         (0, - self.__yunit*self.__yheight))],
                      self.__backlinecolor)
        self.__pic.line([lib.Add(self.__xaxis[1], self.__dev),
                       lib.Add(lib.Add(self.__xaxis[1], self.__dev), (0, - self.__yunit*self.__yheight))],
                      self.__backlinecolor)
        for i in self.__ypoints:
            self.__pic.line([i, lib.Add(i, self.__dev)], self.__backlinecolor)
        for i in self.__xmarks:
            self.__pic.line([i, lib.Add(i, self.__dev)], self.__backlinecolor)
        for i in self.__ypoints:
            self.__pic.line([lib.Add(i, self.__dev), (lib.Add(lib.Add(i,self.__dev),
                                                    (self.__xaxis[1][0] - self.__zero[0], 0)))],
                          self.__backlinecolor)


    def Points(self, col):
        blc = [self.__zero[0] - 2, self.__zero[1] - self.__y[0]*self.__yunit + 2]
        urc = [self.__zero[0] + 2, self.__zero[1] - self.__y[0]*self.__yunit - 2]
        self.__pic.rectangle([blc[0], blc[1], urc[0], urc[1]], col, col)
        for j in range(1, len(self.__x)):
            blc = [self.__xfields[j-1][0] - 2, self.__zero[1] - self.__y[j]*self.__yunit + 2]
            urc = [self.__xfields[j-1][0] + 2, self.__zero[1] - self.__y[j]*self.__yunit - 2]
            self.__pic.rectangle([blc[0], blc[1], urc[0], urc[1]], col, col)

    def Line(self, col):
        self.__pic.line([(self.__zero[0], self.__zero[1] - self.__y[0]*self.__yunit),
                         (self.__xfields[0][0], self.__zero[1] - self.__y[1]*self.__yunit)],
                        col)
        for j in range(2, len(self.__x)):
            self.__pic.line([(self.__xfields[j-2][0],
                              self.__xfields[j-2][1] - self.__y[j-1]*self.__yunit),
                             (self.__xfields[j-1][0],
                              self.__xfields[j-1][1] - self.__y[j]*self.__yunit)],
                            col)

    def Plane(self, col):
        for i in range(0, len(self.__y)):
            self.__pic.polygon([self.__zero[0], self.__zero[1] - self.__y[0]*self.__yunit,
                                self.__zero[0] + self.__dev[0],
                                self.__zero[1] - self.__y[0]*self.__yunit + self.__dev[1],
                                self.__xfields[0][0] + self.__dev[0],
                                self.__zero[1] - self.__y[1]*self.__yunit + self.__dev[1],
                                self.__xfields[0][0], self.__zero[1] - self.__y[1]*self.__yunit],
                               col, col)
            self.__pic.polygon([self.__zero[0],
                                self.__zero[1], self.__zero[0], self.__zero[1] - self.__y[0]*self.__yunit,
                                self.__xfields[0][0], self.__zero[1] - self.__y[1]*self.__yunit,
                                self.__xfields[0][0], self.__xfields[0][1]],
                               (lib.Partsub(col[0], 50), lib.Partsub(col[1], 50), lib.Partsub(col[2], 50)),
                               (lib.Partsub(col[0], 50), lib.Partsub(col[1], 50), lib.Partsub(col[2], 50)))
            for j in range(2, len(self.__x)):
                self.__pic.polygon([self.__xfields[j-2][0],
                                    self.__xfields[j-2][1] - self.__y[j-1]*self.__yunit,
                                    self.__xfields[j-2][0] + self.__dev[0],
                                    self.__xfields[j-2][1] - self.__y[j-1]*self.__yunit + self.__dev[1],
                                    self.__xfields[j-1][0] + self.__dev[0],
                                    self.__xfields[j-1][1] - self.__y[j]*self.__yunit + self.__dev[1],
                                    self.__xfields[j-1][0],
                                    self.__xfields[j-1][1] - self.__y[j]*self.__yunit],
                                   col)
                self.__pic.polygon([self.__xfields[j-2][0], self.__xfields[j-2][1],
                                    self.__xfields[j-2][0],
                                    self.__xfields[j-2][1] - self.__y[j-1]*self.__yunit,
                                    self.__xfields[j-1][0],
                                    self.__xfields[j-1][1] - self.__y[j]*self.__yunit,
                                    self.__xfields[j-1][0], self.__xfields[j-1][1]],
                                   (lib.Partsub(col[0], 50), lib.Partsub(col[1], 50), lib.Partsub(col[2], 50)),
                                   (lib.Partsub(col[0], 50), lib.Partsub(col[1], 50), lib.Partsub(col[2], 50)))
                self.__pic.polygon([self.__xfields[len(self.__xfields)-1][0],
                                    self.__xfields[len(self.__xfields)-1][1],
                                    self.__xfields[len(self.__xfields)-1][0],
                                    self.__xfields[len(self.__xfields)-1][1] - self.__y[len(self.__x)-1]*self.__yunit,
                                    self.__xfields[len(self.__xfields)-1][0] + self.__dev[0],
                                    self.__xfields[len(self.__xfields)-1][1] - self.__y[len(self.__x)-1]*self.__yunit + self.__dev[1],
                                    self.__xfields[len(self.__xfields)-1][0] + self.__dev[0],
                                    self.__xfields[len(self.__xfields)-1][1] + self.__dev[1]],
                                   col, col)

    def CubicColumns(self):
        for j in range(0, len(self.__x)):
            blc = (self.__zero[0] + j * self.__xunit +
                   self.__xunit/4, self.__zero[1]) 
            brc = (blc[0] + (self.__xunit / 2),
                   self.__zero[1])
            ulc = (blc[0], blc[1] - (self.__y[j] * self.__yunit))
            urc = (brc[0], brc[1] - (self.__y[j] * self.__yunit))
            self.__pic.rectangle([blc[0], blc[1], urc[0], urc[1]],
                               self.__medium[j], self.__medium[j])
            self.__pic.polygon([ulc[0], ulc[1], urc[0], urc[1], urc[0] +
                              self.__dev[0], urc[1] + self.__dev[1], ulc[0] +
                              self.__dev[0],
                              ulc[1] + self.__dev[1]], self.__light[j],
                             self.__light[j])
            self.__pic.polygon([urc[0], urc[1], brc[0], brc[1], brc[0] +
                              self.__dev[0], brc[1] + self.__dev[1],
                              urc[0] + self.__dev[0],
                              urc[1] + self.__dev[1]], self.__dark[j],
                             self.__dark[j])

    def CylindricColumns(self):
        for j in range(0, len(self.__x)):
            blc = (self.__zero[0] + j * self.__xunit + self.__xunit / 4,
                   self.__zero[1]) 
            brc = (blc[0] + self.__xunit / 2,
                   self.__zero[1])
            ulc = (blc[0], blc[1] - (self.__y[j] * self.__yunit))
            urc = (brc[0], brc[1] - (self.__y[j] * self.__yunit))
            self.__pic.ellipse([blc[0], blc[1] + self.__dev[1]+1,
                              brc[0], brc[1]],
                             self.__medium[j], self.__medium[j]) 
            self.__pic.rectangle([blc[0], blc[1] + self.__dev[1]/2, urc[0], urc[1] + self.__dev[1]/2],
                               self.__medium[j], self.__medium[j])
            self.__pic.ellipse([ulc[0], ulc[1] + (self.__dev[1]),
                              ulc[0] + (self.__xunit/2),
                              ulc[1]], self.__light[j],
                             self.__light[j])

    def Multicolumn(self):
        y = 0
        for i in self.__y:
            y = y + i
        ysum = y
        tlc = (self.__size[0]/10, 2 * self.__size[1]/10)
        blc = (self.__size[0]/10, self.__size[1] - self.__size[1]/10)
        height = tlc[1] - blc[1]
        width = self.__size[0]/5
        trc = (self.__size[0]/10 + width, 2 * self.__size[1]/10)
        brc = (self.__size[0]/10 + width, self.__size[1] - self.__size[1]/10)
        hunit = height / ysum
        sblc = blc
        sbrc = (sblc[0] + width, sblc[1])
        for i in range(0, len(self.__y)):
            strc = (sblc[0] + width, sblc[1] + self.__y[i] * hunit)
            self.__pic.rectangle([sblc[0], sblc[1], strc[0],
                                  strc[1]], self.__medium[i], self.__medium[i])
            self.__pic.polygon([sbrc, lib.Add(sbrc, self.__dev),
                                lib.Add(strc, self.__dev), strc],
                               self.__dark[i], self.__dark[i])
            self.__pic.line([(sbrc[0] + self.__dev[0]/2, sbrc[1] + self.__y[i] * hunit / 2),
                             (sbrc[0] + self.__dev[0]/2 + 50, sbrc[1] + self.__y[i] * hunit / 2)],
                             self.__linecolor)
            self.__pic.text((sbrc[0] + self.__dev[0]/2 + 55,
                             sbrc[1] + self.__y[i] * hunit / 2 - self.__font.getsize(str(self.__x[i]))[1]/2),
                            str(self.__x[i]) + ':   ' + str(self.__y[i]), self.__linecolor, self.__font) 

            sblc = (sblc[0], sblc[1] + self.__y[i] * hunit)
            sbrc = sbrc = (sblc[0] + width, sblc[1])


        self.__pic.polygon([sblc, lib.Add(sblc, self.__dev),
                            lib.Add(sbrc, self.__dev), sbrc],
                           self.__light[len(self.__y)-1],
                           self.__light[len(self.__y)-1])
        
    def Pie(self):
        self.__piesize = (4* min(self.__size[0], self.__size[1])/5,
                          4* min(self.__size[0], self.__size[1])/5)
        self.__pieheight = self.__piesize[1]/5 * (1 - self.__devc[1])
        self.__piedepth = self.__piesize[1] * self.__devc[1]
        center = (self.__size[0]/2, self.__size[1]/2)
        tcenter = (center[0], center[1] - self.__pieheight/2)
        bcenter = (center[0], center[1] + self.__pieheight/2)
        tblc = (tcenter[0] - self.__piesize[0]/2, tcenter[0] + self.__piedepth/2)
        tbrc = (tcenter[0] + self.__piesize[0]/2, tcenter[1] + self.__piedepth/2)
        tulc = (tcenter[0] - self.__piesize[0]/2, tcenter[1] - self.__piedepth/2)
        turc = (tcenter[0] + self.__piesize[0]/2, tcenter[1] - self.__piedepth/2)
        bblc = (bcenter[0] - self.__piesize[0]/2, bcenter[1] + self.__piedepth/2)
        bbrc = (bcenter[0] + self.__piesize[0]/2, bcenter[1] + self.__piedepth/2)
        bulc = (bcenter[0] - self.__piesize[0]/2, bcenter[1] - self.__piedepth/2)
        burc = (bcenter[0] + self.__piesize[0]/2, bcenter[1] - self.__piedepth/2)
        sum = 0
        for i in range(0, len(self.__x)):
            sum = sum + self.__y[i]
        for j in range(0, int(self.__pieheight)):
            s = 0.0
            for i in range(0, len(self.__y)):
                ang = ((self.__y[i]*1.0) / sum) * 360
                self.__pic.pieslice([bulc[0], bulc[1] - j, bbrc[0], bbrc[1] - j],
                                    s, s + ang, self.__medium[i], self.__medium[i])
                s = s + ang

        s = 0.0    
        for i in range(0, len(self.__y)):
            ang = ((self.__y[i]*1.0) / sum) * 360
            self.__pic.pieslice([tulc[0], tulc[1], tbrc[0], tbrc[1]], s, s + ang, self.__light[i], self.__light[i])
            s = s + ang

        s = (((self.__y[0]*1.0) / sum) * 2*math.pi) / 2
        self.__pic.line([(tcenter[0] + self.__piesize[0]/3 * math.cos(s),
                          tcenter[1] + self.__piesize[0]/3 * math.sin(s) * self.__devc[1]),
                         (tcenter[0] + 3 * self.__piesize[0]/4 * math.cos(s),
                          tcenter[1] + 3 * self.__piesize[0]/4 * math.sin(s) * self.__devc[1])],
                        self.__linecolor)
        self.__pic.text((tcenter[0] + 3 * self.__piesize[0]/4 * math.cos(s),
                          tcenter[1] + 3 * self.__piesize[0]/4 * math.sin(s) * self.__devc[1]),
                        str(self.__x[0]) + ':   ' + str(self.__y[0]), self.__linecolor, self.__font)
        s = 2 * s
        for i in range(1, len(self.__y)):
            ang = (((self.__y[i]*1.0) / sum) * 2*math.pi) / 2
            s = s + ang
            self.__pic.line([(tcenter[0] + self.__piesize[0]/3 * math.cos(s),
                              tcenter[1] + self.__piesize[0]/3 * math.sin(s) * self.__devc[1]),
                             (tcenter[0] + 3 * self.__piesize[0]/4 * math.cos(s),
                              tcenter[1] + 3 * self.__piesize[0]/4 * math.sin(s) * self.__devc[1])],
                            self.__linecolor)
            if (s >= 0 and s <= math.pi/2) or (s >= 3*math.pi/2 and s <= 2*math.pi):
                textx = tcenter[0] + 3 * self.__piesize[0]/4 * math.cos(s)
            else:
                textx = tcenter[0] + 3 * self.__piesize[0]/4 * math.cos(s) - self.__font.getsize(str(self.__x[i] + ':   ' + str(self.__y[i])))[0]
            if (s >= 0 and s <= math.pi):
                texty = tcenter[1] + 3 * self.__piesize[0]/4 * math.sin(s) * self.__devc[1]
            else:
                texty = tcenter[1] + 3 * self.__piesize[0]/4 * math.sin(s) * self.__devc[1] - self.__font.getsize(str(self.__x[i]))[1]

            self.__pic.text((textx, texty), str(self.__x[i]) + ':   ' + str(self.__y[i]), self.__linecolor, self.__font)
            s = s + ang

    ###########################################################################################################
    # Coordinate System Drawing Methods
    ###########################################################################################################

    def StandardCoordinates(self):
        self.__pic.line([self.__xaxis[0], self.__xaxis[1]], self.__linecolor)
        for i in self.__xmarks: 
            self.__pic.line([i, lib.Add(i, (0, 5))], self.__linecolor)
        # draw the y axis
        self.__pic.line([lib.Add(self.__zero, (0, 10)),
                       lib.Add(self.__zero, (0, - (self.__yunit * self.__yheight)))], self.__linecolor)
        # draw the points at y-axis
        for i in range(0, ((self.__yheight/self.__yscale) + 1)):
            self.__pic.line([lib.Add(self.__zero, (0, - i*self.__ysteps)),
                           lib.Add(self.__zero, (-10, - i*self.__ysteps))], self.__linecolor)
        # write x-unit name
        self.__pic.text((self.__size[0] - self.__font.getsize(self.__xname)[0],
                       self.__zero[1] - self.__font.getsize(self.__xname)[1]/2),
                      self.__xname, self.__textcolor, self.__font)
        # write y-unit name
        self.__pic.text((self.__zero[0] - self.__font.getsize(self.__yname)[0]/2, 0),
                      self.__yname, self.__textcolor, self.__font)
        # write (x-)arguments below x-axis
        c = 0
        for i in range(0, len(self.__x), self.__xscale):
            l = self.__font.getsize(str(self.__x[i]))[0]
            self.__pic.text((self.__zero[0] + c*self.__xunit*self.__xscale - l/2,
                           self.__zero[1] + 5),
                          str(self.__x[i]), self.__textcolor, self.__font)
            c = c+1
        # write (y-)values at the y-scale
        for i in range(0, ((self.__yheight/self.__yscale) + 1)):
            textx = self.__zero[0] - self.__font.getsize(str(int(self.__yheight)))[0] - 15
            texty = self.__zero[1] - i * self.__ysteps - self.__font.getsize(str(self.__yheight))[1]/2
            if self.__yscale * i >= 1 or self.__yscale * i == 0:
                self.__pic.text((textx, texty), '%s' %(int(self.__yscale * i)), self.__textcolor, self.__font)
            else:
                self.__pic.text((textx, texty), '%s' %(self.__yscale * i), self.__textcolor, self.__font)

    def EnumeratedColumns(self):
        # x-axis
        self.__pic.line([self.__xaxis[0], self.__xaxis[1]], self.__linecolor)
        self.__pic.line((self.__zero, lib.Add(self.__zero, (0,5))), self.__linecolor) 
        for i in range(0, len(self.__x)):
            self.__pic.line((self.__xfields[i], lib.Add(self.__xfields[i], (0,5))), self.__linecolor) 
        # draw the y axis
        self.__pic.line([lib.Add(self.__zero, (0, 10)),
                       lib.Add(self.__zero, (0, - (self.__yunit * self.__yheight)))], self.__linecolor)
        # draw the points at y-axis
        for i in range(0, self.__yheight/self.__yscale + 1):
            self.__pic.line([lib.Add(self.__zero, (0, - i*self.__ysteps)),
                           lib.Add(self.__zero, (-10, - i*self.__ysteps))], self.__linecolor)
        # write x-unit name
        self.__pic.text((self.__size[0] - self.__font.getsize(self.__xname)[0],
                       self.__zero[1] - self.__font.getsize(self.__xname)[1]/2),
                      self.__xname, self.__textcolor, self.__font)
        # write y-unit name
        self.__pic.text((self.__zero[0] - self.__font.getsize(self.__yname)[0]/2, 0),
                      self.__yname, self.__textcolor, self.__font)
        # write (x-)arguments below x-axis
        for i in range(0, len(self.__x)):
            self.__pic.text((self.__zero[0] + i*self.__xunit + self.__xunit/2 - self.__font.getsize(str(i+1))[0]/2,
                           self.__zero[1] + 7), str(i+1), self.__textcolor, self.__font)
        # write (y-)values at the y-scale
        for i in range(0, ((self.__yheight/self.__yscale) + 1)):
            textx = self.__zero[0] - 10
            texty = (self.__zero[1] - i*self.__ysteps)
            if self.__yscale * i >= 1 or self.__yscale * i == 0:
                self.__pic.text((textx-30, texty-5), '%s' %(int(self.__yscale * i)), self.__textcolor, self.__font)
            else:
                self.__pic.text((textx-30, texty-5), '%s' %(self.__yscale * i), self.__textcolor, self.__font)

    # numerated columns coordinate system type
    def StandardColumns(self):
        # x-axis
        self.__pic.line([self.__xaxis[0], self.__xaxis[1]], self.__linecolor)
        self.__pic.line((self.__zero, lib.Add(self.__zero, (0,5))), self.__linecolor) 
        for i in range(0, len(self.__x)):
            self.__pic.line((self.__xfields[i], lib.Add(self.__xfields[i], (0,5))), self.__linecolor) 
        # draw the y axis
        self.__pic.line([lib.Add(self.__zero, (0, 10)),
                       lib.Add(self.__zero, (0, - (self.__yunit * self.__yheight)))], self.__linecolor)
        # draw the points at y-axis
        for i in range(0, self.__yheight/self.__yscale + 1):
            self.__pic.line([lib.Add(self.__zero, (0, - i*self.__ysteps)),
                           lib.Add(self.__zero, (-10, - i*self.__ysteps))], self.__linecolor)
        # write x-unit name
        self.__pic.text((self.__size[0] - self.__font.getsize(self.__xname)[0],
                       self.__zero[1] - self.__font.getsize(self.__xname)[1]/2),
                      self.__xname, self.__textcolor, self.__font)
        # write y-unit name
        self.__pic.text((self.__zero[0] - self.__font.getsize(self.__yname)[0]/2, 0),
                      self.__yname, self.__textcolor, self.__font)
        # write (x-)arguments below x-axis
        for i in range(0, len(self.__x)):
            self.__pic.text((self.__zero[0] + i*self.__xunit + self.__xunit/2 - self.__font.getsize(str(self.__x[i]))[0]/2,
                           self.__zero[1] + 7), str(self.__x[i]), self.__textcolor, self.__font)
        # write (y-)values at the y-scale
        for i in range(0, ((self.__yheight/self.__yscale) + 1)):
            textx = self.__zero[0] - 10
            texty = (self.__zero[1] - i*self.__ysteps)
            if self.__yscale * i >= 1 or self.__yscale * i == 0:
                self.__pic.text((textx-30, texty-5), '%s' %(int(self.__yscale * i)), self.__textcolor, self.__font)
            else:
                self.__pic.text((textx-30, texty-5), '%s' %(self.__yscale * i), self.__textcolor, self.__font)

    # extended coordinate system type
    def ExtendedColumns(self):
        self.__pic.line([self.__xaxis[0], self.__xaxis[1]], self.__linecolor)
        self.__pic.line([lib.Add(self.__xaxis[0], (- self.__xunit, self.__textscale)),
                       lib.Add(self.__xaxis[1], (0, self.__textscale))], self.__linecolor)
        for i in range(1, len(self.__y)+2):
            self.__pic.line([(self.__zero[0] - self.__xunit, self.__zero[1] + i*self.__textscale),
                           (self.__xaxis[1][0], self.__zero[1] + i*self.__textscale)],
                          self.__linecolor)
        for i in self.__xfields:
            self.__pic.line([i, lib.Add(i, (0, (len(self.__y)+1) * self.__textscale))], self.__linecolor)
        # draw the y axis
        self.__pic.line([lib.Add(self.__zero, (0, (self.__textscale + self.__textscale))),
                       lib.Add(self.__zero, (0, - (self.__yunit * self.__yheight)))], self.__linecolor)
        # draw the points at y-axis
        for i in range(0, (self.__yheight/self.__yscale + 1)):
            self.__pic.line([lib.Add(self.__zero, (0, - i*self.__ysteps)),
                           lib.Add(self.__zero, (-10, - i*self.__ysteps))], self.__linecolor)
        # Write (y-)values into textfields
        for i in range(len(self.__y)):
            textx = (self.__leftmargin + (i + 1) * self.__xunit) + 10
            texty = (self.__size[1]  - self.__textscale) + 10
            self.__pic.text((textx , texty), '%s' % self.__y[0][0][i], self.__textcolor, self.__font)
        # write y-unitname
        self.__pic.text((self.__zero[0] - self.__font.getsize(self.__yname)[0]/2,
                       0), self.__yname, self.__textcolor, self.__font)
        # write (x-)arguments into text fields
        for i in range(2, len(self.__y)+2):
            textx = ((i - 1) * self.__xunit) + 5
            texty = (self.__size[1] - 2*self.__textscale) + 10
            self.__pic.text((textx, texty), self.__x[i - 2] , self.__textcolor, self.__font)
        # write (y-)values the y-scale
        for i in range(0, ((self.__yheight/self.__yscale) + 1)):
            textx = self.__zero[0] - 12 - self.__font.getsize(str(int(self.__yheight)))[0]
            texty = self.__zero[1] - i*self.__ysteps - self.__font.getsize(str(self.__yheight))[1]/2 
            if self.__yscale * i >= 1 or self.__yscale * i == 0:
                self.__pic.text((textx, texty), '%s' %(int(self.__yscale * i)), self.__textcolor, self.__font)
            else:
                self.__pic.text((textx, texty), '%s' %(self.__yscale * i), self.__textcolor, self.__font)
