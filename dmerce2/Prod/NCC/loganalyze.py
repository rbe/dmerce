import sdv, data, title, colors, graphic, legend
import file, log, lib

def Draw(x, y, tle = '', xname = '', yname = ''):
    
    # data Object
    d = data.Data()
    d.SetX(x)
    d.SetY(y)

    # Size Object
    s = data.Size()
    s.SetSize((800,600))

    # Color Objects
    background = colors.Color()
    background.Init()
    background.SetValue((200,200,200))
    
    # ColorList Object
    cl = colors.Colorlist()
    cl.Init(colors.BProgression(len(d.GetX())))
    cl.SetMediumshade(50)
    cl.SetDarkshade(100)
    cl.SetMedium()
    cl.SetDark()

    # Font Objects
    # Big Font
    bf = file.Font()
    bf.SetDir()
    bf.SetName('helvB12.pil')
    bf.Name2Path()
    # Small Font
    sf = file.Font()
    sf.SetDir()
    sf.SetName('helvB08.pil')
    sf.Name2Path()

    # SDV Class Call    
    sd = sdv.SDV()
    sd.Init()
    sd.SetSize(s.GetSize())
    sd.Setup()
    sd.SetColor(background.GetValue())
    sd.TopTitle(bf.GetPath())
    sd.Graphic()

    # Title Class Call
    t = title.Title()
    t.Init()
    t.SetSize(sd.GetTitlesize())
    t.SetText(tle)
    t.SetFont(bf.GetPath())
    t.SetColor(background.GetValue())
    t.Draw()

    # Graphic Class Call
    g = graphic.Graphic()
    g.Init(d.GetX(), d.GetY(0), sd.GetGraphicsize())
    g.SetColor(background.GetValue())
    g.SetLight(cl.GetLight())
    g.SetMedium(cl.GetMedium())
    g.SetDark(cl.GetDark())
    g.SetDevc((1,1))
    g.SetDev()
    g.SetUp()
    g.SetXname(xname)
    g.SetYname(yname)
    g.ColumnMeasures()
    g.Draw()
    g.BackgroundLining()
    g.CubicColumns()
    g.StandardCoordinates()

    # Drawing and Pasting the whole picture
    sd.Draw()
    sd.Paste(t.GetPicture(), sd.GetTitlelocation())
    sd.Paste(g.GetPicture(), sd.GetGraphiclocation())
    sd.Show()

def MultiDraw():
    O = log.LogSet()
    O.SendmailRead('/home/tt/mail.log')

    S = log.Data()
    S.SetSet(O)
    S.SortByMonth()
    x = S.GetX()
    y = S.GetY()
    Draw(x, y, tle = 'Mails pro Monat',
         xname = 'Monate', yname = 'Anzahl e-mails')

    S = log.Data()
    S.SetSet(O)
    S.SortByDay()
    x = S.GetX()
    y = S.GetY()
    Draw(x, y, tle = 'Mails pro Tag/Monat', xname = 'Tage',
         yname = 'Anzahl e-mails')

    S = log.Data()
    S.SetSet(O)
    S.SortByHour()
    x = S.GetX()
    y = S.GetY()
    Draw(x, y, tle = 'Mails pro Stunde/Uhrzeit',
         xname = 'Uhrzeit', yname = 'Anzahl e-mails')

    S = log.Data()
    S.SetSet(O)
    S.SortByMinute()
    x = S.GetX()
    y = S.GetY()
    Draw(x, y, tle = 'Mails pro Minute/Stunde',
         xname = 'Minute', yname = 'Anzahl e-mails')

    S = log.Data()
    S.SetSet(O)
    S.SortBySecond()
    x = S.GetX()
    y = S.GetY()
    Draw(x, y, tle = 'Mails pro Sekunde/Minute',
         xname = 'Sekunde', yname = 'Anzahl e-mails')



