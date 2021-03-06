# Vespucci Introduktion

Vespucci är en fullfjädrad OpenStreetMap redigerare som stödjer de flesta redigeringar som stationära program tillhandahåller. Det har testats framgångsrikt på googles Android 2,3 till 6,0 och olika AOSP baserade varianter. Ett varningens ord: medan mobila enheter kapacitet har kommit ikapp sina stationära motsvarigheter, särskilt äldre androidenheter har mycket begränsat minne tillgängligt och tenderar att vara ganska långsam. Du bör ta det i beaktande när du använder Vespucci och behålla, till exempel storleken på de områden som du redigerar till en rimlig storlek. 

## Förstagångs användning

On startup Vespucci shows you the "Download other location"/"Load Area" dialog. If you have coordinates displayed and want to download immediately, you can select the appropriate option and set the radius around the location that you want to download. Do not select a large area on slow devices. 

Alternativt kan du stänga dialogen genom att trycka på "Gå till karta" knappen och panorera och zooma in en plats som du vill redigera och hämta data därefter (se nedan: "Redigering med Vespucci").

## Redigering med Vespucci

Beroende på skärmstorlek och ålder på enheten redigering åtgärder kan antingen vara tillgängliga direkt via ikoner i det översta fältet, via en rullgardinsmeny till höger om den översta raden, från den nedre listen (i förekommande fall) eller via menyknappen.

### Nedladdning av OSM data

Select either the transfer icon ![](../images/menu_transfer.png)  or the "Transfer" menu item. This will display seven options:

977/5000
* **Hämta nuvarande vy** - ladda ner det område som syns på skärmen och ersätt all befintlig data *(kräver nätverksanslutning)* ↩
* **Lägg till nuvarande vy till nedladdning** - ladda ner det område som syns på skärmen och slå samman den med befintlig data *(kräver nätverksanslutning)* ↩
* **Ladda ner annan plats** - visar en form som gör det möjligt att ange koordinater, söka efter en plats eller använda den aktuella positionen och sedan ladda ner ett område runt den platsen *(kräver nätverksanslutning)* ↩
* **Ladda upp data till OSM server** - ladda upp ändringar till OpenStreetMap *(kräver autentisering)* *(kräver nätverksanslutning)* ↩
* **Automatisk nedladdning** - ladda ner ett område runt den aktuella platsen automatiskt *(kräver nätverksanslutning)* * (kräver GPS)* ↩
* **Fil ...** - Spara och ladda OSM data till/från på enheten files.↩
* **Anteckningar/buggar...** - Ladda ner (automatiskt och manuellt) OSM anteckningar och "buggar" från QA verktyg (för närvarande Osmose) *(kräver nätverksanslutning)*

The easiest way to download data to the device is to zoom and pan to the location you want to edit and then to select "Download current view". You can zoom by using gestures, the zoom buttons or the volume control buttons on the telephone.  Vespucci should then download data for the current view. No authentication is required for downloading data to your device.

### Redigering

För att undvika oavsiktliga ändringar startar Vespucci i "låst" läge, ett läge som endast tillåter zoomning och flyttning av kartan. Tryck på ![Låst](../images/locked.png) ikonen för att låsa upp skärmen. Ett långt tryck på hänglåset kommer att aktivera "Tag redigering bara"-läge som inte tillåter dig att skapa nya objekt eller redigera geometrin av objekt, det här läget indikeras av en något annorlunda vit låsikon.

Som standard, valbara noder och vägar har en orange området kring dem som indikerar ungefär där du måste trycka på för att välja ett objekt. Om du försöker att välja ett objekt och Vespucci bestämmer att valet kan betyda flera objekt kommer ett menyval att presentera. Markerade objekten markeras i gult.

Det är en bra strategi att zooma in om du försöker redigera ett område med hög täthet.

Vespucci har ett bra "ångra/gör om" system så var inte rädd för att experimentera på din enhet, men vänligen ladda inte upp och spara ren testdata.

#### Välja / Välja bort

Touch an object to select and highlight it, a second touch on the same object opens the tag editor on the element. Touching the screen in an empty region will de-select. If you have selected an object and you need to select something else, simply touch the object in question, there is no need to de-select first. A double tap on an object will start [Multiselect mode](../en/Multiselect.md).

#### Adding a new Node/Point or Way

Långt tryck där du vill att noden ska vara eller väg ska börja. Du kommer att se en svart "hårkors" symbol. Vidrör skapar en ny nod på samma plats igen, vidrör en plats utanför berörings toleranszonen kommer att lägga ett vägsegment från den ursprungliga positionen till den aktuella positionen. 

Simply touch the screen where you want to add further nodes of the way. To finish, touch the final node twice. If the initial and  end nodes are located on a way, they will be inserted into the way automatically.

#### Flytta en node eller väg

Objects can be dragged/moved only when they are selected. If you select the large drag area in the preferences, you get a large area around the selected node that makes it easier to position the object. 

#### Förbättra väg-geometri

If you zoom in far enough you will see a small "x" in the middle of way segments that are long enough. Dragging the "x" will create a node in the way at that location. Note: to avoid accidentally creating nodes, the touch tolerance for this operation is fairly small.

#### Klipp ut, kopiera och klistra in

You can copy or cut selected nodes and ways, and then paste once or multiple times to a new location. Cutting will retain the osm id and version. To paste long press the location you want to paste to (you will see a cross hair marking the location). Then select "Paste" from the menu.

#### Effektivt lägga till adresser

Vespucci has an "add address tags" function that tries to make surveying addresses more efficient. It can be selected 

* after a long press: Vespucci will add a node at the location and make a best guess at the house number and add address tags that you have been lately been using. If the node is on a building outline it will automatically add a "entrance=yes" tag to the node. The tag editor will open for the object in question and let you make any further changes.
* in the node/way selected modes: Vespucci will add address tags as above and start the tag editor.
* in the tag editor.

House number prediction typically requires at least two house numbers on each side of the road to be entered to work, the more numbers present in the data the better.

Fundera på att anvönda det med "Automatisk-nedladdning" läget.  

#### Lägger till Sväng restriktioner

Vespucci has a fast way to add turn restrictions. Note: if you need to split a way for the restriction you need to do this before starting.

* select a way with a highway tag (turn restrictions can only be added to highways, if you need to do this for other ways, please use the generic "create relation" mode, if there are no possible "via" elements the menu item will also not display)
* select "Add restriction" from the menu
* select the "via" node or way (all possible "via" elements will have the selectable element highlighting)
* select the "to" way (it is possible to double back and set the "to" element to the "from" element, Vespucci will assume that you are adding an no_u_turn restriction)
* set the restriction type in the tag menu

### Vespucci i "låst" läge

When the red lock is displayed all non-editing actions are available. Additionally a long press on or near to an object will display the detail information screen if it is an OSM object.

### Spara dina ändringar

*(kräver nätverksanslutning)*

Välj samma knapp eller menyalternativ du gjorde för nedladdning och välj nu "Ladda upp data till OSM server".

Vespucci supports OAuth authorization and the classical username and password method. OAuth is preferable since it avoids sending passwords in the clear.

New Vespucci installs will have OAuth enabled by default. On your first attempt to upload modified data, a page from the OSM website loads. After you have logged on (over an encrypted connection) you will be asked to authorize Vespucci to edit using your account. If you want to or need to authorize the OAuth access to your account before editing there is a corresponding item in the "Tools" menu.

If you want to save your work and do not have Internet access, you can save to a JOSM compatible .osm file and either upload later with Vespucci or with JOSM. 

#### Lös konfliktervid uppladdning

Vespucci has a simple conflict resolver. However if you suspect that there are major issues with your edits, export your changes to a .osc file ("Export" menu item in the "Transfer" menu) and fix and upload them with JOSM. See the detailed help on [conflict resolution](../en/Conflict resolution.md).  

## Använda GPS

You can use Vespucci to create a GPX track and display it on your device. Further you can display the current GPS position (set "Show location" in the GPS menu) and/or have the screen center around and follow the position (set "Follow GPS Position" in the GPS menu). 

If you have the later set, moving the screen manually or editing will cause the "follow GPS" mode to be disabled and the blue GPS arrow will change from an outline to a filled arrow. To quickly return to the "follow" mode, simply touch the arrow or re-check the option from the menu.

## Anteckningar och buggar

Vespucci supports downloading, commenting and closing of OSM Notes (formerly OSM Bugs) and the equivalent functionality for "Bugs" produced by the [OSMOSE quality assurance tool](http://osmose.openstreetmap.fr/en/map/). Both have to either be downloaded explicitly or you can use the auto download facility to access the items in your immediate area. Once edited or closed, you can either upload the bug or Note immediately or upload all at once.

On the map the Notes and bugs are represented by a small bug icon ![](../images/bug_open.png), green ones are closed/resolved, blue ones have been created or edited by you, and yellow indicates that it is still active and hasn't been changed. 

The OSMOSE bug display will provide a link to the affected object in blue, touching the link will select the object, center the screen on it and down load the area beforehand if necessary. 

## Anpassa Vespucci

### Inställningar som du kanske vill ändra

* Background layer
* Overlay layer. Adding an overlay may cause issues with older devices and such with limited memory. Default: none.
* Notes/Bugs display. Open Notes and bugs will be displayed as a yellow bug icon, closed ones the same in green. Default: on.
* Photo layer. Displays georeferenced photographs as red camera icons, if direction information is available the icon will be rotated. Default: off.
* Node icons. Default: on.
* Keep screen on. Default: off.
* Large node drag area. Moving nodes on a device with touch input is problematic since your fingers will obscure the current position on the display. Turning this on will provide a large area which can be used for off-centre dragging (selection and other operations still use the normal touch tolerance area). Default: off.

#### Avancerade inställningar

* Enable split action bar. On recent phones the action bar will be split in a top and bottom part, with the bottom bar containing the buttons. This typically allows more buttons to be displayed, however does use more of the screen. Turning this off will move the buttons to the top bar. note: you need to restart Vespucci for the change to take effect.
* Always show context menu. When turned on every selection process will show the context menu, turned off the menu is displayed only when no unambiguous selection can be determined. Default: off (used to be on).
* Enable light theme. On modern devices this is turned on by default. While you can enable it for older Android versions the style is likely to be inconsistent.
* Show statistics. Will show some statistics for debugging, not really useful. Default: off (used to be on).  

## Rapportera problem

If Vespucci crashes, or it detects an inconsistent state, you will be asked to send in the crash dump. Please do so if that happens, but please only once per specific situation. If you want to give further input or open an issue for a feature request or similar, please do so here: [Vespucci issue tracker](https://github.com/MarcusWolschon/osmeditor4android/issues). If you want to discuss something related to Vespucci, you can either start a discussion on the [Vespucci google group](https://groups.google.com/forum/#!forum/osmeditor4android) or on the [OpenStreetMap Android forum](http://forum.openstreetmap.org/viewforum.php?id=56)


