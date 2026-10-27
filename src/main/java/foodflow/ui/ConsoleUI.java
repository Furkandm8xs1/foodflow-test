package foodflow.ui;

import foodflow.application.OrderingService;
import foodflow.domain.Customer;
import foodflow.domain.MenuItem;
import foodflow.domain.Order;
import foodflow.domain.Restaurant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Reads commands, calls the application layer and prints the results.
 * It contains no business rules; rule violations arrive as exceptions and are shown as
 * friendly messages so that invalid input never ends the program (BR-9).
 */
public class ConsoleUI {

    private static final String PROMPT = "FoodFlow > ";

    private final OrderingService service;
    private final BufferedReader in;
    private final PrintStream out;
    private final boolean echoInput;

    public ConsoleUI(OrderingService service, BufferedReader in, PrintStream out) {
        this(service, in, out, false);
    }

    public ConsoleUI(OrderingService service, BufferedReader in, PrintStream out, boolean echoInput) {
        this.service = service;
        this.in = in;
        this.out = out;
        this.echoInput = echoInput;
    }

    public void run() {
        out.println("Welcome to FoodFlow! Type 'help' to see the commands.");
        while (true) {
            out.print(PROMPT);
            out.flush();
            String line = readLine();
            if (line == null) { // end of input
                out.println();
                break;
            }
            if (line.isBlank()) {
                continue;
            }
            if (echoInput) { // for piped input (sample-run transcript): show what was "typed"
                out.println(line);
            }
            if (!handle(line.trim())) {
                break;
            }
        }
        out.println("Goodbye!");
    }

    private String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Returns false when the user wants to quit. Never throws for bad user input. */
    private boolean handle(String line) {
        String[] parts = line.split("\\s+");
        String command = parts[0].toLowerCase(Locale.ROOT);
        List<String> args = Arrays.asList(parts).subList(1, parts.length);
        try {
            switch (command) {
                case "help" -> printHelp();
                case "register" -> register();
                case "login" -> login(args);
                case "restaurants" -> restaurants(args);
                case "menu" -> menu(args);
                case "add" -> add(args);
                case "set" -> setQuantity(args);
                case "remove" -> remove(args);
                case "cart" -> cart(args);
                case "order" -> order();
                case "orders" -> orders();
                case "quit", "exit" -> {
                    return false;
                }
                default -> out.println("Unknown command '" + command + "'. Type 'help' for the list of commands.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            out.println("! " + e.getMessage());
        } catch (RuntimeException e) {
            // BR-9: whatever goes wrong inside one command must not end the program.
            out.println("! Something went wrong (" + e.getClass().getSimpleName() + "). Please try again.");
        }
        return true;
    }

    private void printHelp() {
        out.println("""
                Commands:
                  register                              create a customer (asks name, e-mail, district, street)
                  login <e-mail>                        continue as an existing customer
                  restaurants [--cuisine X] [--district Y]   list / filter restaurants
                  menu <restaurantId>                   show a restaurant's menu, e.g. menu R01
                  add <itemId> <quantity>               add to cart, e.g. add R01-01 2
                  set <itemId> <quantity>               change a quantity in the cart
                  remove <itemId>                       remove an item from the cart
                  cart                                  show the cart
                  cart clear                            empty the cart
                  order                                 place the order (cash on delivery)
                  orders                                list your orders in this session
                  help | quit""");
    }

    // ---- F1 ----

    private void register() {
        String name = ask("Name: ");
        String email = ask("E-mail: ");
        String district = ask("District: ");
        String street = ask("Street: ");
        Customer c = service.register(name, email, district, street);
        out.println("Welcome, " + c.name() + "! Delivering to " + c.address() + ".");
    }

    private void login(List<String> args) {
        requireArgs(args, 1, "login <e-mail>");
        Customer c = service.login(args.get(0));
        out.println("Welcome back, " + c.name() + "! Delivering to " + c.address() + ".");
    }

    private String ask(String question) {
        out.print(question);
        out.flush();
        String answer = readLine();
        if (echoInput && answer != null) {
            out.println(answer);
        }
        return answer == null ? "" : answer.trim();
    }

    // ---- F2, F3, F4 ----

    private void restaurants(List<String> args) {
        FilterArguments filter = FilterArguments.parse(args);
        List<Restaurant> found = service.listRestaurants(filter.cuisine(), filter.district());
        if (found.isEmpty()) {
            out.println("No restaurants match your filter. Try another cuisine or district.");
            return;
        }
        found.forEach(r -> out.println(ConsoleFormatter.restaurantLine(r)));
    }

    private void menu(List<String> args) {
        requireArgs(args, 1, "menu <restaurantId>");
        out.println(ConsoleFormatter.menu(service.viewMenu(args.get(0))));
    }

    // ---- F5 ----

    private void add(List<String> args) {
        requireArgs(args, 2, "add <itemId> <quantity>");
        int quantity = parseQuantity(args.get(1));
        MenuItem item = service.addItem(args.get(0), quantity);
        out.println("Added " + quantity + " x " + item.name() + ". Cart subtotal: "
                + ConsoleFormatter.money(service.viewCart().subtotal()));
    }

    private void setQuantity(List<String> args) {
        requireArgs(args, 2, "set <itemId> <quantity>");
        service.changeQuantity(args.get(0), parseQuantity(args.get(1)));
        out.println("Quantity updated. Cart subtotal: " + ConsoleFormatter.money(service.viewCart().subtotal()));
    }

    private void remove(List<String> args) {
        requireArgs(args, 1, "remove <itemId>");
        service.removeItem(args.get(0));
        out.println("Removed. Cart subtotal: " + ConsoleFormatter.money(service.viewCart().subtotal()));
    }

    private void cart(List<String> args) {
        if (args.isEmpty()) {
            out.println(ConsoleFormatter.cart(service.viewCart()));
        } else if (args.size() == 1 && args.get(0).equalsIgnoreCase("clear")) {
            service.clearCart();
            out.println("Cart emptied.");
        } else {
            throw new IllegalArgumentException("Usage: cart  or  cart clear");
        }
    }

    // ---- F6, F7 ----

    private void order() {
        Order order = service.placeOrder();
        out.println("Order #" + order.number() + " placed: " + ConsoleFormatter.money(order.total())
                + " (incl. delivery " + ConsoleFormatter.money(order.deliveryFee()) + "), cash on delivery.");
        out.println(ConsoleFormatter.receipt(order));
    }

    private void orders() {
        List<Order> mine = service.myOrders();
        if (mine.isEmpty()) {
            out.println("You have not placed any orders in this session.");
            return;
        }
        mine.forEach(o -> out.println(ConsoleFormatter.orderSummary(o)));
    }

    // ---- input helpers (syntax only, no business rules) ----

    private static void requireArgs(List<String> args, int expected, String usage) {
        if (args.size() != expected) {
            throw new IllegalArgumentException("Usage: " + usage);
        }
    }

    private static int parseQuantity(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity must be a whole number, not '" + text + "'.");
        }
    }
}
