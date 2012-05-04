import math, Image, ImageDraw, ImageFont, string
import time

def Partsub(x, y):
    if x < y:
        return 0
    else:
        return x - y

def Partadd(x, y, z):
    if x + y > z:
        return z
    else:
        return x + y

class Diagram:

    def __init__(self, x, y, filename, title, unitname, type = 'bars', size = (640, 480), res = 1, 
                 xmargin = 30, ymargin = 30, devc = (1,1), lines_text = 1, textscale2 = 30, 
                 mc = 1.1, linecol = (0,0,0), backgroundcol = (255,255,255), titlefontcol = (0,0,0),
                 textfontcol = (0,0,0), piesidecol = (150,150,150),
                 colorformat = "RGB", titlefont = 'helvB12.pil', font = 'helvB10.pil'):
        self.__x = x
        self.__y = y
        self.__type = type
        f = string.split(filename, '.')
        self.__filename = string.join(f, '.')
        self.__fileformat = f[-1:][0]
        self.__size = size
        self.__xmargin = xmargin
        self.__ymargin = ymargin
        self.__piebottommargin = (len(self.__x)/2 + 1) * 20
        self.__diagrambottommargin = (len(self.__x)/2 + 1) * 20
        self.__devc = devc
        self.__unitname = unitname
        self.__res = res
        self.__dev = ((self.__size[0] / 50) * self.__devc[0], - (self.__size[1] / 50) * self.__devc[1])
        self.__titlespace = 50
        self.__piesize = (min(self.__size[0] - 2 * self.__xmargin, self.__size[1] - self.__ymargin - self.__piebottommargin),
                          min(self.__size[0] - 2 * self.__xmargin, self.__size[1] - self.__ymargin - self.__piebottommargin))
        self.__piewidth = self.__piesize[0] * self.__devc[0]
        self.__pieheight = min((self.__piesize[1])/10 * self.__devc[1], self.__piesize[1])
        self.__piedepth = self.__piesize[1] - self.__pieheight
        self.__xwidth = (self.__size[0] - 2*self.__xmargin - self.__dev[0])
        self.__xunit = self.__xwidth / (len(self.__x) + 1)
        self.__mc = mc
        self.__yheight = self.__mc * max(self.__y)
        self.__textscale1 = 20 + lines_text * 10
        self.__textscale2 = textscale2
        self.__title = title        
        self.__yscale = 10.0 ** (int(math.log10(max(y)/3)))
        self.__yunit = (self.__size[1] - 2*self.__ymargin - self.__textscale1 - self.__textscale2 - self.__dev[1] - self.__titlespace) / self.__yheight
        self.__numyunit = (self.__size[1] - 2*self.__ymargin - self.__textscale1 - self.__diagrambottommargin - self.__dev[1] -
                           self.__titlespace) / self.__yheight
        self.__ysteps = self.__yunit * self.__yscale
        self.__numysteps = self.__numyunit * self.__yscale
        col = []
        c = 255/(len(self.__y)/6 + 2)
        for j in range(c, 255, c):
            col.append((255,j,0))
            col.append((j,255,0))
            col.append((j,0,255))
            col.append((255,0,j))
            col.append((0,255,j))
            col.append((0,j,255))
        self.__colors = col
        col1 = []
        col2 = []
        for i in self.__colors:
            x = []
            y = []
            for j in i:
                x.append(Partsub(j, 50))
                y.append(Partsub(j, 100))
            col1.append(tuple(x))
            col2.append(tuple(y))
        self.__colors1 = col1
        self.__colors2 = col2
        self.__linecol = linecol
        self.__backgroundcol = backgroundcol
        self.__titlefontcol = titlefontcol
        self.__textfontcol = textfontcol
        self.__piesidecol = piesidecol
        self.__colorformat = colorformat
        self.__font = font
        self.__titlefont = titlefont
        self.__zero = (self.__xmargin + self.__xunit, self.__size[1] - self.__ymargin - self.__textscale2 - self.__textscale1)
        self.__xaxis = [self.__zero, self.Add(self.__zero, (self.__xunit * len(self.__x),0))]
        self.__yaxis = [self.__zero, self.Add(self.__zero, (0, - (self.__yunit * self.__yheight)))]
        self.__numzero = (self.__xmargin + self.__xunit, self.__size[1] - self.__ymargin - self.__diagrambottommargin - self.__textscale1)
        self.__numxaxis = [self.__numzero, self.Add(self.__numzero, (self.__xunit * len(self.__x),0))]
        self.__numyaxis = [self.__numzero, self.Add(self.__numzero, (0, - (self.__numyunit * self.__yheight)))]
        xfields = []
        for i in range(1, len(self.__x)+1):
            xfields.append(self.Add(self.__zero, (i*self.__xunit, 0)))
        self.__xfields = xfields
        numxfields = []
        for i in range(1, len(self.__x)+1):
            numxfields.append(self.Add(self.__numzero, (i*self.__xunit, 0)))
        self.__numxfields = numxfields
        ypoints = []
        i = 1
        while i*self.__ysteps <= self.__yheight * self.__yunit:
            ypoints.append(self.Add(self.__zero, (0, - i*self.__ysteps)))
            i = i + 1
        self.__ypoints = ypoints
        numypoints = []
        i = 1
        while i*self.__numysteps <= self.__yheight * self.__numyunit:
            numypoints.append(self.Add(self.__numzero, (0, - i*self.__numysteps)))
            i = i + 1
        self.__numypoints = numypoints
        
    def SetX(x):
        self.__x = x

    def SetY(y):
        self.__y = y

    def SetFilename(f):
        self.__filename = f

    def SetTitle(t):
        self.__title = t

    def SetUnitname(u):
        self.__unitname = u

    def SetType(t):
        self.__type = t

    def SetSize(s = (640,480)):
        self.__size = s

    def SetRes(r = 1):
        self.__res = r

    def SetXmargin(x = 30):
        self.__xmargin = x

    def SetYmargin(y = 30):
        self.__ymargin = y

    def SetDevc(d = (1,1)):
        self.__devc = d

    def SetLines_text(l = 1):
        self.__lines_text = l

    def SetTextscale2(t = 30):
        self.__textscale2 = t

    def SetMc(m = 1.1):
        self.__mc = m

    def SetLinecol(l = (0,0,0)):
        self.__linecol = l
    
    def SetBackgroundcol(b):
        self.__backgroundcol = b

    def SetTitlefontcol(t):
        self.__titlefontcol = t

    def SetTextfontcol(t):
        self.__textfontcol = t
        
    def SetPiesidecol(p):
        self.__piesidecol = p
        
    def SetColorformat(c):
        self.__colorformat = c

    def SetTitlefont(t):
        self.__titlefont = t

    def SetFont(f):
        self.__font = f


    # vector arithmetics
    # vector addition
    def Add(self, x, y):
        return (x[0] + y[0], x[1] + y[1])

    # draw a pie
    def Draw_Pie(self):
        id = Image.new(self.__colorformat, self.__size, self.__backgroundcol)
        im = ImageDraw.Draw(id)
        font = ImageFont.load('/fonts/' + self.__font)
        fontt = ImageFont.load('/fonts/' + self.__titlefont) 
        blc = (self.__xmargin, self.__ymargin + self.__piesize[1])
        brc = (self.__xmargin + self.__piewidth, self.__ymargin + self.__piesize[1])
        center = (blc[0] + self.__piewidth / 2, blc[1] - self.__piedepth / 2 - self.__pieheight)
        im.ellipse([blc[0], blc[1] - self.__piedepth, brc[0], brc[1]], self.__piesidecol, self.__piesidecol)
        im.rectangle([blc[0], blc[1] - self.__piedepth / 2, brc[0], brc[1] - self.__piedepth / 2 - self.__pieheight],
                     self.__piesidecol, self.__piesidecol)
        im.line([(blc[0], blc[1] - self.__piedepth / 2), (blc[0] + self.__piewidth, blc[1] - self.__piedepth / 2)], self.__piesidecol)
        im.ellipse([blc[0], blc[1] - self.__piedepth - self.__pieheight, brc[0], brc[1] - self.__pieheight], self.__piesidecol, self.__piesidecol)
        sum = 0
        for i in range(0, len(self.__y)):
            sum = sum + self.__y[i]
        s = 0.0
        for i in range(0, len(self.__y)):
            ang = ((self.__y[i]*1.0) / sum) * 360
            im.pieslice([blc[0], blc[1] - self.__piedepth - self.__pieheight, brc[0], brc[1] - self.__pieheight],
                        s, s + ang, self.__colors[i], self.__colors[i])
            s = s + ang
        for i in range(0, len(self.__y), 2):
            im.text((self.__xmargin, self.__size[1] - self.__piebottommargin + i/2*15 +10),
                    self.__x[i] + ':', self.__colors[i], font)
            im.text((self.__xmargin + 300, self.__size[1] - self.__piebottommargin + i/2*15 +10),
                    str(self.__y[i]), self.__colors[i], font)
            if i+1 < len(self.__y):
                im.text((self.__xmargin + 400, self.__size[1] - self.__piebottommargin + i/2*15 +10),
                        self.__x[i+1] + ':', self.__colors[i+1], font)
                im.text((self.__xmargin + 600, self.__size[1] - self.__piebottommargin + i/2*15 +10),
                        str(self.__y[i+1]), self.__colors[i+1], font)

        if self.__title != '':
            im.text((self.__piesize[0] + 30, 30), self.__title, self.__titlefontcol, fontt)

        id.save(open(self.__filename, "w"), self.__fileformat) 


    # draw a diagram
    def Draw(self):
        id = Image.new(self.__colorformat, self.__size, self.__backgroundcol)
        im = ImageDraw.Draw(id)
        font = ImageFont.load('/fonts/' + self.__font)
        fontt = ImageFont.load('/fonts/' + self.__titlefont) 

        # x - axis and text fields below it
        im.line([self.__xaxis[0], self.__xaxis[1]], self.__linecol)
        im.line([self.Add(self.__xaxis[0], (- self.__xunit, self.__textscale1)),
                 self.Add(self.__xaxis[1], (0, self.__textscale1))], self.__linecol)
        im.line([self.Add(self.__xaxis[0], (- self.__xunit, self.__textscale1 + self.__textscale2)),
                 self.Add(self.__xaxis[1], (0, self.__textscale1 + self.__textscale2))], self.__linecol)
        im.line([self.Add(self.__xaxis[0], (- self.__xunit, self.__textscale1)),
                 self.Add(self.__xaxis[0], (- self.__xunit, self.__textscale1 + self.__textscale2))], self.__linecol)

        for i in self.__xfields:
            im.line([i, self.Add(i, (0, self.__textscale1 + self.__textscale2))], self.__linecol)

        # y axis
        im.line([self.Add(self.__zero, (0, (self.__textscale1 + self.__textscale2))),
                 self.Add(self.__zero, (0, - (self.__yunit * self.__yheight)))], self.__linecol)

        # y - scale
        for i in range(0, ((max(self.__y)/self.__yscale) + 2)):
            im.line([self.Add(self.__zero, (0, - i*self.__ysteps)), self.Add(self.__zero, (-10, - i*self.__ysteps))], self.__linecol)

        # Write title
        if self.__title != '':
            im.text((self.__size[1] / 1.5, 30), self.__title, self.__titlefontcol, fontt)

        # Fill lower fields with text-data
        for i in range(len(self.__y)):
            textx = (self.__xmargin + (i + 1) * self.__xunit) + 10
            texty = (self.__size[1] - self.__ymargin - self.__textscale1) + 10
            im.text((textx , texty), '%s' % self.__y[i], self.__textfontcol, font)

        # write unitname
        textx = self.__xmargin + 5
        texty = (self.__size[1] - self.__ymargin - self.__textscale2) + 10
        im.text((textx, texty), self.__unitname , self.__textfontcol, font)

        # write text into text fields while creating them
        for i in range(2, len(self.__y)+2):
            textx = (self.__xmargin + (i - 1) * self.__xunit) + 5
            texty = (self.__size[1] - self.__ymargin - self.__textscale1 - self.__textscale2) + 10
            im.text((textx, texty), self.__x[i - 2] , self.__textfontcol, font)
        
        # describing scales with text
        for i in range(0, ((max(self.__y)/self.__yscale) + 2)):
            textx = (self.__xmargin + self.__xunit) - 10
            texty = (self.__size[1] - self.__ymargin - self.__textscale2 - self.__textscale1 - self.__res*i*self.__ysteps)
            if self.__yscale * i >= 1:
                im.text((textx-30, texty-5), '%s' %(int(self.__yscale * i)), self.__textfontcol, font)
            else:
                im.text((textx-30, texty-5), '%s' %(self.__yscale * i), self.__textfontcol, font)
            
        # the 3D stuff
        im.line([self.__zero, self.Add(self.__zero, self.__dev)], self.__linecol)
        im.line([self.Add(self.__zero, self.__dev), (self.Add(self.Add(self.__zero,self.__dev), (len(self.__x) * self.__xunit, 0)))], self.__linecol)
        im.line([self.Add(self.__zero, self.__dev), self.Add(self.__ypoints[len(self.__ypoints)-1], self.__dev)], self.__linecol)
        im.line([self.Add(self.__xfields[len(self.__xfields)-1], self.__dev),
                 self.Add(self.Add(self.__ypoints[len(self.__ypoints)-1],self.__dev), (len(self.__x) * self.__xunit, 0))], self.__linecol)
        for i in self.__ypoints:
            im.line([i, self.Add(i, self.__dev)], self.__linecol)
        for i in self.__xfields:
            im.line([i, self.Add(i, self.__dev)], self.__linecol)
        for i in self.__ypoints:
            im.line([self.Add(i, self.__dev), (self.Add(self.Add(i,self.__dev), (len(self.__x) * self.__xunit, 0)))], self.__linecol)

        # bars or cylinders
        if self.__type == 'bars':
            for i in range(1, len(self.__x) + 1):
                blc = (self.__xmargin + (i * self.__xunit) + (self.__xunit / 4),
                       self.__size[1] - self.__ymargin - self.__textscale1 - self.__textscale2)
                brc = (blc[0] + (self.__xunit / 2),
                       self.__size[1] - self.__ymargin - self.__textscale1 - self.__textscale2)
                ulc = (blc[0], blc[1] - (self.__y[i-1] * self.__yunit))
                urc = (brc[0], brc[1] - (self.__y[i-1] * self.__yunit))
                im.rectangle([blc[0], blc[1], urc[0], urc[1]], self.__colors1[i-1], self.__colors1[i-1])
                im.polygon([ulc[0], ulc[1], urc[0], urc[1], urc[0] + self.__dev[0], urc[1] + self.__dev[1], ulc[0] + self.__dev[0],
                            ulc[1] + self.__dev[1]], self.__colors[i-1], self.__colors[i-1])
                im.polygon([urc[0], urc[1], brc[0], brc[1], brc[0] + self.__dev[0], brc[1] + self.__dev[1], urc[0] + self.__dev[0],
                            urc[1] + self.__dev[1]], self.__colors2[i-1], self.__colors2[i-1])

        elif self.__type == 'cylinders':
            for i in range(1, len(self.__x) + 1):
                blc = (self.__xmargin + (i * self.__xunit) + (self.__xunit / 4),
                       (self.__dev[1]/2) + self.__size[1] - self.__ymargin - self.__textscale1 - self.__textscale2)
                brc = (blc[0] + (self.__xunit / 2),
                       (self.__dev[1]/2) + self.__size[1] - self.__ymargin - self.__textscale1 - self.__textscale2)
                ulc = (blc[0], blc[1] - (self.__y[i-1] * self.__yunit))
                urc = (brc[0], brc[1] - (self.__y[i-1] * self.__yunit))
                im.ellipse([blc[0], blc[1] + (self.__dev[1]/2), blc[0] + (self.__xunit/2), blc[1] - (self.__dev[1]/2)],
                           self.__colors1[i-1], self.__colors1[i-1]) 
                im.rectangle([blc[0], blc[1], urc[0], urc[1]], self.__colors1[i-1], self.__colors1[i-1])
                im.line([blc[0], blc[1], brc[0], brc[1]], self.__colors1[i-1])
                im.ellipse([ulc[0], ulc[1] + (self.__dev[1]/2), ulc[0] + (self.__xunit/2), ulc[1] - (self.__dev[1]/2)],
                           self.__colors[i-1], self.__colors[i-1]) 
            
        id.save(open(self.__filename, "w"), self.__fileformat)

    # draw an enumerated diagram
    def Draw_Num(self):
        id = Image.new(self.__colorformat, self.__size, self.__backgroundcol)
        im = ImageDraw.Draw(id)
        font = ImageFont.load('/fonts/' + self.__font)
        fontt = ImageFont.load('/fonts/' + self.__titlefont) 

        # x - axis and text fields below it
        im.line([self.__numxaxis[0], self.__numxaxis[1]], self.__linecol)
        im.line([self.Add(self.__numxaxis[0], (0, self.__textscale1)),
                 self.Add(self.__numxaxis[1], (0, self.__textscale1))], self.__linecol)
        for i in self.__numxfields:
            im.line([i, self.Add(i, (0, self.__textscale1))], self.__linecol)

        # y axis
        im.line([self.Add(self.__numzero, (0, (self.__textscale1))),
                 self.Add(self.__numzero, (0, - (self.__numyunit * self.__yheight)))], self.__linecol)

        # y - scale
        for i in range(0, ((max(self.__y)/self.__yscale) + 2)):
            im.line([self.Add(self.__numzero, (0, - i*self.__numysteps)), self.Add(self.__numzero, (-10, - i*self.__numysteps))], self.__linecol)

        # Write title
        if self.__title != '':
            im.text((self.__size[1] / 1.5, 30), self.__title, self.__titlefontcol, fontt)

        # write text into text fields
        for i in range(2, len(self.__y)+2):
            textx = (self.__xmargin + (i - 1) * self.__xunit) + 10
            texty = (self.__size[1] - self.__ymargin - self.__textscale1 - self.__diagrambottommargin) + 10
            im.text((textx, texty), str(i-1), self.__colors[i-2], font)
        
        # describing scales with text
        for i in range(0, ((max(self.__y)/self.__yscale) + 2)):
            textx = (self.__xmargin + self.__xunit) - 10
            texty = (self.__size[1] - self.__ymargin - self.__diagrambottommargin - self.__textscale1 - self.__res*i*self.__numysteps)
            if self.__yscale * i >= 1:
                im.text((textx-30, texty-5), '%s' %(int(self.__yscale * i)), self.__textfontcol, font)
            else:
                im.text((textx-30, texty-5), '%s' %(self.__yscale * i), self.__textfontcol, font)
            
        # the 3D stuff
        im.line([self.__numzero, self.Add(self.__numzero, self.__dev)], self.__linecol)
        im.line([self.Add(self.__numzero, self.__dev), (self.Add(self.Add(self.__numzero,self.__dev), (len(self.__x) * self.__xunit, 0)))], self.__linecol)
        im.line([self.Add(self.__numzero, self.__dev), self.Add(self.__numypoints[len(self.__numypoints)-1], self.__dev)], self.__linecol)
        im.line([self.Add(self.__numxfields[len(self.__numxfields)-1], self.__dev),
                 self.Add(self.Add(self.__numypoints[len(self.__numypoints)-1],self.__dev), (len(self.__x) * self.__xunit, 0))], self.__linecol)
        for i in self.__numypoints:
            im.line([i, self.Add(i, self.__dev)], self.__linecol)
        for i in self.__numxfields:
            im.line([i, self.Add(i, self.__dev)], self.__linecol)
        for i in self.__numypoints:
            im.line([self.Add(i, self.__dev), (self.Add(self.Add(i,self.__dev), (len(self.__x) * self.__xunit, 0)))], self.__linecol)

        # bars or cylinders
        if self.__type == 'bars':
            for i in range(1, len(self.__x) + 1):
                blc = (self.__xmargin + (i * self.__xunit) + (self.__xunit / 4),
                       self.__size[1] - self.__ymargin - self.__textscale1 - self.__diagrambottommargin)
                brc = (blc[0] + (self.__xunit / 2),
                       self.__size[1] - self.__ymargin - self.__textscale1 - self.__diagrambottommargin)
                ulc = (blc[0], blc[1] - (self.__y[i-1] * self.__numyunit))
                urc = (brc[0], brc[1] - (self.__y[i-1] * self.__numyunit))
                im.rectangle([blc[0], blc[1], urc[0], urc[1]], self.__colors1[i-1], self.__colors1[i-1])
                im.polygon([ulc[0], ulc[1], urc[0], urc[1], urc[0] + self.__dev[0], urc[1] + self.__dev[1], ulc[0] + self.__dev[0],
                            ulc[1] + self.__dev[1]], self.__colors[i-1], self.__colors[i-1])
                im.polygon([urc[0], urc[1], brc[0], brc[1], brc[0] + self.__dev[0], brc[1] + self.__dev[1], urc[0] + self.__dev[0],
                            urc[1] + self.__dev[1]], self.__colors2[i-1], self.__colors2[i-1])

        elif self.__type == 'cylinders':
            for i in range(1, len(self.__x) + 1):
                blc = (self.__xmargin + (i * self.__xunit) + (self.__xunit / 4),
                       (self.__dev[1]/2) + self.__size[1] - self.__ymargin - self.__textscale1 - self.__diagrambottommargin)
                brc = (blc[0] + (self.__xunit / 2),
                       (self.__dev[1]/2) + self.__size[1] - self.__ymargin - self.__textscale1 - self.__diagrambottommargin)
                ulc = (blc[0], blc[1] - (self.__y[i-1] * self.__numyunit))
                urc = (brc[0], brc[1] - (self.__y[i-1] * self.__numyunit))
                im.ellipse([blc[0], blc[1] + (self.__dev[1]/2), blc[0] + (self.__xunit/2), blc[1] - (self.__dev[1]/2)],
                           self.__colors1[i-1], self.__colors1[i-1]) 
                im.rectangle([blc[0], blc[1], urc[0], urc[1]], self.__colors1[i-1], self.__colors1[i-1])
                im.line([blc[0], blc[1], brc[0], brc[1]], self.__colors1[i-1])
                im.ellipse([ulc[0], ulc[1] + (self.__dev[1]/2), ulc[0] + (self.__xunit/2),
                            ulc[1] - (self.__dev[1]/2)], self.__colors[i-1], self.__colors[i-1]) 
            
        # write the annotations
        for i in range(0, len(self.__y), 2):
            im.text((self.__xmargin, self.__size[1] - self.__piebottommargin + i/2*15 +10),
                    str(i+1) + '. ' + self.__x[i] + ':', self.__colors[i], font)
            im.text((self.__xmargin + 200, self.__size[1] - self.__piebottommargin + i/2*15 +10),
                    str(self.__y[i]), self.__colors[i], font)
            if i+1 < len(self.__y):
                im.text((self.__xmargin + 300, self.__size[1] - self.__piebottommargin + i/2*15 +10),
                        str(i+2) + '. ' + self.__x[i+1] + ':', self.__colors[i+1], font)
                im.text((self.__xmargin + 500, self.__size[1] - self.__piebottommargin + i/2*15 +10),
                        str(self.__y[i+1]), self.__colors[i+1], font)

        id.save(open(self.__filename, "w"), self.__fileformat) 


