import random
import math
import time

class Place:
    def __init__(self, left, right, monster):
        self.left = left
        self.right = right
        self.next = None
        self.end = False
        if left == None:
            self.next = right
        if right == None:
            self.next = left
        self.monster = monster
        self.reward = None

x131 = Place(None,None,None)
x13th = [x131]
x131.end = True

x121 = Place(None,x131,None)
x122 = Place(x131,None,None)
x12th = [x121,x122]

x111 = Place(None,x121,None)
x112 = Place(x121,x122,None)
x113 = Place(x122,None,None)
x11th = [x111,x112,x113]

x101 = Place(None,x111,None)
x102 = Place(x111,x112,None)
x103 = Place(x112,x113,None)
x104 = Place(x113,None,None)
x10th = [x101,x102,x103,x104]

x91 = Place(None,x101,None)
x92 = Place(x101,x102,None)
x93 = Place(x102,x103,None)
x94 = Place(x103,x104,None)
x95 = Place(x104,None,None)
x9th = [x91,x92,x93,x94,x95]

x81 = Place(None,x91,None)
x82 = Place(x91,x92,None)
x83 = Place(x92,x93,None)
x84 = Place(x93,x94,None)
x85 = Place(x94,x95,None)
x86 = Place(x95,None,None)
x8th = [x81,x82,x83,x84,x85,x86]

x71 = Place(x81,x82,None)
x72 = Place(x82,x83,None)
x73 = Place(x83,x84,None)
x74 = Place(x84,x85,None)
x75 = Place(x85,x86,None)
x7th = [x71,x72,x73,x74,x75]

x61 = Place(x71,x72,None)
x62 = Place(x72,x73,None)
x63 = Place(x73,x74,None)
x64 = Place(x74,x75,None)
x6th = [x61,x62,x63,x64]

x51 = Place(x61,x62,None)
x52 = Place(x62,x63,None)
x53 = Place(x63,x64,None)
x5th = [x51,x52,x53]

x41 = Place(x51,x52,None)
x42 = Place(x52,x53,None)
x4th = [x41,x42]

x31 = Place(x41,x42,None)
x3rd = [x31]

x21 = Place(x31,None,None)
x2nd = [x21]

x11 = Place(x21,None,None)
x1st = [x11]


class Character:
    gold = 0
    target = None
    burnduration = 0
    healduration = 0
    def __init__(self,name,message,health,damage,potency,moves,location):
        self.name = name
        self.maxhealth = health
        self.health = health
        self.damage = damage
        self.potency = potency
        self.moves = moves
        self.location = location
        self.message = message
    def damaged(self,amount):
        self.health -= amount
    def fight(self,move,target):
        move.action(self,target)
        if self.burnduration > 0 :
            current = self.health
            self.health -= max(.1*self.health,.05*self.maxhealth)
            print("Burned {} damage!".format(round(current-self.health,1)))
            self.burnduration -= 1
        if self.healduration > 0:
            current = self.health
            self.health = min([(self.health + .1*self.maxhealth),self.maxhealth,(self.health+15)])
            print("Healed {} health!".format(round(self.health-current,1)))
            self.healduration -= 1


class Punch:
    message = "Just a normal punch"
    accuracy = 90
    potency = 3
    def __init__(self):
        self.name = "Punch"
    def action(self,owner,target):
        target.damaged(owner.damage)
        print("""
        Did {} damage!



        """.format(round(owner.damage,1)))

class Heal:
    message = "Heals you"
    accuracy = 100
    potency = -20
    def __init__(self):
        self.name = "Heal"
    def action(self,owner,target):
        current = owner.health
        owner.health += random.randint(5,round(.15*owner.maxhealth))
        if owner.health > owner.maxhealth:
            owner.health = owner.maxhealth
        print("""
        Recovered {} health!



        """.format(round(owner.health-current,1)))

class Pawnch:
    message = "Just a normal punch, but stronger"
    accuracy = 60
    potency = 6
    def __init__(self):
        self.name = "SuperPunch"
    def action(self,owner,target):
        target.damaged(owner.damage*2)
        print("""
        Did {} damage!



        """.format(round(owner.damage*2,1)))

class Swipe:
    message = "Multi-hit hit that hits multiple times"
    accuracy = 90
    potency = 4
    def __init__(self):
        self.name = "Swipe"
    def action(self,owner,target):
        for n in range(2,random.randint(3,6)):
            target.damaged(owner.damage*.4)
            print("Did {} damage!".format(round(owner.damage*.4,1)))
            time.sleep(.2)
        print("""



        """)

class Slash:
    message = "A punch in a slashlike motion with increased critical chance"
    accuracy = 90
    potency = 10
    def __init__(self):
        self.name = "Slash"
    def action(self,owner,target):
        target.damaged(owner.damage*.8)
        print("""
        Did {} damage!



        """.format(round(owner.damage*.8,1)))

class DootDiddlyDonger:
    message = "Call upon the gods by cucking your opponent, destroying half of their health. Can only be used once in the entire game tho."
    accuracy = 100
    potency = -20
    def __init__(self):
        self.name = "Doot Diddly Donger"
    def action(self,owner,target):
        target.health = math.ceil(target.health/2)
        owner.moves.pop()
        print("""
        Cucked the enemy!



        """)

class Flamethrower:
    message = "Call upon the gods by bringing out your Boring Company Flamethrower and burning them for 3 turns."
    accuracy = 100
    potency = -20
    def __init__(self):
        self.name = "Flamethrower"
    def action(self,owner,target):
        target.burn = True
        target.burnduration = 3
        print("""
        Burned the enemy!



        """)

class Succ:
    message = "Call upon the gods by getting the good succ, healing you for 3 turns"
    accuracy = 100
    potency = -20
    def __init__(self):
        self.name = "The Good Succ"
    def action(self,owner,target):
        owner.heal = True
        owner.healduration = 3
        print("""
        Got the good succ!



        """)

class SuccPunch:
    message = "Call upon the gods to steal health from your opponent"
    accuracy = 100
    potency = -20
    def __init__(self):
        self.name = "Succ Punch"
    def action(self,owner,target):
        target.damaged(owner.damage*.7)
        owner.health += owner.damage*.7
        print("""
        Did {0} damage!
        Succed {1} health!



        """.format(round(owner.damage*.7,1),round(owner.damage*.7,1)))

class Explosion:
    message = "Call upon the gods to explode, hurting yourself but also exploding the enemy"
    accuracy = 100
    potency = -20
    def __init__(self):
        self.name = "Expuhlosion"
    def action(self,owner,target):
        target.damaged(owner.damage*10)
        owner.health -= owner.maxhealth*.33
        print("""
        Did {0} damage!
        Lost {1} health!



        """.format(round(owner.damage*10,1),round(owner.maxhealth*.33,1)))

class Kill:
    accuracy = 100
    potency = -20
    def __init__(self):
        self.name = "Kill"
    def action(self,owner,target):
        target.health = 0

x51.reward = Pawnch()
x52.reward = Swipe()
x53.reward = Slash()

x91.reward = Explosion()
x92.reward = Flamethrower()
x93.reward = Succ()
x94.reward = SuccPunch()
x95.reward = DootDiddlyDonger()

player = Character("BoBo",None,100,15,1,[Punch(),Heal()],x11)


monster1 = Character(r"""
It's the Helpful Tutorial Bear!
    .--.              .--.
   : (\ ". _......_ ." /) :
     '.    `        `    .'
     /'   _        _   `\
    /     0}      {0     \
   |       /      \       |
   |     /'        `\     |
    \   | .  .==.  . |   /
     '._ \.' \__/ './ _.'
     /  ``'._-''-_.'``  \
    (                    )
""",["Hey there newcomer, you must be BoBo. I am the gatekeeper to Heaven, so you must prove yourself in battle. Punch me.",
"Ouchie.",
"Punch me harder boi.",
"Ruh roh, looks like I hurt you a lil. Try healing.",
"Aight now kill me please.",
"I have no more purpose in life please just end me.",
"Please just let me die",
"Every day is wasted.",
"I wish no longer to suffer.",
"I'll even pay you.",
"This is what I want, man."]
,100,5,1,[Punch()],x11)
for n in x1st:
    n.monster = monster1

monster2 = Character(r"""
It's Mr. Potatohead, the Harbinger of Death!
              .-"'"-.
             |       |
           (`-._____.-')
        ..  `-._____.-'  ..
      .', :./'.== ==.`\.: ,`.
     : (  :   ___ ___   :  ) ;
     '._.:    |0| |0|    :._.'
        /     `-'_`-'     \
      _.|       / \       |._
    .'.-|      (   )      |-.`.
   //'  |  .-"`"`-'"`"-.  |  `\\
  ||    |  `~":-...-:"~`  |    ||
  ||     \.    `---'    ./     ||
  ||       '-._     _.-'       ||
 /  \       _/ `~:~` \_       /  \
||||\)   .-'    / \    `-.   (/||||
\|||    (`.___.')-(`.___.')    |||/
 '"      `-----'   `-----'     '")
 """,["Listen here, buddy, I'm really craving some blood right now.",
 "I hope you remembered to say goodbye to your loved ones.",
 "I will make sure you die quickly.",
 "My body parts are recycled from the ones I've ended",
 "Pain is temporary. Your death is forever.",
 "Even the Gods will kneel to me.",
 "Your pathetic life is simply an annoyance."
 "There is no God for you, only me. Submit.",
 "Satan consults me on designs for eternal punishment.",
 "I will make sure yours is long."]
 ,200,5,1,[Punch()],x21)
for n in x2nd:
    n.monster = monster2

monster3 = Character(r"""
It's a motherfucking X-Wing.
           __
.-.__      \ .-.  ___  __
|_|  '--.-.-(   \/\;;\_\.-._______.-.
(-)___     \ \ .-\ \;;\(   \       \ \
 Y    '---._\_((Q)) \;;\\ .-\     __(_)
 I           __'-' / .--.((Q))---'    \,
 I     ___.-:    \|  |   \'-'_          \
 A  .-'      \ .-.\   \   \ \ '--.__     '\
 |  |____.----((Q))\   \__|--\_      \     '
    ( )        '-'  \_  :  \-' '--.___\
     Y                \  \  \       \(_)
     I                 \  \  \         \,
     I                  \  \  \          \
     A                   \  \  \          '\
     |                    \  \__|           '
                           \_:.  \
                             \ \  \
                              \ \  \
                               \_\_))
""",["PEW PEW PEW",
"PEW PEW PEW",
"PEW PEW PEW",
"PEW PEW PEW",
"PEW PEW PEW",
"PEW PEW PEW",
"PEW PEW PEW",
"PEW PEW PEW",
"PEW PEW PEW",
"PEW PEW PEW",
"PEW PEW PEW"]
,200,7,1,[Punch(),Punch(),Punch(),Swipe()],x31)
for n in x3rd:
    n.monster = monster3

monster4 = Character(r"""
It's the Beef of Justice!
          .=     ,        =.
  _  _   /'/    )\,/,/(_   \ \
   `//-.|  (  ,\\)\//\)\/_  ) |
   //___\   `\\\/\\/\/\\///'  /
,-"~`-._ `"--'_   `"" `  _ \`'"~-,_
\       `-.  '_`.      .'_` \ ,-"~`/
 `.__.-'`/   (-\        /-) |-.__,'
   ||   |     \O)  /^\ (O/  |
   `\\  |         /   `\    /
     \\  \       /      `\ /
      `\\ `-.  /' .---.--.\
        `\\/`~(, '()      ('
         /(O) \\   _,.-.,_)
        //  \\ `\'`      /
       / |  ||   `"" ~"`
     /'  |__||
           `o))
""",["Moo"],
300,6,1,[Punch(),Punch(),Punch(),Pawnch(),Swipe(),Pawnch()],x41)
for n in x4th:
    n.monster = monster4

monster5 = Character(r"""
It's Po, the Eternal Defender of Truth!
             .-"-.
            : /"\ :
            `.`-'.'
              | |
           _..| |.._
      _  ."   ___   ".  _
     : `: .-""   ""-. :' :
     `  : : ()   () : :  '
      `.` `   ___   ' '.'
         `-`._____.'-'
        .-"        "-.
      ."               '
    .'  .^          .   '
   "   "             ".  "
  '   '     _______    ".  ".
 (_.-'.    :       :    ."._ )
      .    :       :    .
      .    '-------'    .
       .               .
        .      _      :
        :    .' `.    :
        :   .     .   '
        :   .     .   '
        :___.     .___'
       (____)     (____)
""",["I have been watching you intently. This shall be the true first test of your strength.",
"Only the strong can proceed.",
"Show me if you are truly worthy of going higher.",
"Truth is, you suck.",
"You think you can just simply keep going forward?",
"I am the Alpha. You are nothing but a waste.",
"Defeat me, and maybe you'll have a chance.",
"I am all-seeing, but to be quite frank, your future is still unclear.",
"Prove to me that you are truly ready for this quest."
"I envision your failure, but I still see something inside of you.",
"Something.",
"Something deep inside that drives you to keep going forward.",
"I want you to show it to me."],
300,5,5,[Punch(),Punch(),Punch(),Pawnch(),Swipe(),Pawnch(),Slash()],x51)
for n in x5th:
    n.monster = monster5

monster6 = Character(r"""
Oh shit it's Goku!
                  _
                   \"-._ _.--"~~"--._
                    \   "            ^.    ___
                    /                  \.-~_.-~
             .-----'     /\/"\ /~-._      /
            /  __      _/\-.__\L_.-/\     "-.
           /.-"  \    ( ` \_o>"<o_/  \  .--._\
          /'      \    \:     "     :/_/     "`
                  /  /\ "\    ~    /~"
                  \ I  \/]"-._ _.-"\
               ___ \|___/ ./    l   \___   ___
          .--v~   "v` ( `-.__   __.-' ) ~v"   ~v--.
       .-{   |     :   \_    "~"    _/   :     |   }-.
      /   \  |           ~-.,___,.-~           |  /   \
     ]     \ |                                 | /     |
     /\     \|     :                     :     |/     /)
""",["悟空です",
"何をしているかが自分もわからない",
"わたしは、あなたを愛しています",
"スキードッジスキードゥードゥーあなたのディックは今、麺です",
"私はアフリカで雨を降らせます",
"ああ、私は私にいくつかのアニメを愛するアニメ",
"ザワールド",
"ムダムダムダムダムダムダムダムダムダムダムダムダ",
"殺してください"],
200,7,2,[Punch(),Punch(),Pawnch(),Heal()],x61)
for n in x6th:
    n.monster = monster6

monster7 = Character(r"""
HIIIIIIIIIII
            ██████████  ████
        ████▒▒░░░░░░░░██▒▒░░██
      ██▒▒░░░░██░░██░░░░██░░░░██
    ██▒▒░░░░░░██░░██░░░░░░▒▒░░██
    ██░░░░░░░░██░░██░░░░░░▒▒▒▒██
  ██░░░░░░▒▒▒▒░░░░░░▒▒▒▒░░░░▒▒██
██▒▒░░░░░░░░░░░░██░░░░░░░░░░░░██
██░░░░▒▒░░░░░░░░██░░░░░░░░░░▒▒██
██░░░░▒▒░░░░░░░░░░░░░░░░░░░░██
  ██████░░░░░░░░░░░░░░░░░░▒▒██
██▒▒▒▒▒▒██░░░░░░░░░░░░░░░░▒▒██
██▒▒▒▒▒▒▒▒██░░░░░░░░░░░░▒▒██
██▒▒▒▒▒▒▒▒██░░░░░░░░░░▒▒████
  ██▒▒▒▒▒▒▒▒██▒▒▒▒▒▒████▒▒▒▒██
    ██▒▒▒▒██████████▒▒▒▒▒▒▒▒▒▒██
      ██████       ████████████
""",["HIIIIIIIII",
"HIIIIIIIII",
"HIIIIIIIII",
"HIIIIIIIII",
"HIIIIIIIII",
"HIIIIIIIII",
"ima succ you bitch",
"HIIIIIIIII",
"HIIIIIIIII",
"HIIIIIIIII",
"HIIIIIIIII"],
300,6,2,[Punch(),Pawnch(),Punch(),Punch(),Punch(),SuccPunch(),Succ(),Punch()],x71)
for n in x7th:
    n.monster = monster7

monster8 = Character(r"""
It's Garfield, Consumer of Worlds!
        _ooo--.
     @@@=@MMM\.`,_.',-
   _.\X/"/"   \  33,
  ===A   |     \ P""B
    /@,_ (  __,/""\.M\
    |; \"/\"_,/ / .'.A
    \,\._><-__./    "V
     \F _       a_3R"---,.
      _>"#           _   )
     (  /           .@J  /
     ) /           /    )
     ( |           \    /,
     | \            `,._,/ ___
      "=\,          ]@7,.n| P @\
          7-______.  \____.,   .)
          /  /     \ \      \WWW/
          |  |     |  |      ""'
 """, ["Have you come to feed me?",
 "I'm pretty hungry right now.",
 "You look pretty yummy."
 "You wouldn't mind if I ate you, would you?",
 "The very structure of the Universe hinges on me eating, so don't try to stop me.",
 "I will make sure to tear you apart quickly so that you can don't have to enjoy every second of pain.",
 "I will eat everything.",
 "Everything.",
 "Surrender to me."],
 400,6,2,[Punch(),Pawnch(),Punch(),SuccPunch(),Succ(),SuccPunch(),Explosion(),Punch(),Punch(),Punch(),Punch()],x81)
for n in x8th:
    n.monster = monster8

monster9 = Character(r"""
It's Sonic, God of Speed, Destroyer of Space and Time!
       ___------__
 |\__-- /\       _-
 |/    __      -
 //\  /  \    /__
 |  o|  0|__     --_
 \\____-- __ \   ___-
 (@@    __/  / /_
    -_____---   --_
     //  \ \\   ___-
   //|\__/  \\  \
   \_-\_____/  \-\
        // \\--\|
   ____//  ||_
  /_____\ /___\)
""",["Go, go, go, go, go, go, go, go, go, go",
"Gotta go fast, gotta go fast",
"Gotta go faster, faster, faster, faster, faster",
"Movin' at speed of sound (make tracks)",
"Quickest hedgehog around",
"Got ourselves a situation, stuck in a new location",
"Without any explanation, no time for relaxation!",
"Don't, don't, don't, don't, don't blink, don't think,",
"Just go, go, go, go, g-g-g-g-go, go!"],
400,5,7,[Slash(),Slash(),Slash(),Slash(),Swipe()],x91)
for n in x9th:
    n.monster = monster9

monster10 = Character(r"""
Napoleon Bonaparte!
         ________
        /        \
     __/       (o)\__
    /     ______\\   \
    |____/__  __\____|
       [  --~~--  ]
        |(  L   )|
  ___----\  __  /----___
 /   |  < \____/ >   |   \
/    |   < \--/ >    |    \
||||||    \ \/ /     ||||||
|          \  /   o       |
|     |     \/   === |    |
|     |      |o  ||| |    |
|     \______|   +#* |    |
|            |o      |    |
 \           |      /     /
 |\__________|o    /     /
 |           |    /     /)
""",["WEE WEE ITS TIME FOR YEUR DEIMISE",
"I AM NAHPOLEOWN! I WEELL NOT SURRENDEUR AGINST SUCH AN ISOEULENT FEWL"],
400,6,3,[Flamethrower(),SuccPunch(),Pawnch(),Punch(),Punch(),Punch(),Slash()],x101)
for n in x10th:
    n.monster = monster10

monster11 = Character(r"""
The Curator of Universal Mathematical Expression!
.-----------------------------.
| # Texax Instruments   TI-82 |
| .-------------------------. |
| |            ./           | |
| |            +            | |
| |. . . . . ./. . . . . . .| |
| |          / .            | |
| | X=5.2   /  .   Y=0      | |
| '-------------------------' |
| [Y=][WIN][ZOOM][TRACE][GRH] |
|                  _ [ ^ ] _  |
| [2nd][MODE][DEL]|_|     |_| |
| [ALP][XTO][STAT]   [ V ]    |
| [MATH][MAT][PGM][VARS][CLR] |
| [x-1] [SIN] [COS] [TAN] [^] |
|  [x2][ , ][ ( ][ ) ][ / ]   |
| [LOG][ 7 ][ 8 ][ 9 ] [ X ]  |
| [LN ][ 4 ][ 5 ][ 6 ] [ - ]  |
| [STO>][ 1 ][ 2 ][ 3 ][ + ]  |
| [ON][ 0 ][ . ][ (-) ][ENTR] |
| ----                        |
'-----------------------------'
""",[19857123407513475431501,
1451435436465135,
1345556245246,
134612834231943,
851924830455,
1324750705806066,
6569068945049584,
65986054850345,
494532404973,
243958046866,
92342342358590,
23423059854,
24395454560505,
60183491,
8008135],
500,7,4,[Punch(),Punch(),Punch(),Punch(),Punch(),Punch(),Pawnch(),Flamethrower(),Succ(),Pawnch(),Pawnch()],x111)
for n in x11th:
    n.monster = monster11

monster12 = Character(r"""
Totoro the Demon king
                =.,'__,---||-.____',.=
                =(: _     ||__    ):)=
               ,"::::`----||::`--':::"._
             ,':::::::::::||::::::::::::'.
     .__    ;:::.-.:::::__||___:::::.-.:::;   __,
         "-;:::( O )::::>_|| _<::::( O )::::-""
   =======;:::::`-`:::::::||':::::::`-`:::::\=======
    ,--"";:::_____________||______________::::""----.          , ,
         ; ::`._(    |    |||     |   )_,'::::\_,,,,,,,,,,____/,'_,
       ,;    :::`--._|____|||_____|_.-'::::::::::::::::::::::::);_
      ;/ /      :::::::::,||,:::::::::::::::::::::::::::::::::::/
     /; ``''''----------/,'/,__,,,,,____:::::::::::::::::::::,"
     ;/                :);/|_;| ,--.. . ```-.:::::::::::::_,"
    /;                :::):__,'//""\\. ,--.. \:::,:::::_,")
""",[],
666,6,6,[Punch(),Pawnch(),Punch(),Punch(),Punch(),Slash(),Swipe(),Explosion(),Succ(),SuccPunch(),Punch(),Pawnch()],x121)
for n in x12th:
    n.monster = monster12

monster13= Character(r"""
Mike Wazowski, Ender of Worlds, Anti-God, Destroyer of Hope
                           `.;;,`
                  `      ``;;;';:,`       `
                   +    `::;#@@@@#:,`    #+
                   #;   +,'#+;::+@@',;` ::'
                   '.,.','+,.'+#+:.@#,:',::
                   ;:++,'::;.```` #:'+,::,,
                   ;++';:#.```    ``;;':::`
                  `++',;;..``       `;'.::;
                  '+++#',.``         `,#,::;
                 :''+:':..``          `..,,:,
                `'++++,,..````';      `'#,.,:`
                ++++'':,...`###@@:`     ',,,,;`
               ,++++::,,...###'##`'     ,,,,,:,
              `+++++':,,,..+'@@@+##     :.,,,:;`
              '++++++:,,,,;':@@@@##     .,,,,,:;
             `'++++'+::,,,::.@@@#+#     `.,,,,::.
            `'+++++'';::,,,+::@@'+#     :..,.,::;
            .+++++'+;';::,,''::'+#     `:.,,,,,:::
            ++++++'';,;::,,,:'''@```````,..,,,:,:,`
           ;++++++''';:;;:,,,....`````.,...,,,:,:::
           ++++++++'';+;;;:,,,,..,.```,,...,,,::,:;`
          :+++++++'''';';;:::,,.......:....,,,,:::;:
          '+++++++'''';;+,;::,,,,,,,......,,,,,,::::`
         `'++++++++'''';::+:::::::.'......,,,,,:::::;
         :'++++++++''''';;;::'++;,,,,,,,.,,,,,:::;::;`
         +'++++++++'''''';;::::,,,..,,,,,,,:::,:::::'.
        `+'++++++++''''';;;;;::,,,,,,,:,,,,,::,::::;':
        `#'+++++++++''''';;;:::,,,.,,,,,,,,,,:::::;:;:
        `#'+++++++++'''''';;;::,,,...,,,,,,::::::;;:;:`
        :#;++++++++++'''''';;'++:....,,,,:,::::::;;:;:.
        '#'++++++++++''''''+@@@@@#+.,,,,,,::::::;;;;;:,
        +##+++++++++++''''@@@@@@@@@#,,:,::::::::;;;+'::
        +##'++#++#+++++++@@@@@@@@@@@@:::::::::;;;';;'::
        +#+'+++#+#++++++@@@@@@@@@@@@@#::::::;;;;'';`+;:
        +#+:+++#+###+++#@@@@@@@@@@@@@@#:::;;;;;;''; +;;
       `+#+`++++#####+'.@@###++##+#+#@;;:;;;;;;'''` +;:
        +#+ '++++###+##',`:'.`;'.`.#,';;;;;;;;;'';  +';
       `#+' `'++########'::;;';::;;:,:;;;'';;''''`  +':
       `##;  '+++#########+''';;:,,::;''';';'''';   ;+;`
       `##,  `'++##########++++###++++'''''''''+`   ,+;`
       `#+,  `,+++############+#++++++''''''+'+,    `':.
       `+'.    :'++###########++++++++++++++++;     .;;.
        +':     ;+++########+++++++++++++++++;`     .;:.
        +';     `,'++#++++++++++++++++++++++:       .;:.
        +''       `++++++++++++++++++++++++``       .;:.
        +''        ''++++''++++++'+++++++::`        `';.
        '''        +'':.;+'+''''''++''.,+:,`        `';.
        ,''        +++:    ``.....`    .+:,`        `;:.
        `''        +'',                `+:,`        `':`
        `';`       +''.                `';:`        `;;`
         ';`       +''`                `';,`         ;;`
""",["So this is it.",
"This is where it all ends",
"I hope you're prepared.",
"Because I am.",
"I will not let you succeed.",
"You will feel hope.",
"I will crush it.",
"And everything you love",
"You are nothing.",
"You're time is up.",
"It's my time.",
"My thousand-year kingdom.",
"No.",
"It will be me.",
"Forever."],
1000,7,6,[Punch(),Punch(),Punch(),Punch(),Pawnch(),Pawnch(),Slash(),Swipe(),Punch(),Punch(),Punch(),Slash(),Swipe(),Flamethrower(),Flamethrower(),Punch(),Pawnch(),Punch(),Punch(),Punch(),SuccPunch(),Succ(),Punch(),
Heal(),Punch(),Punch(),Pawnch()],
x131)
for n in x13th:
    n.monster = monster13


class Upgrade:
    def __init__(self, health, damage, crit, cost):
        self.health = health
        self.damage = damage
        self.crit = crit
        self.cost = cost
    def upgrade(self, player):
        if isinstance(self.health,int):
            player.maxhealth = player.maxhealth + self.health
            player.health = player.maxhealth
        player.damage += self.damage
        player.potency += self.crit
        player.gold -= self.cost


print("""
Welcome to BoBo's Unusual Escapade!
You are BoBo, a man seeking to beat the shit out of everything in his way and
finding his true purpose in life.
On his journey toward self-discovery, he must climb the unusually long staircase
toward Heaven which happens to be in the shape of a dungeon.
Help BoBo on his quest for meaning. Type "start()" to begin
""")

def start():
    while player.health > 0:
        if player.location.monster:
            enemy = player.location.monster
            print(enemy.name)
            if len(enemy.message) > 0:
                print(enemy.message[0])
                enemy.message = enemy.message[1:]
            print("""
            Your health:{0}   Enemy health:{1}
            """.format(round(player.health,1),round(enemy.health,1)))
            print("Your moves:")
            for n in player.moves:
                print(n.name)
            moveset = dict([(move.name,move) for move in player.moves])
            while True:
                move = input("""
                What move do you want to use?
                """)
                if move in moveset:
                    time.sleep(1)
                    if moveset[move].accuracy >= random.randint(1,100):
                        player.fight(moveset[move],enemy)
                        if (player.potency + moveset[move].potency) >= random.randint(1,20):
                            time.sleep(1)
                            print("Critical hit!")
                            moveset[move].action(player,enemy)
                    else:
                        print("""

                        Lmao you missed
                        """)
                    time.sleep(1)
                    break
                else:
                    print("""
                    That is not a valid move dafuq. Try again.

                    """)
            if enemy.health > 0:
                enemyattack = random.randint(0,len(enemy.moves)-1)
                print("""
                Enemy used {}
                """.format(enemy.moves[enemyattack].name))
                time.sleep(1)
                if enemy.moves[enemyattack].accuracy >= random.randint(1,100):
                    enemy.fight(enemy.moves[enemyattack],player)
                    if (enemy.potency + enemy.moves[enemyattack].potency) >= random.randint(1,20):
                        time.sleep(1)
                        print("Critical hit!")
                        enemy.moves[enemyattack].action(enemy,player)
                else:
                    print("""

                    Lmao they missed
                    """)
                enemy.damage += .05
                time.sleep(1)
            if enemy.health <= 0:
                print("Killed the enemy!")
                if enemy is monster13:
                    print("""
                    And so BoBo was able to ascend to the skies, defeating all
                    those who challenged his rightful spot in Heaven. The body
                    of Mike Wazowski in his arms, BoBo took the final steps up
                    the dungeon-like staircase, entering God's sanctuary of
                    peace and transcendance. Finally, BoBo could rest, for he
                    had found his true purpose, to be amongst cloud, for he knew
                    that he was now worthy. Casting the fallen Mike Wazowski
                    down to Earth, BoBo was left with no more challenges, and
                    became one with the skies.
                    """)
                    raise SystemExit
                player.location.monster = None
                player.gold += 10
                print("""
                Got 10 gold!
                """)
                time.sleep(2)
                while True:
                    if (player.gold < 5):
                        break
                    Upgrades = {"Heals":Upgrade(0,0,0,5),
                    "Health":Upgrade(15,0,0,10),
                    "Thicc":Upgrade(None,2.5,0,7.5),
                    "Swift":Upgrade(None,0,1,7.5),
                    "Armor":Upgrade(60,0,0,20),
                    "Sword":Upgrade(None,10,0,20),
                    "Lightning":Upgrade(None,0,4,20)}
                    print("""
                    Current gold - {}

                    Heals (5 gold) - heals you
                    Health (10 gold) - heals you and increases health
                    Thicc (7.5 gold) - sharpens your fists to be stronger
                    Swift (7.5 gold) - increases critical chance
                    Armor (20 gold) - heals you and greatly increases max health
                    Sword (20 gold) - greatly increases fists
                    Lightning (20 gold) - greatly increases critical chance
                    Nothing

                    """.format(player.gold))
                    shop = input("What would you like the upgrade? ")
                    if shop == "Nothing":
                        break
                    elif shop in Upgrades:
                        if Upgrades[shop].cost > player.gold:
                            print("You don't have enough money!")
                        else:
                            Upgrades[shop].upgrade(player)
                        time.sleep(1)
                    else:
                        print("That's not a valid item dafuq. Try again.")
                        time.sleep(1)
                if player.location.reward:
                    print(r"""
                                    ,-'     `._
                                 ,'           `.        ,-.
                               ,'               \       ),.\
                     ,.       /                  \     /(  \;
                    /'\_     ,o.        ,ooooo.   \  ,'  `-')
                    )) )`. d8P'Y8.    ,8P'''''Y8.  `'  .--''
                   (`-'   `Y'  `Y8    dP       `'     /
                    `----.(   __ `    ,' ,---.       (
                           ),--.`.   (  ;,---.        )
                          / \O_,' )   \  \O_,'        |
                         ;  `-- ,'       `---'        |
                         |    -'         `.           |
                        _;    ,            )          :
                     _.'|     `.:._   ,.::' `..       |
                  --'   |   .'     '''         `      |`.
                        |  :;      :   :     _.       |`.`.-'--.
                        |  ' .     :   :__.,'|/       |  \
                        `     \--.__.-'|_|_|-/        /   )
                         \     \_   `--^"__,'        ,    |
                         ;  `    `--^---'          ,'     |
                          \  `                    /      /
                           \   `    _ _          /
                            \           `       /
                             \           '    ,'
                              `.       ,   _,'
                                `-.___.---'
                    You see a light from up above. It is God.
                    "My child, in order to ascend to greatness you must make use
                    of newfound powers to rise above all obstacles.
                    Become one with the universe, and the universe will become
                    one with you."

                    You eat the mangled body of your fallen opponent.
                    Learned {}!
                    """.format(player.location.reward.name))
                    print("""
                    {0} - {1}
                    """.format(player.location.reward.name,player.location.reward.message))
                    player.moves.append(player.location.reward)
                    while True:
                        wait = input("Type 'Yes' when you are ready. ")
                        if wait == "Yes":
                            break
                if player.location.next:
                    player.location = player.location.next
                    print("""
                    You went into the next room...

                    """)
                elif player.location is x131:
                    pass
                else:
                    while True:
                        next = input("There are two rooms, where do you want to go? Left or Right? ")
                        if next == "Left":
                            player.location = player.location.left
                            break
                        elif next == "Right":
                            player.location = player.location.right
                            break
                        else:
                            print("""
                            Dafuq pick somewhere to go.
                            """)
                time.sleep(2)
    if player.health <= 0:
        print("""
        You died lmao.
        """)
        raise SystemExit
