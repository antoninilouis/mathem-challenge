package mathem.challenge;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.javatuples.Pair;

/**
 * We need a delivery service to keep track of which "delivery slots" are
 * available for each days of the delivery period.
 */
public class DeliveryService {
    private static final EnumSet<DayOfWeek> greenDays;
    private static final int FIRST_DELIVERY_SLOT = 9;
    private static final int LAST_DELIVERY_SLOT = 19;
    private static final int MAX_DELIVERIES = 1
    + LAST_DELIVERY_SLOT - FIRST_DELIVERY_SLOT;
    private SortedSet<DeliverySlot> deliveries =
    new TreeSet<DeliverySlot>(new Comparator<DeliverySlot>() {
        @Override public int compare(DeliverySlot o1, DeliverySlot o2) {
            return o1.begin.compareTo(o2.begin);
        }
    });

    // Green days are defined as FRIDAY, SATURDAY and SUNDAY
    static {
        greenDays = EnumSet.range(DayOfWeek.FRIDAY, DayOfWeek.SUNDAY);
        greenDays.add(DayOfWeek.TUESDAY);
    }

    /**
     * We override Object.equals and Object.hashCode methods for DeliverySlot
     * to enable efficient HashSet operations
     */
    private class DeliverySlot {
        private final Instant begin;
        private final Instant end;
        private final boolean isGreen;
        private UUID productId;

        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public UUID getProductId() {
            return this.productId;
        }

        public boolean getIsGreen() {
            return this.isGreen;
        }

        private DeliverySlot(Instant begin, Instant end) {
            this.begin = begin;
            this.end = end;
            this.isGreen = greenDays.contains(LocalDate
            .ofInstant(this.begin, ZoneId.systemDefault()).getDayOfWeek());
        }

        /**
         * The DeliverySlot is a class managed by the DeliverySchedule to
         * represent a duration of time delimited by a beginning instant
         * and an ending instant during which a delivery can occur within
         * the DeliveryService schedule.
         */
        private DeliverySlot(LocalDateTime datetime) {
            Instant begin = Instant.from(datetime
            .atZone(ZoneId.systemDefault()));
            Instant end = Instant.from(datetime.plusHours(1)
            .atZone(ZoneId.systemDefault()));
            this.begin = begin;
            this.end = end;
            this.isGreen = greenDays.contains(LocalDate
            .ofInstant(this.begin, ZoneId.systemDefault()).getDayOfWeek());
        }

        private boolean overlaps(Instant begin, Instant end) {
            return (begin.isAfter(this.begin) && begin.isBefore(this.end))
                || (end.isAfter(this.begin) && end.isBefore(this.end))
                || (begin.isBefore(this.begin) && end.isAfter(this.end));
        }

        @Override public boolean equals(Object obj) {
            if (!(obj instanceof DeliverySlot))
                return false;
            DeliverySlot deliverySlot = (DeliverySlot) obj;
            return deliverySlot.begin.equals(begin)
                && deliverySlot.end.equals(end);
        }

        @Override public int hashCode() {
            return this.begin.hashCode() ^ this.end.hashCode();
        }

        @Override public String toString() {
            return this.begin + " to " + this.end;
        }
    }

    /**
     * Create a new DeliverySlot object based on the ldt parameter and insert
     * it into the DeliveryService set of deliveries
     * @param ldt - the LocalDateTime object to define the created DeliverSlot
     * beginning and end instants from
     * @return a boolean value equal to true if the DeliverySlot was added to
     * the set of deliveries.
     */
    public boolean addSlot(LocalDateTime ldt) {
        DeliverySlot slot = new DeliverySlot(ldt);
        return deliveries.add(slot);
    }

    public boolean addSlot(DeliverySlot slot) {
        return deliveries.add(slot);
    }

    /**
     * Adds a new slot in place of a free slot found in the list of possible
     * days provided
     * @param possibleDays - the list of days to find free slots from
     * @return a boolean value indicating if the delivery scheduling operation
     * succeeded (true) or failed (false)
     */
    public boolean scheduleDelivery(List<LocalDate> possibleDays,
                                    Product product) {
        for (LocalDate day : possibleDays) {
            if (nextSlot(day).isPresent()) {
                DeliverySlot slot = nextSlot(day).get();
                slot.setProductId(product.getProductId());
                return addSlot(slot);
            }
        }
        return false;
    }

    /**
     * Returns a DeliverySlot object representing the next available delivery
     * slot for the given day
     * @param day - LocalDate object representing the day to look the delivery
     * slot from
     * @return a DeliverySlot object  representing the next available delivery
     * slot for the given day or null if none was found
     */
    private Optional<DeliverySlot> nextSlot(LocalDate day) {
        DeliverySlot slot = null;
        if (countDeliveries(day) == MAX_DELIVERIES)
            return Optional.empty();
        Instant begin = Instant.from(day.atStartOfDay()
        .atZone(ZoneId.systemDefault()));
        Instant end = Instant.from(day.plusDays(1).atStartOfDay()
        .atZone(ZoneId.systemDefault()));
        Set<DeliverySlot> dayDeliveries = deliveries.stream()
        .filter(deliverySlot -> deliverySlot.overlaps(begin, end))
        .collect(Collectors.toSet());
        for (int i = FIRST_DELIVERY_SLOT; i <= LAST_DELIVERY_SLOT; i++) {
            slot = new DeliverySlot(LocalDateTime
            .from(day.atTime(i, 0).atZone(ZoneId.systemDefault())));
            if (!dayDeliveries.contains(slot))
                return Optional.of(slot);
        }
        return Optional.ofNullable(slot);
    }

    public int countDeliveries() {
        return deliveries.size();
    }

    public int countDeliveries(LocalDate day) {
        return deliveries.stream().collect(Collectors.filtering(d -> 
        {
            Instant begin = day.atStartOfDay().atZone(ZoneId.systemDefault())
            .toInstant();
            Instant end = day.plusDays(1).atStartOfDay().atZone(ZoneId
            .systemDefault()).toInstant();
            return d.overlaps(begin, end);
        }, Collectors.toSet())).size();
    }

    public List<Pair<OffsetDateTime,Boolean>> getSchedule() {
        List<Pair<OffsetDateTime,Boolean>> schedule = 
        new ArrayList<Pair<OffsetDateTime,Boolean>>();
        TreeSet<DeliverySlot> greenDayFirstDeliveries =
        new TreeSet<DeliverySlot>(new Comparator<DeliverySlot>() {
            @Override public int compare(DeliverySlot o1, DeliverySlot o2) {
                Instant greenLimit = LocalDate.now().plusDays(3).atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant();
                boolean hasPriorityO1 = o1.isGreen
                && o1.begin.isBefore(greenLimit);
                boolean hasPriorityO2 = o2.isGreen 
                && o2.begin.isBefore(greenLimit);
                if (hasPriorityO1 && hasPriorityO2) {
                    return o1.begin.compareTo(o2.begin);
                } else if (hasPriorityO1) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        greenDayFirstDeliveries.addAll(deliveries);
        for (DeliverySlot deliverySlot : greenDayFirstDeliveries) {
            schedule.add(new Pair<OffsetDateTime,Boolean>(
                deliverySlot.begin.atOffset(ZoneOffset.UTC),
                deliverySlot.isGreen)
            );
        }
        return schedule;
    }
}