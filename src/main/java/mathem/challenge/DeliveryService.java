package mathem.challenge;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * We need a delivery service to keep track of which "delivery slots" are
 * available for each days of the delivery period.
 */
public class DeliveryService {
    private Set<DeliverySlot> deliveries = new HashSet<DeliverySlot>();

    public DeliveryService(LocalDate begin, LocalDate end) {

    }

    /**
     * We overried Object.equals and Object.hashCode methods for DeliverySlot
     * to enable efficient HashSet operations
     */
    private class DeliverySlot {
        private final Instant begin;
        private final Instant end;

        private DeliverySlot(Instant begin, Instant end) {
            this.begin = begin;
            this.end = end;
        }

        private DeliverySlot(LocalDateTime datetime) {
            Instant begin = Instant.from(datetime);
            Instant end = Instant.from(datetime.plusHours(1));
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
            return deliverySlot.begin == this.begin
                && deliverySlot.end == this.end;
        }

        @Override public int hashCode() {
            return this.begin.hashCode() ^ this.end.hashCode();
        }
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
        Instant begin = Instant.from(day.atTime(LocalTime.MIDNIGHT));
        Instant end = Instant.from(day.plusDays(1).atTime(LocalTime.MIDNIGHT));
        Set<DeliverySlot> dayDeliveries = deliveries.stream()
        .filter(deliverySlot -> deliverySlot.overlaps(begin, end))
        .collect(Collectors.toSet());
        DeliverySlot slot = new DeliverySlot(LocalDateTime
        .from(day.atTime(8, 0)));
        // if (!dayDeliveries.contains(slot))
        return Optional.ofNullable(slot);
    }
}