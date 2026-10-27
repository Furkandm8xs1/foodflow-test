package foodflow.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import foodflow.application.CartSummary;
import foodflow.domain.MenuItem;
import foodflow.domain.Order;
import foodflow.domain.OrderLine;
import foodflow.domain.Restaurant;

/** Turns domain and application objects into console text. Pure formatting: no rules, no state. */
final class ConsoleFormatter {

    private ConsoleFormatter() {
    }

    /** All amounts: two decimals and "TL" (BR-5). */
    static String money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString() + " TL";
    }

    static String restaurantLine(Restaurant r) {
        return String.format("%-4s %-20s %-11s %-10s delivery %s",
                r.id(), r.name(), r.cuisine(), r.district(), money(r.deliveryFee()));
    }

    static String menu(Restaurant r) {
        StringBuilder sb = new StringBuilder();
        sb.append(r.name()).append(" - ").append(r.cuisine()).append(", ").append(r.district())
                .append(" (delivery ").append(money(r.deliveryFee())).append(")\n");
        for (Map.Entry<String, List<MenuItem>> category : r.menuByCategory().entrySet()) {
            sb.append("  ").append(category.getKey()).append('\n');
            for (MenuItem item : category.getValue()) {
                sb.append(String.format("    %-8s %-28s %s%n", item.id(), item.name(), money(item.price())));
            }
        }
        return sb.toString().stripTrailing();
    }

    static String cart(CartSummary cart) {
        if (cart.isEmpty()) {
            return "Your cart is empty.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cart - ").append(cart.restaurantName()).append('\n');
        for (CartSummary.Line line : cart.lines()) {
            sb.append(String.format("  %-8s %-26s %2d x %-10s = %s%n", line.itemId(), line.itemName(),
                    line.quantity(), money(line.unitPrice()), money(line.lineTotal())));
        }
        sb.append(String.format("  %-48s %s%n", "Subtotal:", money(cart.subtotal())));
        sb.append(String.format("  %-48s %s%n", "Delivery fee:", money(cart.deliveryFee())));
        sb.append(String.format("  %-48s %s", "Total:", money(cart.total())));
        return sb.toString();
    }

    static String orderSummary(Order o) {
        return String.format("Order #%d  %-18s %-7s %s", o.number(), o.restaurantName(), o.status(), money(o.total()));
    }

    static String receipt(Order o) {
        StringBuilder sb = new StringBuilder();
        sb.append("================ RECEIPT ================\n");
        sb.append("Order #").append(o.number()).append("   Status: ").append(o.status()).append('\n');
        sb.append("Restaurant : ").append(o.restaurantName()).append('\n');
        sb.append("Customer   : ").append(o.customer().name()).append(" <").append(o.customer().email()).append(">\n");
        sb.append("Deliver to : ").append(o.deliveryAddress()).append('\n');
        sb.append("-----------------------------------------\n");
        for (OrderLine line : o.lines()) {
            sb.append(String.format("  %2d x %-24s %s%n", line.quantity(), line.itemName(), money(line.lineTotal())));
        }
        sb.append("-----------------------------------------\n");
        sb.append(String.format("  %-28s %s%n", "Subtotal:", money(o.subtotal())));
        sb.append(String.format("  %-28s %s%n", "Delivery fee:", money(o.deliveryFee())));
        sb.append(String.format("  %-28s %s%n", "TOTAL:", money(o.total())));
        sb.append("Payment    : cash on delivery\n");
        sb.append("=========================================");
        return sb.toString();
    }
}
