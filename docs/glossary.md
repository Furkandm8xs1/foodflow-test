# Glossary (D2)

Same words are used in use cases, domain model and code. "Rule home" is the one class that enforces the rule.

| # | Term | Definition | Rules | Aliases | Code / rule home |
|---|---|---|---|---|---|
| 1 | Customer | Person who orders food. Identified by e-mail. | BR-8 | user, client | `Customer` |
| 2 | E-mail address | Unique identifier of a customer; contains "@". | BR-8 | login, id | `Customer` (format), `CustomerRegistry` (uniqueness) |
| 3 | Delivery address | District and street where an order is delivered. | BR-4 | address | `DeliveryAddress` |
| 4 | District | City area (e.g. Kadıköy) of a restaurant or address. Used for filtering. | - | neighbourhood, area | `DeliveryAddress`, `Restaurant` |
| 5 | Restaurant | Place that prepares food; has cuisine, district, delivery fee and a menu. | BR-5 | shop, vendor | `Restaurant` |
| 6 | Cuisine | Kind of food a restaurant serves (Turkish, Italian ...). Used for filtering. | - | food type | `Restaurant` |
| 7 | Menu | The list of menu items a restaurant offers. | - | - | `Restaurant.menu()` |
| 8 | Menu item | A dish or drink with id, name, category and price. | BR-1 | product, dish | `MenuItem` |
| 9 | Category | Group of menu items (Main, Soup, Dessert, Drink ...). | - | section | `MenuItem` |
| 10 | Price | Positive amount in TL of one menu item. | BR-1 | unit price | `MenuItem` |
| 11 | Delivery fee | Amount a restaurant charges for delivery. | BR-5 | shipping | `Restaurant` |
| 12 | Cart | Items a customer intends to order; from one restaurant only. | BR-2, BR-3, BR-7 | basket | `Cart` |
| 13 | Cart line | One menu item with a quantity in the cart; one line per item. | BR-1, BR-3 | basket line | `CartLine` |
| 14 | Quantity | Number of units on a cart line, 1 to 20. | BR-1 | amount, count | `CartLine` |
| 15 | Line total | Price times quantity of a line. | BR-5 | - | `CartLine`, `OrderLine` |
| 16 | Subtotal | Sum of the line totals. | BR-5 | items total | `Cart`, `Order` |
| 17 | Order total | Subtotal plus the restaurant's delivery fee. | BR-5 | total | `Order` |
| 18 | Order | Placed, cash-on-delivery request for the cart's content; a snapshot. | BR-4, BR-5, BR-6, BR-7 | purchase | `Order` |
| 19 | Order line | Copy of a cart line at the moment of ordering (item id, name, unit price, quantity). | BR-7 | order item | `OrderLine` |
| 20 | Order number | Unique, increasing number of an order (starts at 1001). | BR-6 | order id | `OrderNumberGenerator` |
| 21 | Order status | State of an order; initial value PLACED. | BR-6 | state | `OrderStatus` |
| 22 | Cash on delivery | Payment method of I1: customer pays the courier on arrival. | - | COD | `Order` (only payment in I1) |
| 23 | Receipt | Printed summary of a placed order. | BR-5 | invoice, bill | `ConsoleFormatter` |
| 24 | Session | One run of the application; all data lives in memory. | - | run | `OrderingService` |

## Where each business rule lives
| Rule | Description | Class / Home |
|---|---|---|
| BR-1a | Price > 0 | `MenuItem` (constructor) |
| BR-1b | Quantity 1..20 | `CartLine` (validation in constructor and `changeQuantity`) |
| BR-2 | Single restaurant per cart | `Cart.addItem` |
| BR-3 | Single line per item (increases quantity) | `Cart.addItem` |
| BR-4 | Non-empty cart and customer address required | `Order.place` |
| BR-5 | Total = subtotal + delivery fee | `Cart` (single calculation source for totals), `CartLine` (line total) |
| BR-6a | Unique increasing order numbers | `OrderNumberGenerator` |
| BR-6b | Initial status PLACED | `Order` / `OrderStatus` |
| BR-7a | Order lines snapshot prices and names | `OrderLine` |
| BR-7b | Placing order empties cart | `Order.place` |
| BR-8a | Email contains "@" and valid format | `Customer` |
| BR-8b | Email uniqueness in session | `CustomerRegistry` |
| BR-9 | Invalid input never terminates program | `ConsoleUI` (catches domain exceptions and unexpected runtime errors) |

