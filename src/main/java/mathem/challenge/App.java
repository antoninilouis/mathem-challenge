package mathem.challenge;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import mathem.challenge.Product.ProductType;

/**
 * The result should be sorted in priority order, with green delivery dates at
 * the top of the list if they are within the next 3 days, otherwise dates
 * should just be sorted ascending.
 */
public class App {
    private static final EnumSet<DayOfWeek> greenDays;
    private static final int PERIOD_LENGTH = 14;
    private DeliveryService deliveryService;

    // temporary definition for “green” (environment-friendly) delivery dates
    // could be replaced by specific days of months
    static {
        greenDays = EnumSet.range(DayOfWeek.FRIDAY, DayOfWeek.SUNDAY);
    }

    public App(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    public static void main(String[] args) {
        DeliveryService deliveryService = new DeliveryService();
        App app = new App(deliveryService);
        Product[] products = new Product[] {
            Product.create("P1", ProductType.NORMAL,
                 EnumSet.allOf(DayOfWeek.class), 15),
            Product.create("P2", ProductType.NORMAL,
                 EnumSet.allOf(DayOfWeek.class), 0),
            Product.create("P3", ProductType.NORMAL,
                 EnumSet.allOf(DayOfWeek.class), 0),
            Product.create("P4", ProductType.NORMAL,
                 EnumSet.allOf(DayOfWeek.class), 0),
        };
        app.listDeliveryDates("12345", Arrays.asList(products));
    }

    /**
     * Return the available delivery dates for the upcoming 14 days.
     * Calculations are made from the current date when code runs.
     * 
     * - A delivery date is not good if a product can't be delivered on that
     *   weekday
     * - A delivery date is not good if a product must be ordered more days in
     *   advance than possible
     * 
     * @param postcode - the postcal code for the delivery
     * @param products - the list of products to deliver
     * @return the available delivery dates for the upcoming 14 days.
     */
    public void listDeliveryDates(String postcode,
                                         Collection<Product> products) {
        products = getValidProducts(new ArrayList<Product>(products));
        for (Product product : products) {
            List<LocalDate> possibleDays = possibleDays(product);
            deliveryService.scheduleDelivery(possibleDays, product);
        }
    }

    private static List<Product> getValidProducts(List<Product> products) {
        return products.stream().collect(Collectors.filtering(product -> 
        product.isValid(), Collectors.toList()));
    }

    /**
     * Returns a list of LocalDate within the delivery period where a delivery
     * for the product could happen if there is a delivery slot.
     * @param product
     * @return
     */
    private static List<LocalDate> possibleDays(Product product) {
        int daysInAdvance = product.getDaysInAdvance();
        if (daysInAdvance > PERIOD_LENGTH - 1) {
            return new ArrayList<LocalDate>();
        }
        EnumSet<DayOfWeek> deliveryDays = product.getDeliveryDays();
        LocalDate d = LocalDate.now();
        ArrayList<LocalDate> possibleDays = new ArrayList<LocalDate>();
        for (int i = 0; i < PERIOD_LENGTH; i++) {
            d = d.plusDays(1);
            if (deliveryDays.contains(d.getDayOfWeek()))
                possibleDays.add(d);
        }
        return possibleDays;
    }
}
