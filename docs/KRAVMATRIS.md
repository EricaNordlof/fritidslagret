# Kravmatris

| Krav | Implementation | Verifiering |
|---|---|---|
| Konsolapplikation | `Main`, `ConsoleApplication`, `ScannerConsoleIO` | Manuell testplan och `ScannerConsoleIOTest` |
| Flera typer av utrustning | `Bicycle`, `Tent`, `SkiEquipment` | `EquipmentTest` |
| Unikt utrustnings-ID | `InventoryService.register()` | `InventoryAndMemberServiceTest` |
| Namn och tillgänglighetsstatus | `Equipment` | `EquipmentTest`, `LoanServiceTest` |
| Olika maximala lånetider | `LoanTerms` och polymorfa implementationer | `EquipmentTest` |
| Medlemsregister | `Member`, `MemberService` | Service- och lånetester |
| Unikt medlems-ID | `MemberService.register()` | `InventoryAndMemberServiceTest` |
| Medlemsstatus | `MemberStatus`, `changeStatus()` | `LoanServiceTest` |
| Låntagare och utrustning | `Loan` | `LoanServiceTest` |
| Låne- och förfallodatum | `LoanPeriod`, Java Time API | `LoanServiceTest` |
| Aktivt eller avslutat lån | `LoanStatus` | `LoanServiceTest` |
| Endast aktivt lån kan avslutas | `LoanService.returnEquipment()` och `Loan.close()` | `LoanServiceTest` |
| Endast låntagaren kan avsluta | `isBorrowedBy()` | `onlyBorrowerCanCloseActiveLoan` |
| Hindra dubbelutlåning | `EquipmentUnavailableException` | `alreadyBorrowedEquipmentCannotReceiveASecondLoan` |
| Högst tre aktiva lån | `LoanService.MAX_ACTIVE_LOANS` | `thirdLoanIsAllowedButFourthIsRejectedWithoutChangingEquipment` |
| Rapportera sen retur | `ReturnResult.returnedLate` | `returnOnDueDateIsOnTimeAndNextDayIsLate` |
| FIFO-väntelista | `WaitingList` med `ArrayDeque` | `waitlistIsFifoAndCreatesAutomaticLoanOnReturn` |
| Automatisk utlåning från kö | `createLoanForFirstEligibleWaitingMember()` | `LoanServiceTest` |
| Obehörig kömedlem tas bort | Behörighetskontroll vid retur | två tester för inaktiv/fullbelagd medlem |
| Statistik | `StatisticsService` | `StatisticsServiceTest` |
| Rapport | `OperationalReportService` | `OperationalReportServiceTest` |
| Kontrollerad felaktig input | `ScannerConsoleIO` | `ScannerConsoleIOTest` |
| Abstrakt klass | `Equipment` | Kompilering och `EquipmentTest` |
| Interface | `LoanTerms`, repositories, `ReportWriter`, `ConsoleIO`, `LoanIdGenerator` | Kompilering och tester |
| Arv och polymorfism | Utrustningshierarkin | `EquipmentTest` |
| Komposition | `Loan` → `LoanPeriod`, `WaitingList` → kö | Domän- och tjänstetester |
| SOLID | Lagerindelning och constructor injection | README och `DESIGN.md` |
| Eget exception | `FritidslagretException` med subklasser | flera `assertThrows`-tester |
| Collections | Map, deque, set och list | README och implementation |
| Mockbara externa beroenden | `Clock`, repository-interface och `ReportWriter` injiceras | Kontrollerad klocka och test-dubbel i rapporttest |
| Maven | `pom.xml` | `mvn clean test`, `mvn clean package` |
| Körbar JAR | `maven-jar-plugin` med `Main-Class` | `java -jar target/fritidslagret.jar` |
| README | `README.md` | Manuell granskning |
| AI-reflektion | `docs/AI-ANVANDNING.md` | Fylls i och granskas av båda studenterna |
| Git-historik och brancher | Verkligt arbete i grupprepository | Kontrolleras i GitHub före inlämning |

## Definition av viktiga gränsfall

- Det tredje aktiva lånet är tillåtet; det fjärde nekas.
- Återlämning på förfallodagen är i tid.
- Återlämning från dagen efter förfallodatum är sen.
- En kömedlems behörighet kontrolleras när utrustningen återlämnas, inte bara
  när köplatsen skapas.
- Om alla i kön är obehöriga förblir utrustningen tillgänglig.
