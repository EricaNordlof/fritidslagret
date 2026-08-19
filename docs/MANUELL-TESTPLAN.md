# Manuell testplan

Starta med:

```bash
mvn clean package
java -jar target/fritidslagret.jar --demo
```

## Snabb kontroll

1. Visa inventariet och verifiera sex objekt med olika lånetider och status.
2. Visa medlemmarna och verifiera att `MED-003` är inaktiv.
3. Visa aktiva lån och notera låne-ID:n.
4. Visa väntelistan för `TALT-001`; `MED-004` ska vara först.
5. Visa statistik; demodatan ska ha fyra medlemmar och två aktiva lån.
6. Visa verksamhetsrapporten.

## Normalfall från tom start

1. Starta utan `--demo`.
2. Registrera medlem `MED-100`.
3. Registrera cykel `CYK-100`.
4. Låna cykeln till medlemmen.
5. Kontrollera att cykeln är utlånad och att förfallodatum är sju dagar senare.
6. Återlämna med låne-ID och `MED-100`.
7. Kontrollera att lånet är avslutat och cykeln tillgänglig.

## Fel och gränsfall

- Försök registrera samma medlems-ID två gånger.
- Skriv text i huvudmenyn och därefter ett giltigt val.
- Försök låna samma utrustning till två medlemmar.
- Ge en medlem tre aktiva lån och försök skapa ett fjärde.
- Försök returnera ett lån med fel medlems-ID.
- Köa två personer, gör den första inaktiv och återlämna utrustningen.
- Kontrollera att den andra personen får automatiskt lån.

Automatiska tester gör dessa kontroller repeterbart. Den manuella planen visar
dessutom att hela konsolflödet och återkopplingen fungerar för en användare.
