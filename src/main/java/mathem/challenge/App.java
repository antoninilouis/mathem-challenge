package mathem.challenge;

// Also include a hard-coded definition for “green” (environment-friendly) delivery dates. This could
// be as simple as “all Wednesdays” or “date 5, 15 and 25 each month”.

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
    public static void main(String[] args) {
    }
}
