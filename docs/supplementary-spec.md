# Supplementary Specification – FURPS+ (D4)

Each requirement is **checkable**: it says how to verify it.

| Category | ID | Requirement | How to check |
|---|---|---|---|
| **Functionality** | FN-1 | Every F1–F6 feature is reachable from the console; F7 as well. | Run the demo script in the README; compare with `docs/sample-run.txt`. |
| Functionality | FN-2 | Rules BR-1…BR-8 are enforced in the domain, not in the UI. | `mvn test`: domain tests exist per rule; `ConsoleUI` has no `if` on prices, quantities or e-mail. |
| Functionality | FN-3 | Restaurant data comes from `data/restaurants.json`, not from Java code. | Change a price in the JSON, restart: the menu shows the new price. |
| **Usability** | US-1 | Every error is a one-line message that says what to do next; no stack traces. | Enter `add`, `add R01-01 abc`, `add R01-01 99`, `foo`: no exception text, program continues (`ConsoleUITest`). |
| Usability | US-2 | `help` lists all commands. | Type `help`. |
| Usability | US-3 | Filters ignore case and Turkish diacritics ("kadikoy" finds Kadıköy). | `restaurants --district kadikoy` lists Kadıköy restaurants. |
| **Reliability** | RE-1 | Invalid input never terminates the program (BR-9). | `ConsoleUITest.invalidInputNeverEndsTheProgram_BR9`. |
| Reliability | RE-2 | A rejected operation leaves the cart unchanged. | `CartTest` (BR-2, BR-1 tests). |
| Reliability | RE-3 | A missing or invalid data file stops start-up with a clear message and exit code 1. | Run with `FOODFLOW_DATA=missing.json`. |
| **Performance** | PE-1 | Start-up with the 6-restaurant file takes under 2 seconds; any command answers instantly (under 100 ms). | Time `docker compose run --rm foodflow` with piped `quit`. |
| **Supportability** | SU-1 | Layers `domain`, `application`, `infrastructure`, `ui`; domain has no `System.out`/`System.in` and no JSON imports. | `grep -r "System\.\(out\|in\)\|gson" src/main/java/foodflow/domain` finds nothing. |
| Supportability | SU-2 | Builds and tests with a single command in Docker. | `docker compose build` succeeds (tests run inside). |
| Supportability | SU-3 | At least 10 JUnit tests of domain rules. | `mvn test` report. |
| **+ Design constraint** | DC-1 | Java 21, Maven, Gson; no database. | `pom.xml`. |
| + Implementation | IM-1 | Source and data files are UTF-8; Turkish characters display correctly. | Menu of R01 shows "Kebapçı Mehmet", "Künefe". |
| + Interface | IF-1 | Console only; no GUI in I1. | - |
| + Legal / business | LG-1 | All amounts in TL with two decimals; payment is cash on delivery only. | Receipt shows "Payment: cash on delivery". |

