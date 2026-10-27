# Domain Model (D5)

Conceptual model: concepts, attributes and associations with multiplicities. No methods, no Java types, no UI or file classes.

```mermaid
classDiagram
    direction LR

    class Customer {
        name
        email
    }
    class DeliveryAddress {
        district
        street
    }
    class Restaurant {
        name
        cuisine
        district
        deliveryFee
        minimumBasket
    }
    class MenuItem {
        name
        category
        price
    }
    class Cart
    class CartLine {
        quantity
    }
    class Order {
        number
        status
        deliveryFee
        total
    }
    class OrderLine {
        itemName
        unitPrice
        quantity
    }

    Customer "1" --> "1" DeliveryAddress : delivers to
    Customer "1" --> "0..1" Cart : fills
    Cart "1" o-- "0..*" CartLine : contains
    CartLine "*" --> "1" MenuItem : is for
    Cart "0..1" --> "0..1" Restaurant : orders from
    Restaurant "1" *-- "1..*" MenuItem : offers
    Customer "1" --> "0..*" Order : places
    Order "*" --> "1" Restaurant : is from
    Order "1" *-- "1..*" OrderLine : consists of
    Order "*" --> "1" DeliveryAddress : is delivered to
```

## Notes
- **Cart ↔ Restaurant (BR-2):** a cart refers to at most one restaurant; empty cart: none.
- **CartLine → MenuItem (BR-3):** per cart, each menu item appears on at most one line.
- **OrderLine is not a link to MenuItem (BR-7):** it records name and price at the time of ordering, so later menu changes do not change a placed order (see `design-decisions.md`).
- `total` of an Order is derived: sum of line totals + `deliveryFee` (BR-5).
- `status` starts as PLACED (BR-6); `number` is unique and increasing (BR-6).
- Customer is identified by `email` (BR-8).
- All words follow `glossary.md`.

