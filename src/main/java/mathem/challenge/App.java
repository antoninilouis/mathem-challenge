package mathem.challenge;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.EnumSet;

/**
 * - A delivery date is not valid if a product can't be delivered on that
 *   weekday
 * - A delivery date is not valid if a product must be ordered more days in 
 *   advance than possible
 * - All external products need to be ordered 5 days in advance
 * - Temporary products can only be ordered within the current week (Mon-Sun)
 * 
 * The result should be sorted in priority order, with green delivery dates at
 * the top of the list if they are within the next 3 days, otherwise dates
 * should just be sorted ascending.
 */
public class App {
    private static final EnumSet<DayOfWeek> greenDays; 

    // temporary definition for “green” (environment-friendly) delivery dates
    // could be replaced by specific days of months
    static {
        greenDays = EnumSet.range(DayOfWeek.FRIDAY, DayOfWeek.SUNDAY);
    }

    public static void main(String[] args) {
    }

    /**
     * Return the available delivery dates for the upcoming 14 days.
     * Calculations are made from the current date when code runs.
     * @param postcode - the postcal code for the delivery
     * @param products - the list of products to deliver
     * @return the available delivery dates for the upcoming 14 days.
     */
    public static void listDeliveryDates(String postcode,
                                         Collection<Product> products) {
        // date-time with an offset from UTC/Greenwich in the ISO-8601
        OffsetDateTime dateTime = OffsetDateTime.now(ZoneId.systemDefault());

        // Filter the valid products
    }
}
