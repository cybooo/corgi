# CorgiBot Contribuiton guide (CZ)
Jedná se o návod, jak co dělat a nedělet, pokuď chceš přispět k tomuto projektu. 

### Co dělat a čemu se vyvarovat
 - Nevytvářej PR s úpravou jednoho řádku.
 - Vždy, pokuď to je možné otestuj zda vše funguje.
 - Neměň kód o kterém víme, že funguje správně.
 - Zkontroluj si, zda PR neobsahuje nějaké soubory navíc př. Eclipse soubory.

### Formátování
Corgiho source je formátováno podle IntelliJ základního formátu, neakceptujeme tedy žádný jiný typ.
 - Minimální mezera mezi hlavičkou třídy a metodami je 1 řádek.

### Branches
Rozpis jak zde fungují branche:
 - `master` - je základní branch, se kterou se updatuje Corgi na hlavní verzi.
 - `develop` - je testovací verze, která obsahuje bugy, opravy a pull requesty.

### Pull Requesty a jejich typy
Před odesláním PR si zkontroluj následující věci:
 - PR je dokončený, obsahuje tedy všechny opravy, změny a jiné úpravy.
 - Vše jsem odzkoušel/a a vše fungovalo.
 
 Dále si zkontroluj, zda tvoje branch odpovídá Corgiho standartům:
  - `feature/` - Nové updaty, změny atd.
  - `fix/` - Opravy bugů, často se za / píše ID issue.
