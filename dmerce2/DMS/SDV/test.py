import data
import colors
import file
import sdv
import title
import graphic

x=['00-03', '03-06', '06-09', '09-12', '12-15', '15-18', '18-21', '21-00']
y=[0, 0, 0, 0, 0, 2L, 0, 0]
#x = ['wert1', 'wert2', 'wert3', 'wert4', 'wert5', 'wert6', 'wert7', 'wert8', 'wert9', 'wert10']
#y = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

# Data
d = data.Data()
d.SetX(x)
d.SetY(y)

# Size
s = data.Size()
s.SetSize((800, 600))

# Color
bgc = colors.Color()
bgc.Init()
bgc.SetValue((200, 200, 200))
tc = colors.Color()
tc.Init()
tc.SetValue((200, 200, 200))
lc = colors.Color()
lc.Init()
lc.SetValue((200, 200, 200))
cl = colors.Colorlist()
#cl.Init(colors.BProgression(len(d.GetX())))
cl.Init(colors.DynamicList(len(d.GetX())))
cl.SetMediumshade(50)
cl.SetDarkshade(100)
cl.SetMedium()
cl.SetDark()

# Font
tf = file.Font()
tf.SetDir()
tf.SetName('helvB14.pil')
tf.Name2Path()
lf = file.Font()
lf.SetDir()
lf.SetName('helvB12.pil')
lf.Name2Path()

# SDV
sd = sdv.SDV()
sd.Init(filename = 'foo.gif')
sd.SetSize(s.GetSize())
sd.Setup()
sd.SetColor(bgc.GetValue())
sd.TopTitle(tf.GetPath())
sd.BottomLegend(d.GetX(), d.GetY(0), lf.GetPath())
sd.Graphic()

# Title
t = title.Title()
t.Init()
t.SetSize(sd.GetTitlesize())
t.SetText('MASY')
t.SetFont(tf.GetPath())
t.SetColor(tc.GetValue())
t.Draw()

# Graphic
g = graphic.Graphic()
g.Init(d.GetX(), d.GetY(0), sd.GetGraphicsize())
g.SetColor((200, 200, 200))
g.SetLight(cl.GetLight())
g.SetMedium(cl.GetMedium())
g.SetDark(cl.GetDark())
g.SetDevc((1, 1))
g.SetDev()
g.SetUp()
g.SetXname('Xname')
g.SetYname('Yname')
g.ColumnMeasures()
g.Draw()
g.BackgroundLining()
g.CubicColumns()
g.StandardCoordinates()

sd.Draw()
sd.Paste(t.GetPicture(), sd.GetTitlelocation())
#sd.Paste(l.GetPicture(), sd.GetLegendlocation())
sd.Paste(g.GetPicture(), sd.GetGraphiclocation())
sd.Save()
