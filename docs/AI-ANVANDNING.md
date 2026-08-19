# Dokumentation av AI-användning

> Fyll i studentnamn, datum och eventuell ytterligare egen användning före
> inlämning. Båda studenterna ska kunna förklara den slutliga koden.

## Granskat och förbättrat AI-förslag

**Datum:** 2026-08-19  
**Berörda delar:** `WaitingList`, `WaitlistService`, `LoanService` och tester

### Ursprungligt förslag

Ett första AI-förslag var att vid återlämning ta första personen ur FIFO-kön
och direkt skapa ett nytt lån.

### Problem som upptäcktes

Förslaget kontrollerade inte på nytt om kömedlemmen fortfarande var aktiv eller
redan hade tre aktiva lån. Det uppfyllde därför inte kravet att en medlem som
inte får låna ska tas bort och att nästa person i kön ska hanteras.

### Förbättring

Flödet ändrades så att varje köplats tas bort med `poll()` och därefter
kontrolleras mot aktuell medlemsstatus och aktuellt antal aktiva lån. Obehöriga
medlemmar sparas i `ReturnResult.skippedMemberIds` för tydlig återkoppling.
Första behöriga medlem får lånet; om ingen är behörig förblir utrustningen
tillgänglig.

### Verifiering

Förbättringen verifieras av testerna:

- `ineligibleFirstMemberIsRemovedAndNextMemberGetsLoan`
- `waitingMemberWithThreeActiveLoansIsSkipped`
- `waitlistIsFifoAndCreatesAutomaticLoanOnReturn`

## Ytterligare kritisk granskning

Konsolinmatningen använder konsekvent `Scanner.nextLine()` och konverterar
själv menyval till heltal. Det undviker det vanliga felet där en kombination av
`nextInt()` och `nextLine()` lämnar en radbrytning och hoppar över nästa fråga.
`ScannerConsoleIOTest` visar att text och val utanför intervallet hanteras utan
krasch.

## Studenternas egen reflektion

Fyll i före inlämning:

- Vilka AI-verktyg användes?
- Vilka delar skrev eller ändrade respektive student?
- Vilken kod var svårast att förstå och hur verifierades den?
- Vilket AI-förslag valdes bort och varför?
- Hur säkerställdes att lösningen faktiskt matchar kraven?
