# Fritidslagret

Fritidslagret är en konsolapplikation för en ideell förening som lånar ut
fritidsutrustning gratis. Systemet hanterar inventarie, medlemmar, lån,
väntelistor, statistik och en verksamhetsrapport.

Projektet är utvecklat för **Java 25** och byggs med **Maven**. All data lagras
i minnet och återställs när programmet stängs.

## Funktioner

- Registrera och söka cyklar, tält och skidutrustning.
- Registrera medlemmar och ändra medlemsstatus.
- Skapa lån med automatiskt beräknat förfallodatum.
- Hindra lån av redan utlånad utrustning.
- Hindra fler än tre aktiva lån per medlem.
- Avsluta lån endast när rätt medlem återlämnar.
- Rapportera återlämningar efter förfallodatum.
- Hantera FIFO-väntelistor utan dubbla köplatser.
- Skapa automatiskt lån åt första behöriga medlemmen i kön.
- Hoppa över kömedlemmar som inte längre är behöriga.
- Visa verksamhetsstatistik och en operativ rapport.
- Hantera felaktig konsolinmatning utan att programmet kraschar.

## Förutsättningar

- JDK 25
- Maven 3.9 eller senare

Kontrollera installationen:

```bash
java -version
mvn -version
```

## Bygga, testa och starta

Kör alla tester:

```bash
mvn clean test
```

Bygg en körbar JAR-fil:

```bash
mvn clean package
```

Starta med tomma register:

```bash
java -jar target/fritidslagret.jar
```

Starta med demodata:

```bash
java -jar target/fritidslagret.jar --demo
```

I IntelliJ kan `se.chasacademy.fritidslagret.app.Main` köras direkt. Säkerställ
att Project SDK och Maven Runner använder Java 25.

## Menyer

Huvudmenyn innehåller:

1. Inventarie
2. Medlemmar
3. Utlåning och återlämning
4. Väntelistor
5. Statistik
6. Visa verksamhetsrapport

Programmet använder endast `Scanner.nextLine()`. Tom input, text där ett tal
förväntas och menyval utanför tillåtet intervall fångas och ger en ny fråga.

## Paketstruktur

| Paket | Ansvar |
|---|---|
| `app` | Startpunkt, beroendekoppling och valfri demodata |
| `domain` | Utrustning, medlem, lån, låneperiod och väntelista |
| `service` | Affärsregler och användningsfall |
| `repository` | Abstraktioner och in-memory-lagring |
| `report` | Rapportgenerering och utgångsport |
| `ui` | Konsolmenyer och kontrollerad inmatning |
| `exception` | Egna domän- och regelfel |
| `util` | Utbytbar generering av låne-ID |

## Objektorienterade principer

| Princip | Exempel i lösningen |
|---|---|
| Inkapsling | Fält är privata. Status ändras via `checkOut()`, `checkIn()`, `close()` och `changeStatus()`. |
| Arv | `Bicycle`, `Tent` och `SkiEquipment` ärver från den abstrakta klassen `Equipment`. |
| Polymorfism | `LoanService` ber ett `Equipment` om lånetiden utan att känna till dess konkreta typ. |
| Interface | `LoanTerms`, repository-interface, `ReportWriter`, `ConsoleIO` och `LoanIdGenerator`. |
| Abstraktion | `Equipment` samlar gemensamma fält och regler men lämnar typ och lånetid till subklasser. |
| Komposition | Ett `Loan` äger sitt `LoanPeriod`; en `WaitingList` äger sin kö och dubblettkontroll. |

Maximal lånetid i denna implementation:

| Typ | Maximal lånetid |
|---|---:|
| Cykel | 7 dagar |
| Tält | 5 dagar |
| Skidutrustning | 3 dagar |

En ny utrustningstyp kan läggas till som en ny subklass utan att ändra
låneberäkningen.

## SOLID-beslut

Det tydligaste beslutet är **Dependency Inversion Principle**. `LoanService`
beror på repository-interface, `Clock` och `LoanIdGenerator`, inte på konkreta
minneslager eller systemklockan. `OperationalReportService` skriver via
`ReportWriter` i stället för direkt till konsolen.

Det ger två vinster:

- affärslogiken kan testas deterministiskt med en kontrollerad klocka;
- lagring och rapportutmatning kan ersättas eller mockas utan att tjänsterna
  skrivs om.

**Single Responsibility Principle** syns också i uppdelningen: UI läser input,
tjänster hanterar regler, repositories lagrar data och domänobjekt bevakar sitt
eget giltiga tillstånd.

## Datastrukturer

| Struktur | Användning | Motivering |
|---|---|---|
| `LinkedHashMap` | Utrustning, medlemmar, lån och väntelistor | Snabb sökning via unikt ID och stabil ordning vid utskrift. |
| `ArrayDeque` | Själva väntelistan | Effektiv FIFO med `offer()` och `poll()`. |
| `HashSet` | Registrerade kömedlemmar | Stoppar dubbla köplatser med snabb kontroll. |
| `List` | Skrivskyddade resultat och ögonblicksbilder | Tjänster lämnar inte ut sina interna samlingar. |

## Datum och tid

`LocalDate` används för lånedatum, förfallodatum och återlämningsdatum.
Förfallodatum beräknas av utrustningens lånevillkor. Tjänsterna får en `Clock`
via konstruktorn, så testerna behöver inte bero på datorns verkliga datum.

Förfallodagen är inkluderad: retur på förfallodagen är i tid, dagen efter är
sen.

## Felhantering

Alla förväntade verksamhetsfel ärver från det egna undantaget
`FritidslagretException`. Exempel:

- `DuplicateIdException`
- `EquipmentUnavailableException`
- `LoanLimitExceededException`
- `MemberNotEligibleException`
- `UnauthorizedReturnException`
- `AlreadyQueuedException`

UI-lagret fångar dessa fel och visar begripliga meddelanden i stället för en
stack trace.

## Automatiserade tester

Testsviten täcker bland annat:

- normal låneregistrering och rätt förfallodatum;
- polymorfa lånetider;
- tredje lån tillåts och fjärde nekas;
- redan utlånad utrustning;
- inaktiv medlem;
- retur av fel medlem;
- retur på respektive efter förfallodagen;
- FIFO, automatisk utlåning och borttagning av obehöriga kömedlemmar;
- väntande medlem som redan har tre aktiva lån;
- dubbletter i väntelistan;
- statistik och mest utlånade utrustning;
- rapport genom en utbytbar utgångsport;
- ogiltig konsolinmatning.

## Avgränsningar

- Data lagras endast i minnet; databas eller filpersistens ingår inte i kraven.
- Systemet har ingen inloggning eller behörighetsmodell för administratörer.
- Utrustning och medlemmar tas inte bort permanent eftersom aktiva lån och
  historik då behöver särskilda arkiveringsregler. Medlemsstatus kan ändras.

## Projektdokumentation

- [Kravmatris](docs/KRAVMATRIS.md)
- [Design och klassöversikt](docs/DESIGN.md)
- [AI-användning](docs/AI-ANVANDNING.md)
- [Git- och pararbetssätt](docs/GIT-ARBETSSATT.md)
- [Manuell testplan](docs/MANUELL-TESTPLAN.md)

## Viktigt inför inlämning

Det färdiga grupprepositoryt ska vara privat. Båda studenterna ska använda sina
egna GitHub-konton, arbeta i riktiga brancher och göra regelbundna commits som
visar deras faktiska bidrag. Examinatorn `david.cederlund@chasacademy.se` ska
ges åtkomst. Båda studenterna lämnar samma repositorylänk i Canvas.

Mallarna i `docs/` ska fyllas i med verkliga namn, brancher, pull requests och
reflektioner före inlämning. Git-historik är en del av examinationen och ska
inte konstrueras i efterhand.
