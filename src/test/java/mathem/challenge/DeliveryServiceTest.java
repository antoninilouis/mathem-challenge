package mathem.challenge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

import org.junit.Test;

import mathem.challenge.Product.ProductType;

public class DeliveryServiceTest {
    private static final int FIRST_DELIVERY_SLOT = 9;
    private static final int LAST_DELIVERY_SLOT = 19;
    private static final int MAX_DELIVERIES = 1
    + LAST_DELIVERY_SLOT - FIRST_DELIVERY_SLOT;

    private static Object invokeMethod(Method m, Object t, Object... args) {
        Object res = null;
        try {
            res = m.invoke(t, args);
        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static Object createDeliverySlot(DeliveryService ds,
                                             LocalDateTime localDateTime) {
        Class<?>[] classes = ds.getClass().getDeclaredClasses();
        Object deliverySlot = null;
        for (Class<?> class1 : classes) {
            if (class1.getSimpleName().equals("DeliverySlot")) {
                Constructor<?> c;
                try {
                    c = class1.getDeclaredConstructor(new Class<?>[] 
                    { DeliveryService.class,LocalDateTime.class });
                    c.setAccessible(true);
                    try {
                        deliverySlot = c.newInstance(ds, localDateTime);
                    } catch (InstantiationException
                            | IllegalAccessException
                            | IllegalArgumentException
                            | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
        return deliverySlot;
    }

    @Test public void testDeliveryServiceBasic() {
        DeliveryService ds = new DeliveryService();
        try {
            Method method = ds.getClass().getDeclaredMethod("nextSlot",
            new Class<?>[] { LocalDate.class });
            method.setAccessible(true);
            Object deliverySlot = createDeliverySlot(ds, LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(FIRST_DELIVERY_SLOT, 0)));
            Optional<?> o = (Optional<?>) invokeMethod(method, ds, LocalDate
            .now().plusDays(1));
            assertEquals(deliverySlot, o.get());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Test public void testDeliveryServiceMedium() {
        DeliveryService ds = new DeliveryService();
        try {
            Method nextSlot = ds.getClass().getDeclaredMethod("nextSlot",
            new Class<?>[] { LocalDate.class });
            nextSlot.setAccessible(true);
            Object deliverySlot = createDeliverySlot(ds, LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(FIRST_DELIVERY_SLOT, 0)));
            assertTrue(ds.addSlot(LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(FIRST_DELIVERY_SLOT, 0))));
            assertFalse(ds.addSlot(LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(FIRST_DELIVERY_SLOT, 0))));
            Optional<?> o = (Optional<?>) invokeMethod(nextSlot, ds, LocalDate
            .now().plusDays(1));
            assertNotEquals(deliverySlot, o.get());
            assertNotNull(o.get());
            Object deliverySlot2 = createDeliverySlot(ds, LocalDateTime.from(
                LocalDate.now().plusDays(1)
                .atTime(FIRST_DELIVERY_SLOT + 1, 0)));
            assertEquals(deliverySlot2, o.get());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Test public void testAddSlot() {
        DeliveryService ds = new DeliveryService();
        try {
            ds.addSlot(LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(FIRST_DELIVERY_SLOT, 0)));
            ds.addSlot(LocalDateTime.from(
                LocalDate.now().plusDays(1)
                .atTime(FIRST_DELIVERY_SLOT + 1, 0)));
            assertEquals(2, ds.countDeliveries(LocalDate.now().plusDays(1)));
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Test public void testCountDeliveries() {
        DeliveryService ds = new DeliveryService();
        try {
            ds.addSlot(LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(FIRST_DELIVERY_SLOT, 0)));
            assertEquals(0, ds.countDeliveries(LocalDate.now()));
            assertEquals(1, ds.countDeliveries(LocalDate.now().plusDays(1)));
            for (int i = FIRST_DELIVERY_SLOT; i <= LAST_DELIVERY_SLOT; i++) {
                ds.addSlot(LocalDateTime.from(
                    LocalDate.now().plusDays(1).atTime(i, 0)));
            }
            assertEquals(MAX_DELIVERIES,
            ds.countDeliveries(LocalDate.now().plusDays(1)));
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Test public void testScheduleDelivery() {
        DeliveryService ds = new DeliveryService();
        try {
            Product dummy = Product.create("P1", ProductType.NORMAL,
                 EnumSet.allOf(DayOfWeek.class), 15);
            assertEquals(true, ds.scheduleDelivery(Arrays.asList(
                new LocalDate[] { LocalDate.now().plusDays(1) }), dummy));
            assertEquals(1, ds.countDeliveries());
            for (int i = FIRST_DELIVERY_SLOT; i <= LAST_DELIVERY_SLOT; i++) {
                ds.scheduleDelivery(Arrays.asList(
                    new LocalDate[] { LocalDate.now().plusDays(1) }), dummy);
            }
            assertEquals(MAX_DELIVERIES, ds.countDeliveries());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Test public void testScheduleDeliveryOverLimit() {
        DeliveryService ds = new DeliveryService();
        try {
            for (int i = FIRST_DELIVERY_SLOT; i <= LAST_DELIVERY_SLOT + 5; 
            i++) {
                Product dummy = Product.create("P1", ProductType.NORMAL,
                EnumSet.allOf(DayOfWeek.class), 15);
                ds.scheduleDelivery(Arrays.asList(
                    new LocalDate[] { LocalDate.now().plusDays(1) }), dummy);
            }
            assertEquals(MAX_DELIVERIES, ds.countDeliveries());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}