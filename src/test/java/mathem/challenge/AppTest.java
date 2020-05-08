package mathem.challenge;

import org.junit.Test;

import mathem.challenge.Product.ProductType;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.util.EnumSet;

public class AppTest {
    @Test public void testCreateDefaultProduct() {
        Product p1 = Product.create("p1");
        assertNotNull(p1.getProductId());
        assertEquals(p1.getProductName(), "p1");
        assertEquals(p1.getProductType(), ProductType.NORMAL);
        assertEquals(p1.getDeliveryDays(), EnumSet.allOf(DayOfWeek.class));
        assertEquals(p1.getDaysInAdvance(), 0);
    }

    @Test public void testCreateProduct() {
        Product p1 = Product.create("p2", ProductType.TEMPORARY,
           EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), 2);
        assertNotNull(p1.getProductId());
        assertEquals(p1.getProductName(), "p2");
        assertEquals(p1.getProductType(), ProductType.TEMPORARY);
        assertEquals(p1.getDeliveryDays(),
        EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
        assertEquals(p1.getDaysInAdvance(), 2);
    }

    @Test public void testIsValid() {
        Product p1 = Product.create("p1");
        Product p2 = Product.create("p2", ProductType.TEMPORARY,
        EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), 2);
        Product p3 = Product.create("p2", ProductType.TEMPORARY,
        EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.FRIDAY), 2);
        assertTrue(p1.isValid());
        assertFalse(p2.isValid());
        assertTrue(p3.isValid());
    }
}
