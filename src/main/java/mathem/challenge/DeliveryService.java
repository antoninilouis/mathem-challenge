package mathem.challenge;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * We need a delivery service to keep track of which "delivery slots" are
 * available for each days of the delivery period.
 */
public class DeliveryService {
    private static final int FIRST_DELIVERY_SLOT = 9;
    private static final int LAST_DELIVERY_SLOT = 19;
    private Set<DeliverySlot> deliveries = new HashSet<DeliverySlot>();
    private LocalDate periodBegin;
    private LocalDate periodEnd;

    public DeliveryService(LocalDate begin, LocalDate end) {
        periodBegin = begin;
        periodEnd = end;
    }

    /**
     * We override Object.equals and Object.hashCode methods for DeliverySlot
     * to enable efficient HashSet operations
     */
    private class DeliverySlot {
        private final Instant begin;
        private final Instant end;

        private DeliverySlot(Instant begin, Instant end) {
            this.begin = begin;
            this.end = end;
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

    /**
     * Returns a DeliverySlot object representing the next available delivery
     * slot for the given day
     * @param day - LocalDate object representing the day to look the delivery
     * slot from
     * @return a DeliverySlot object  representing the next available delivery
     * slot for the given day or null if none was found
     */
    private Optional<DeliverySlot> nextSlot(LocalDate day) {
        Instant begin = Instant.from(day.atStartOfDay()
        .atZone(ZoneId.systemDefault()));
        Instant end = Instant.from(day.plusDays(1).atStartOfDay()
        .atZone(ZoneId.systemDefault()));
        Set<DeliverySlot> dayDeliveries = deliveries.stream()
        .filter(deliverySlot -> deliverySlot.overlaps(begin, end))
        .collect(Collectors.toSet());
        DeliverySlot slot = null;
        for (int i = FIRST_DELIVERY_SLOT; i <= LAST_DELIVERY_SLOT; i++) {
            slot = new DeliverySlot(LocalDateTime
            .from(day.atTime(i, 0).atZone(ZoneId.systemDefault())));
            if (!dayDeliveries.contains(slot))
                return Optional.of(slot);
        }
        return Optional.ofNullable(slot);
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
}