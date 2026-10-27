# Design Decisions (input for the report, D8)

## 1. Layers
`domain` (rules, no I/O) ← `application` (`OrderingService`, one method per system operation) ← `ui` (`ConsoleUI`, formatting only). `infrastructure` (`JsonRestaurantLoader`) turns JSON into domain objects. `App` wires everything with constructor injection. **Why:** the brief grades layering, and a later GUI or database only needs a new `ui` / `infrastructure` class.

## 2. BR-7: Order records name and price, it does not point to live MenuItems
`Order` holds `OrderLine` records (item id, name, unit price, quantity) plus the restaurant name and delivery fee **copied at the moment of ordering**.
- **Alternative:** `Order` refers to the live `MenuItem` objects. Simple, but when a menu price changes in Iteration 2, the total of an already placed order would change, which violates BR-7 and falsifies receipts.
- **Decision:** snapshot. `OrderTest.orderTotalDoesNotDependOnLaterMenuPriceChanges_BR7` documents it.
- **Cost:** a little duplication of data. Accepted.

## 3. Every rule in one class
See the table in `glossary.md`. Example: quantity 1–20 is checked only in `CartLine`; `Cart` and the UI reuse it, they never repeat the numbers.

## 4. Exceptions for rule violations
`IllegalArgumentException` for invalid values (price, quantity, e-mail, unknown id), `IllegalStateException` for operations impossible in the current state (second restaurant in cart, empty cart, duplicate e-mail). `ConsoleUI` catches both and prints the message (BR-9). The UI never contains a rule, so messages are written in the domain.

## 5. Money as `BigDecimal`
`double` can produce 0.30000000000000004. All amounts are `BigDecimal` with scale 2, `HALF_UP`. The loader converts the JSON numbers; formatting to "280.00 TL" lives in `ConsoleFormatter`.

## 6. No static state
Order numbers (`OrderNumberGenerator`), customers (`CustomerRegistry`), cart and orders live in objects created by `App`. Tests create fresh objects, so they are independent.

## 7. Failed operations change nothing
Validation comes before mutation (`Cart.addItem`, `CartLine.changeQuantity`, `Order.place` takes a number only after all checks). This makes BR-2 and BR-4 failures safe to retry.

## 8. Forgiving text matching
Filters and ids ignore case and Turkish diacritics (`Texts.normalize`) because users type "kadikoy". Display always uses the original text.

## 9. Encapsulated UI View (`CartSummary`)
The UI never receives the mutable `Cart` domain reference directly. `OrderingService.viewCart()` returns an immutable `CartSummary` record. This guarantees that UI components cannot bypass application or domain rules by altering cart state directly.

