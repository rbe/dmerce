#!/usr/local/bin/python
##
#
# $Author: rb $
revision = "$Revision: 2.6 $"
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2/')
import os
import types
import string
#import Core.OS
import Core.Log
import DMS.SDV.data
import DMS.SDV.colors
import DMS.SDV.file
import DMS.SDV.sdv
import DMS.SDV.title
import DMS.SDV.graphic
import DMS.MBase

class SDVImage(DMS.MBase.Class):

    """ create image """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        #self.__osBase = Core.OS.Base()
        self.__log = Core.Log.File(debug = self._debug, module = 'Graphic.Image')

    def SetSize(self, size):
        self.size = DMS.SDV.data.Size()
        if type(size) is types.StringType:
            size = string.split(size, ',')
        self.size.SetSize((int(size[0]), int(size[1])))

    def SetData(self, x, y):
        self.data = DMS.SDV.data.Data()
        self.data.SetX(x)
        self.data.SetY(y)

    def SetColor(self):
        self.bgc = DMS.SDV.colors.Color()
        self.tc = DMS.SDV.colors.Color()
        self.cl = DMS.SDV.colors.Colorlist()
        self.bgc.Init()
        self.bgc.SetValue((200, 200, 200))
        self.tc.Init()
        self.tc.SetValue((200, 200, 200))
        self.cl.Init(DMS.SDV.colors.DynamicList(len(self.data.GetX())))
        self.cl.SetMediumshade(50)
        self.cl.SetDarkshade(100)
        self.cl.SetMedium()
        self.cl.SetDark()

    def SetFont(self):
        self.tf = DMS.SDV.file.Font()
        self.tf.SetDir()
        self.tf.SetName('helvB14.pil')
        self.tf.Name2Path()

    def SetTitle(self, title):
        self.title = DMS.SDV.title.Title()
        self.title.Init()
        self.title.SetSize(self.sdv.GetTitlesize())
        self.title.SetText(title)
        self.title.SetFont(self.tf.GetPath())
        self.title.SetColor(self.tc.GetValue())
        self.title.Draw()

    def SetGraphic(self, xname = 'Xname', yname = 'Yname'):
        self.graphic = DMS.SDV.graphic.Graphic()
        self.graphic.Init(self.data.GetX(), self.data.GetY(0), self.sdv.GetGraphicsize())
        self.graphic.SetColor((200, 200, 200))
        self.graphic.SetLight(self.cl.GetLight())
        self.graphic.SetMedium(self.cl.GetMedium())
        self.graphic.SetDark(self.cl.GetDark())
        self.graphic.SetDevc((1, 1))
        self.graphic.SetDev()
        self.graphic.SetUp()
        self.graphic.SetXname(xname)
        self.graphic.SetYname(yname)
        self.graphic.ColumnMeasures()
        self.graphic.Draw()
        self.graphic.BackgroundLining()
        self.graphic.CubicColumns()
        self.graphic.StandardCoordinates()

    def SetSDV(self, filename):
        self.sdv = DMS.SDV.sdv.SDV()
        self.sdv.Init(filename = filename)
        self.sdv.SetSize(self.size.GetSize())
        self.sdv.Setup()
        self.sdv.SetColor(self.bgc.GetValue())
        self.sdv.TopTitle(self.tf.GetPath())
        # self.sdv.BottomLegend(self.data.GetX(), self.data.GetY(0), self.lf.GetPath())
        self.sdv.Graphic()

    def Draw(self):
        self.sdv.Draw()
        self.sdv.Paste(self.title.GetPicture(), self.sdv.GetTitlelocation())
        self.sdv.Paste(self.graphic.GetPicture(), self.sdv.GetGraphiclocation())
        self.sdv.Save()

    def Create(self, tmpdir = None, filename = None, title = 'Kein Titel', size = None,
               x = [], y = [], xname = '', yname = ''):
        #self.__log.Write(msg = 'DIRECTORY: %s' % os.getcwd())
        if not x or not y:
            return 'no-image-created-error.png'
        if not filename:
            filename = 'foo' + str(os.getpid()) + '.png'
        if tmpdir:
            filename = tmpdir + '/' + filename
        if not size:
            size = (640, 480)
        filename = string.lower(filename)
        self.SetSize(size)
        self.SetData(x, y)
        self.SetColor()
        self.SetFont()
        #self.SetSDV(self.__osBase.Env('DOCUMENT_ROOT') + filename)
        self.SetSDV(self._httpEnv('DOCUMENT_ROOT') + filename)
        self.SetTitle(title)
        self.SetGraphic(xname, yname)
        self.Draw()
        return filename

#g = SDVImage({})
#print g.Create(size = '1024,768', x = ['00-03', '03-06', '06-09', '09-12', '12-15', '15-18', '18-21', '21-00'], y = [0, 0, 0, 0, 0, 2L, 0, 0], xname = 'Uhrzeit', yname = 'Schäden')
