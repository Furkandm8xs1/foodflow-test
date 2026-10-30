# FoodFlow – Iteration 1 (Console Application)

CSE3063 term project. A customer can register, browse and filter restaurants, view menus, fill a cart and place a cash-on-delivery order from a console application. Data comes from `data/restaurants.json`; nothing is saved between runs.

- Java 21, Maven, Gson, JUnit 5
- Packages: `foodflow` (`App`), `foodflow.domain`, `foodflow.application`, `foodflow.infrastructure`, `foodflow.ui`

## Run with Docker (recommended, nothing else to install)

```bash
docker compose build                 # compiles, runs all tests, builds the image
docker compose run --rm foodflow     # starts the interactive console application
```

Run only the tests:

```bash
docker compose --profile test run --rm test
```

The `data/` folder is mounted into the container, so you can edit `data/restaurants.json` and restart without rebuilding. Use another data file with `-e FOODFLOW_DATA=/app/data/other.json`.

## Run without Docker (needs JDK 21 and Maven)

```bash
mvn -q test                          # tests
mvn -q package                       # builds target/foodflow.jar
java -Dfile.encoding=UTF-8 -jar target/foodflow.jar [path/to/restaurants.json]
```

The data file is taken from the first argument, else the `FOODFLOW_DATA` environment variable, else `data/restaurants.json`.

## Commands

| Command | Meaning | Feature |
|---|---|---|
| `register` | create a customer (asks name, e-mail, district, street) | F1 |
| `login <e-mail>` | continue as an existing customer | F1 |
| `restaurants [--cuisine X] [--district Y]` | list / filter restaurants | F2, F3 |
| `menu <restaurantId>` | menu grouped by category, e.g. `menu R01` | F4 |
| `add <itemId> <quantity>` | add to cart, e.g. `add R01-01 2` | F5 |
| `set <itemId> <quantity>` | change a quantity | F5 |
| `remove <itemId>` | remove an item | F5 |
| `cart` / `cart clear` | show / empty the cart | F5 |
| `order` | place the order (cash on delivery), prints a receipt | F6 |
| `orders` | orders of the current customer in this session | F7 |
| `help`, `quit` | | |

## Demo script (replay with the scripted input)

```bash
docker compose run --rm -T -e FOODFLOW_ECHO=true foodflow < docs/demo-input.txt
```

`FOODFLOW_ECHO=true` prints the piped commands so the output reads like a session. A recorded run is in `docs/sample-run.txt`.

## Project layout

```
data/restaurants.json      6 restaurants, 35 menu items (UTF-8)
src/main/java/foodflow/
  App.java                 wires the objects, starts the UI
  domain/                  rules BR-1..BR-8, no input/output
  application/             OrderingService: one method per system operation
  infrastructure/          JsonRestaurantLoader: the only class that knows JSON
  ui/                      ConsoleUI + ConsoleFormatter: no business rules
src/test/java/...          JUnit tests of rules, loader and UI error handling
docs/                      vision, glossary, use cases, FURPS+, domain model,
                           SSD and contracts, design decisions, iteration plan
```

## Documents (deliverables)

| ID | File |
|---|---|
| D1 | `docs/vision.md` |
| D2 | `docs/glossary.md` |
| D3 | `docs/use-cases.md` |
| D4 | `docs/supplementary-spec.md` |
| D5 | `docs/domain-model.md` |
| D6 | `docs/ssd-and-contracts.md` |
| D7 | this project, `docs/sample-run.txt` |
| D8 | design rationale: `docs/design-decisions.md` (report itself, contribution table and AI usage log are written by the team) |
| D9 | `docs/iteration-plan.md` |

## Team Workflow & GitHub Setup (English)

| Guide | File | Description |
|---|---|---|
| **25-Commit & Roles Plan** | `docs/team-commit-and-branch-plan.md` | 6 specialized roles, feature branches, and chronological 25-commit breakdown |
| **Branch Protection & Review** | `docs/github-branch-protection-guide.md` | Step-by-step setup for GitHub Actions CI check + 1 peer approval before merge |

## Ekip Başlangıç Rehberleri (Guides)

| Rehber | Dosya | Açıklama |
|---|---|---|
| **Git & GitHub** | `docs/guides/git-github-guide.md` | Dallar (branch), commit, pull request (PR) ve çakışma çözümü |
| **Docker & Compose** | `docs/guides/docker-guide.md` | Derleme, çalıştırma, test koşturma ve Docker komutları |

