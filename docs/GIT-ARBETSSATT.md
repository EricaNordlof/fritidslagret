# Git- och pararbetssätt

Den här filen är en plan och mall. Den ersätter inte verklig Git-historik.

## Första konfigurationen

1. Skapa ett privat GitHub-repository på ett `@chasacademy.se`-konto eller i en
   gemensam organisation.
2. Bjud in den andra studenten med full åtkomst.
3. Bjud in examinatorn `david.cederlund@chasacademy.se`.
4. Skydda gärna `main` så att förändringar går via pull request.
5. Klona repositoryt på båda studenternas datorer.

## Rekommenderade brancher

- `feature/inventory`
- `feature/members`
- `feature/loans`
- `feature/waitlist`
- `feature/reports`
- `test/loan-service`
- `docs/readme`

Brancherna ska fördelas efter det arbete ni faktiskt gör. Skapa inte tomma
brancher bara för syns skull.

## Exempel på meningsfulla commits

```text
feat(inventory): add equipment hierarchy and loan terms
feat(members): add member registration and status changes
feat(loans): enforce maximum of three active loans
feat(waitlist): process queued members in FIFO order
test(loans): cover overdue returns and loan limit
docs: add requirement traceability and AI review
```

Undvik commitmeddelanden som `fix`, `1`, `klart` eller `lite ändringar`. Git ska
berätta en historia, inte ge examinatorn arkeologpraktik.

## Arbetssätt per funktion

```bash
git switch main
git pull
git switch -c feature/exempel

# arbeta, testa och gör flera begripliga commits
git add <berörda-filer>
git commit -m "feat(scope): describe actual change"
git push -u origin feature/exempel
```

Öppna en pull request, låt partnern granska och merga. Radera inte historiken.
Undvik squash om examinatorn behöver se bådas individuella commits.

## Verklig arbetsfördelning

Fyll i detta löpande:

| Student | Branch/PR | Faktiskt ansvar | Exempel på commits |
|---|---|---|---|
| Student 1 |  |  |  |
| Student 2 |  |  |  |

Parprogrammering kan dokumenteras i PR-beskrivningen. `Co-authored-by` ska bara
användas när båda faktiskt arbetade med committen.

## Kontroll före inlämning

- [ ] Repositoryt är privat.
- [ ] Båda studenterna har åtkomst och synliga, verkliga bidrag.
- [ ] Brancher och merges finns kvar.
- [ ] `main` bygger utan lokala specialinställningar.
- [ ] `mvn clean test` är grönt.
- [ ] `mvn clean package` skapar körbar JAR.
- [ ] Examinatorn har åtkomst.
- [ ] README och AI-reflektion är uppdaterade.
- [ ] Båda lämnar samma repositorylänk i Canvas.
