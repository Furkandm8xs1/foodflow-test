# FoodFlow Iteration 1: Team Workflow, Roles & 25-Commit Plan

This document defines how the FoodFlow project is distributed among **6 team members**, specifying specialized responsibilities, branches, and a chronological breakdown of **25 distinct commits**.

---

## 1. Team Members & Specialized Roles

| Member | Title / Specialization | Core Responsibilities & Ownership |
|---|---|---|
| **Member 1** | **Domain Modeler & Core Entity Engineer** | Foundational domain classes, text/validation utilities, restaurant & menu entities, catalog search/filter. |
| **Member 2** | **Cart & Pricing Logic Specialist** | Shopping cart state management, cart lines, quantity constraints, subtotal/delivery pricing, cart summary read-only views. |
| **Member 3** | **Order Lifecycle & Snapshot Engineer** | Order creation, unique order numbering, status transitions, price snapshotting (`OrderLine`), sepet clearing (`BR-7`). |
| **Member 4** | **Customer Domain & Infrastructure Engineer** | Customer identity, delivery addresses, email validation, session customer registry, and JSON restaurant loading infrastructure. |
| **Member 5** | **Application Service & Testing Architect** | `OrderingService` coordination (SSD operations), comprehensive domain & application JUnit 5 test suites. |
| **Member 6** | **Console UI & DevOps Specialist** | Interactive CLI interface, console formatting, argument parsing, Docker configuration, GitHub Actions CI, and developer documentation. |

---

## 2. Feature Branch Strategy

Work is organized across 6 primary feature branches. Each branch corresponds to an area of ownership, is tested via CI, and merged into `main` through a Pull Request requiring **1 approval**:

1. `feature/project-skeleton-and-devops` (Member 6)
2. `feature/domain-core-entities` (Member 1)
3. `feature/customer-and-infrastructure` (Member 4)
4. `feature/cart-and-pricing-rules` (Member 2)
5. `feature/order-lifecycle-and-snapshots` (Member 3)
6. `feature/app-service-and-console-ui` (Member 5 & Member 6)

---

## 3. Detailed 25-Commit Execution Plan

Below is the chronological sequence of 25 granular commits. Each entry specifies the author, branch, conventional commit message, affected files, and implementation details.

```mermaid
graph TD
    subgraph Phase 1: Foundation
        C01[Commit 01: Maven & Base Project]
        C02[Commit 02: Starter Dataset]
        C03[Commit 03: Docker & CI Setup]
        C04[Commit 04: Project Readme & Vision Doc]
    end

    subgraph Phase 2: Domain & Infrastructure
        C05[Commit 05: Text & Validation Utils]
        C06[Commit 06: MenuItem & Restaurant Core]
        C07[Commit 07: RestaurantCatalog & Search]
        C08[Commit 08: Customer & DeliveryAddress]
        C09[Commit 09: CustomerRegistry & BR-8 Tests]
        C10[Commit 10: JsonRestaurantLoader & Tests]
    end

    subgraph Phase 3: Cart, Order & Snapshot Logic
        C11[Commit 11: CartLine & Quantity Limits]
        C12[Commit 12: Cart State & Single Restaurant Rule]
        C13[Commit 13: CartSummary Read-Only DTO]
        C14[Commit 14: OrderStatus & NumberGenerator]
        C15[Commit 15: OrderLine Price Snapshot]
        C16[Commit 16: Order Placement & Cart Clearing]
        C17[Commit 17: Comprehensive Order Unit Tests]
    end

    subgraph Phase 4: Application Layer & Interface
        C18[Commit 18: OrderingService Facade]
        C19[Commit 19: OrderingService Integration Tests]
        C20[Commit 20: Console Output Formatter]
        C21[Commit 21: CLI Filter Arguments Parser]
        C22[Commit 22: ConsoleUI Implementation]
        C23[Commit 23: Application Main Bootstrap]
        C24[Commit 24: Console UI Interactive Tests]
    end

    subgraph Phase 5: Verification & Deliverables
        C25[Commit 25: Deliverables & Sample Run]
    end

    C01 --> C02 --> C03 --> C04 --> C05 --> C06 --> C07 --> C08 --> C09 --> C10
    C10 --> C11 --> C12 --> C13 --> C14 --> C15 --> C16 --> C17
    C17 --> C18 --> C19 --> C20 --> C21 --> C22 --> C23 --> C24 --> C25
```

---

### Phase 1: Foundation & DevOps Setup

#### Commit 01
* **Author:** Member 6 (Console UI & DevOps Specialist)
* **Branch:** `feature/project-skeleton-and-devops`
* **Message:** `chore(build): initialize maven project configuration and dependencies`
* **Files:**
  1. `pom.xml`
  2. `.gitignore`
* **Details:** Defines project metadata (`groupId: foodflow`, `artifactId: foodflow`, Java 21 LTS release target), Gson 2.10.1, JUnit Jupiter 5.10.2, Surefire plugin, and Maven Shade fat-jar plugin. Adds standard `.gitignore`.

#### Commit 02
* **Author:** Member 4 (Customer Domain & Infrastructure Engineer)
* **Branch:** `feature/project-skeleton-and-devops`
* **Message:** `feat(data): add initial restaurants and menus json dataset`
* **Files:**
  1. `data/restaurants.json`
* **Details:** Adds the UTF-8 JSON dataset containing 6 restaurants (Kadıköy, Beşiktaş, Üsküdar, Şişli) and 35 menu items including Turkish cuisine, delivery fees, and category metadata.

#### Commit 03
* **Author:** Member 6 (Console UI & DevOps Specialist)
* **Branch:** `feature/project-skeleton-and-devops`
* **Message:** `ci(docker): configure multi-stage dockerfile, compose and github actions ci`
* **Files:**
  1. `Dockerfile`
  2. `docker-compose.yml`
  3. `.dockerignore`
  4. `.github/workflows/ci.yml`
* **Details:** Creates multi-stage Docker build (`maven:3.9-eclipse-temurin-21` -> `eclipse-temurin:21-jre`), interactive compose service with tty/stdin, test compose profile, and automated GitHub Actions workflow for pull requests.

#### Commit 04
* **Author:** Member 1 (Domain Modeler & Core Entity Engineer)
* **Branch:** `feature/project-skeleton-and-devops`
* **Message:** `docs(vision): outline iteration 1 vision, scope and team roles`
* **Files:**
  1. `docs/vision.md`
  2. `README.md`
* **Details:** Formulates problem statement, primary stakeholders (Customer, Restaurant Owner, Courier, Admin), feature boundaries, and assumptions for Iteration 1.

---

### Phase 2: Core Domain & Data Infrastructure

#### Commit 05
* **Author:** Member 1 (Domain Modeler & Core Entity Engineer)
* **Branch:** `feature/domain-core-entities`
* **Message:** `feat(domain): implement internal text normalization and validation utilities`
* **Files:**
  1. `src/main/java/foodflow/domain/Texts.java`
* **Details:** Package-private utility class handling Turkish diacritic removal and case folding (`Normalizer.Form.NFD`), empty string checks, and `BigDecimal` rounding scale enforcement (2 decimal places, `RoundingMode.HALF_UP`).

#### Commit 06
* **Author:** Member 1 (Domain Modeler & Core Entity Engineer)
* **Branch:** `feature/domain-core-entities`
* **Message:** `feat(domain): implement MenuItem and Restaurant domain entities`
* **Files:**
  1. `src/main/java/foodflow/domain/MenuItem.java`
  2. `src/main/java/foodflow/domain/Restaurant.java`
* **Details:** Enforces positive price validation on `MenuItem` (BR-1). Implements immutable `Restaurant` model with unique item ID enforcement, menu grouping by category (`menuByCategory()`), and case/diacritic-insensitive filtering (`matches()`).

#### Commit 07
* **Author:** Member 1 (Domain Modeler & Core Entity Engineer)
* **Branch:** `feature/domain-core-entities`
* **Message:** `feat(domain): implement RestaurantCatalog search and indexing`
* **Files:**
  1. `src/main/java/foodflow/domain/RestaurantCatalog.java`
  2. `src/test/java/foodflow/domain/TestData.java`
* **Details:** Adds catalog indexing for fast retrieval by restaurant ID or menu item ID. Adds shared test fixture data (`TestData`) for unit tests.

#### Commit 08
* **Author:** Member 4 (Customer Domain & Infrastructure Engineer)
* **Branch:** `feature/customer-and-infrastructure`
* **Message:** `feat(domain): implement DeliveryAddress and Customer entity validation`
* **Files:**
  1. `src/main/java/foodflow/domain/DeliveryAddress.java`
  2. `src/main/java/foodflow/domain/Customer.java`
* **Details:** Implements `DeliveryAddress` record (district and street). Implements `Customer` entity enforcing valid email structure containing `@`, non-blank names, and associated delivery address (BR-8a, BR-4).

#### Commit 09
* **Author:** Member 4 (Customer Domain & Infrastructure Engineer)
* **Branch:** `feature/customer-and-infrastructure`
* **Message:** `feat(domain): implement CustomerRegistry with uniqueness enforcement and tests`
* **Files:**
  1. `src/main/java/foodflow/domain/CustomerRegistry.java`
  2. `src/test/java/foodflow/domain/CustomerTest.java`
* **Details:** Session-scoped `CustomerRegistry` ensuring email uniqueness regardless of case (BR-8b). Comprehensive tests covering email syntax errors, duplicate registration rejections, and search methods.

#### Commit 10
* **Author:** Member 4 (Customer Domain & Infrastructure Engineer)
* **Branch:** `feature/customer-and-infrastructure`
* **Message:** `feat(infra): implement JsonRestaurantLoader using Gson with error handling`
* **Files:**
  1. `src/main/java/foodflow/infrastructure/DataLoadException.java`
  2. `src/main/java/foodflow/infrastructure/JsonRestaurantLoader.java`
  3. `src/test/resources/test-restaurants.json`
  4. `src/test/java/foodflow/infrastructure/JsonRestaurantLoaderTest.java`
* **Details:** Only class aware of JSON serialization formats. Encapsulates private DTO structures. Validates UTF-8 decoding, missing file handling, and corrupt input detection. Includes unit tests with test fixtures.

---

### Phase 3: Cart, Order & Snapshot Logic

#### Commit 11
* **Author:** Member 2 (Cart & Pricing Logic Specialist)
* **Branch:** `feature/cart-and-pricing-rules`
* **Message:** `feat(domain): implement CartLine with quantity boundary validation`
* **Files:**
  1. `src/main/java/foodflow/domain/CartLine.java`
* **Details:** Implements `CartLine` enforcing quantity constraints between 1 and 20 (BR-1b). Encapsulates line total calculation (`unitPrice * quantity`). Restricts modification methods to package-private access for `Cart` ownership.

#### Commit 12
* **Author:** Member 2 (Cart & Pricing Logic Specialist)
* **Branch:** `feature/cart-and-pricing-rules`
* **Message:** `feat(domain): implement Cart aggregate enforcing single-restaurant and quantity summation`
* **Files:**
  1. `src/main/java/foodflow/domain/Cart.java`
  2. `src/test/java/foodflow/domain/CartTest.java`
* **Details:** Single home for BR-2 (single restaurant constraint), BR-3 (merging lines for duplicate items without creating new entries), and BR-5 (subtotal, delivery fee, and overall total calculations). Unit tests covering boundary values.

#### Commit 13
* **Author:** Member 2 (Cart & Pricing Logic Specialist)
* **Branch:** `feature/cart-and-pricing-rules`
* **Message:** `feat(application): create immutable CartSummary DTO for UI encapsulation`
* **Files:**
  1. `src/main/java/foodflow/application/CartSummary.java`
* **Details:** Encapsulates the cart representation before presenting to the UI layer, preventing external layers from mutating internal domain collections directly.

#### Commit 14
* **Author:** Member 3 (Order Lifecycle & Snapshot Engineer)
* **Branch:** `feature/order-lifecycle-and-snapshots`
* **Message:** `feat(domain): introduce OrderStatus enum and thread-safe OrderNumberGenerator`
* **Files:**
  1. `src/main/java/foodflow/domain/OrderStatus.java`
  2. `src/main/java/foodflow/domain/OrderNumberGenerator.java`
* **Details:** Defines initial `OrderStatus.PLACED` (BR-6b). Implements non-static, sequential order numbering starting from 1001 (BR-6a).

#### Commit 15
* **Author:** Member 3 (Order Lifecycle & Snapshot Engineer)
* **Branch:** `feature/order-lifecycle-and-snapshots`
* **Message:** `feat(domain): implement OrderLine price and name snapshot record`
* **Files:**
  1. `src/main/java/foodflow/domain/OrderLine.java`
* **Details:** Implements immutable record taking an instantaneous snapshot of item ID, name, unit price, quantity, and line total, solving the architectural requirement for BR-7.

#### Commit 16
* **Author:** Member 3 (Order Lifecycle & Snapshot Engineer)
* **Branch:** `feature/order-lifecycle-and-snapshots`
* **Message:** `feat(domain): implement Order creation factory with cart emptying rule`
* **Files:**
  1. `src/main/java/foodflow/domain/Order.java`
* **Details:** Static factory method `Order.place(...)` validating customer address presence and non-empty cart (BR-4), taking snapshot lines, copying totals from `Cart`, and immediately clearing the cart upon placement (BR-7b).

#### Commit 17
* **Author:** Member 3 (Order Lifecycle & Snapshot Engineer)
* **Branch:** `feature/order-lifecycle-and-snapshots`
* **Message:** `test(domain): verify order placement, sequential numbering and price immutability`
* **Files:**
  1. `src/test/java/foodflow/domain/OrderTest.java`
* **Details:** Comprehensive test suite validating BR-4, BR-5, BR-6, and demonstrating that altering a restaurant's menu item price post-order does not alter the placed order total (BR-7).

---

### Phase 4: Application Layer & User Interface

#### Commit 18
* **Author:** Member 5 (Application Service & Testing Architect)
* **Branch:** `feature/app-service-and-console-ui`
* **Message:** `feat(application): implement OrderingService orchestrating system operations`
* **Files:**
  1. `src/main/java/foodflow/application/OrderingService.java`
* **Details:** Application service exposing public methods for each SSD operation: `register`, `login`, `listRestaurants`, `viewMenu`, `addItem`, `changeQuantity`, `removeItem`, `clearCart`, `viewCart`, `placeOrder`, `myOrders`.

#### Commit 19
* **Author:** Member 5 (Application Service & Testing Architect)
* **Branch:** `feature/app-service-and-console-ui`
* **Message:** `test(application): add unit tests for OrderingService use-case workflows`
* **Files:**
  1. `src/test/java/foodflow/application/OrderingServiceTest.java`
* **Details:** Tests checking session-based orders, customer login switching, empty cart order rejection, case-insensitive item addition, and read-only `CartSummary` behavior.

#### Commit 20
* **Author:** Member 6 (Console UI & DevOps Specialist)
* **Branch:** `feature/app-service-and-console-ui`
* **Message:** `feat(ui): implement ConsoleFormatter for menus, cart and printed receipt`
* **Files:**
  1. `src/main/java/foodflow/ui/ConsoleFormatter.java`
* **Details:** Formats restaurant listings, categorized menus, cart tables, order history, and cash-on-delivery receipts with strict two-decimal currency formatting (`0.00 TL`). Contains no business logic.

#### Commit 21
* **Author:** Member 6 (Console UI & DevOps Specialist)
* **Branch:** `feature/app-service-and-console-ui`
* **Message:** `feat(ui): implement FilterArguments parser for restaurant search flags`
* **Files:**
  1. `src/main/java/foodflow/ui/FilterArguments.java`
* **Details:** Extracts `--cuisine` and `--district` multi-word options cleanly from command arguments, keeping command processing logic modular and clean.

#### Commit 22
* **Author:** Member 6 (Console UI & DevOps Specialist)
* **Branch:** `feature/app-service-and-console-ui`
* **Message:** `feat(ui): implement interactive ConsoleUI with robust BR-9 error handling`
* **Files:**
  1. `src/main/java/foodflow/ui/ConsoleUI.java`
* **Details:** CLI command loop processing all user inputs. Intercepts `IllegalArgumentException`, `IllegalStateException`, and unexpected `RuntimeException` instances without crashing the terminal session (BR-9). Supports optional input echoing for automated demo scripts.

#### Commit 23
* **Author:** Member 5 (Application Service & Testing Architect)
* **Branch:** `feature/app-service-and-console-ui`
* **Message:** `feat(app): configure main application entry point and dependency wiring`
* **Files:**
  1. `src/main/java/foodflow/App.java`
* **Details:** Wires repository, services, loader, and UI with explicit dependency injection. Resolves `FOODFLOW_DATA` path and initializes UTF-8 I/O streams.

#### Commit 24
* **Author:** Member 5 (Application Service & Testing Architect)
* **Branch:** `feature/app-service-and-console-ui`
* **Message:** `test(ui): add end-to-end console simulation and error resilience tests`
* **Files:**
  1. `src/test/java/foodflow/ui/ConsoleUITest.java`
* **Details:** Simulates bad user inputs (unknown commands, malformed numbers, non-existent menu items) confirming the loop survives without crashing. Tests full end-to-end registration-to-receipt flow.

---

### Phase 5: Verification & Deliverables

#### Commit 25
* **Author:** Member 1, Member 2, Member 3, Member 4, Member 5, Member 6 (Collaborative)
* **Branch:** `feature/app-service-and-console-ui`
* **Message:** `docs(spec): finalize glossary, use cases, domain model, SSD, contracts and sample run`
* **Files:**
  1. `docs/glossary.md`
  2. `docs/use-cases.md`
  3. `docs/supplementary-spec.md`
  4. `docs/domain-model.md`
  5. `docs/ssd-and-contracts.md`
  6. `docs/design-decisions.md`
  7. `docs/iteration-plan.md`
  8. `docs/demo-input.txt`
  9. `docs/sample-run.txt`
* **Details:** Completes deliverables D1–D9: 24-term glossary with BR mappings, fully dressed use cases (*Place Order* & *Manage Cart*), FURPS+ specifications, conceptual domain diagram, SSD with operation contracts, 9 design rationales, risk analysis for Iteration 2, and the recorded execution transcript.

---

## 4. Summary of Commit Distribution by Team Member

| Member | Branch | Commits Owned | Total Commits |
|---|---|---|---|
| **Member 1** | `feature/project-skeleton-and-devops`, `feature/domain-core-entities` | #04, #05, #06, #07, #25 (shared) | **5** |
| **Member 2** | `feature/cart-and-pricing-rules` | #11, #12, #13, #25 (shared) | **4** |
| **Member 3** | `feature/order-lifecycle-and-snapshots` | #14, #15, #16, #17, #25 (shared) | **5** |
| **Member 4** | `feature/customer-and-infrastructure` | #02, #08, #09, #10, #25 (shared) | **5** |
| **Member 5** | `feature/app-service-and-console-ui` | #18, #19, #23, #24, #25 (shared) | **5** |
| **Member 6** | `feature/project-skeleton-and-devops`, `feature/app-service-and-console-ui` | #01, #03, #20, #21, #22, #25 (shared) | **6** |
| **Total** | | | **25 Commits** |

