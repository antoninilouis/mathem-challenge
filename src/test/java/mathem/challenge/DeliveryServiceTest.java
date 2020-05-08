package mathem.challenge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Test;

public class DeliveryServiceTest {
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
        DeliveryService ds = new DeliveryService(LocalDate.now(),
            LocalDate.now().plusDays(14));
        try {
            Method method = ds.getClass().getDeclaredMethod("nextSlot",
            new Class<?>[] { LocalDate.class });
            method.setAccessible(true);
            Object deliverySlot = createDeliverySlot(ds, LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(8, 0)));
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
        DeliveryService ds = new DeliveryService(LocalDate.now(),
            LocalDate.now().plusDays(14));
        try {
            Method method = ds.getClass().getDeclaredMethod("nextSlot",
            new Class<?>[] { LocalDate.class });
            method.setAccessible(true);
            Object deliverySlot = createDeliverySlot(ds, LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(8, 0)));
            assertTrue(ds.addSlot(LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(8, 0))));
            assertFalse(ds.addSlot(LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(8, 0))));
            Optional<?> o = (Optional<?>) invokeMethod(method, ds, LocalDate
            .now().plusDays(1));
            assertNotEquals(deliverySlot, o.get());
            assertNotNull(o.get());
            Object deliverySlot2 = createDeliverySlot(ds, LocalDateTime.from(
                LocalDate.now().plusDays(1).atTime(9, 0)));
            assertEquals(deliverySlot2, o.get());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}