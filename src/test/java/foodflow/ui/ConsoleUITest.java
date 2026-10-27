package foodflow.ui;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import foodflow.application.OrderingService;
import foodflow.domain.CustomerRegistry;
import foodflow.domain.OrderNumberGenerator;
import foodflow.domain.Restaurant;
import foodflow.domain.TestData;

class ConsoleUITest {

    private String run(String input) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        OrderingService service = new OrderingService(TestData.catalog(), new CustomerRegistry(), new OrderNumberGenerator());
        new ConsoleUI(service, new BufferedReader(new StringReader(input)), out).run();
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void invalidInputNeverEndsTheProgram_BR9() {
        String output = run("""
                banana
                add
                add R01-01 abc
                add R01-01 99
                add NOPE 1
                menu R99
                login nobody@example.com
                order
                restaurants --cuisine Martian
                restaurants --weird
                help
                quit
                """);

        assertTrue(output.contains("Unknown command 'banana'"));
        assertTrue(output.contains("Quantity must be a whole number"));
        assertTrue(output.contains("Quantity must be between 1 and 20"));
        assertTrue(output.contains("Unknown menu item"));
        assertTrue(output.contains("Unknown restaurant"));
        assertTrue(output.contains("Your cart is empty") || output.contains("Identify yourself"));
        assertTrue(output.contains("No restaurants match your filter"));
        assertTrue(output.contains("Commands:"));   // program was still alive at 'help'
        assertTrue(output.contains("Goodbye!"));    // and ended only because of 'quit'
    }

    @Test
    void unexpectedErrorInsideACommandDoesNotEndTheProgram_BR9() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        OrderingService broken = new OrderingService(TestData.catalog(), new CustomerRegistry(), new OrderNumberGenerator()) {
            @Override
            public List<Restaurant> listRestaurants(String cuisine, String district) {
                throw new RuntimeException("boom");
            }
        };

        new ConsoleUI(broken, new BufferedReader(new StringReader("restaurants\nhelp\nquit\n")), out).run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Something went wrong"));
        assertFalse(output.contains("boom"));        // internal details are not shown
        assertTrue(output.contains("Commands:"));    // still alive afterwards
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void loginRemoveAndClearWorkThroughTheConsole() {
        String output = run("""
                register
                Ali
                ali@example.com
                Kadıköy
                Moda
                add R01-01 2
                add R01-05 1
                remove R01-05
                cart
                cart clear
                cart
                login ali@example.com
                orders
                """);

        assertTrue(output.contains("Removed. Cart subtotal: 280.00 TL"));
        assertTrue(output.contains("Cart emptied."));
        assertTrue(output.contains("Your cart is empty."));
        assertTrue(output.contains("Welcome back, Ali!"));
        assertTrue(output.contains("You have not placed any orders in this session."));
    }

    @Test
    void filterArgumentsAcceptMultiWordValuesAndRejectUnknownOptions() {
        FilterArguments both = FilterArguments.parse(List.of("--cuisine", "Fast", "Food", "--district", "Kadıköy"));
        assertEquals("Fast Food", both.cuisine());
        assertEquals("Kadıköy", both.district());

        FilterArguments none = FilterArguments.parse(List.of());
        assertNull(none.cuisine());
        assertNull(none.district());

        assertThrows(IllegalArgumentException.class, () -> FilterArguments.parse(List.of("--weird")));
        assertThrows(IllegalArgumentException.class, () -> FilterArguments.parse(List.of("Turkish")));
    }

    @Test
    void fullDemoScenarioPrintsReceipt() {
        String output = run("""
                register
                Ali Yılmaz
                ali@example.com
                Kadıköy
                Moda Cad. 5
                restaurants --district kadikoy
                menu R01
                add R01-01 2
                add R02-01 1
                set R01-01 3
                cart
                order
                orders
                """);

        assertTrue(output.contains("Added 2 x Lahmacun. Cart subtotal: 280.00 TL"));
        assertTrue(output.contains("Your cart has items from Kebapçı Mehmet"));
        assertTrue(output.contains("Order #1001 placed: 445.00 TL (incl. delivery 25.00 TL), cash on delivery."));
        assertTrue(output.contains("RECEIPT"));
        assertTrue(output.contains("Order #1001"));
    }
}

