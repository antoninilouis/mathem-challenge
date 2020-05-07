package mathem.challenge;

import java.io.UnsupportedEncodingException;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

/**
 * - productId
 * - name
 * - deliveryDays (a list of weekdays when the product can be delivered)
 * - productType (normal, external or temporary)
 * - daysInAdvance (how many days before delivery the products need to be
 *   ordered)
 */
public class Product {
    private final UUID productId;
    private final String productName;
    private final ProductType productType;
    private EnumSet<DayOfWeek> deliveryDays;
    private int daysInAdvance;

    public static final String MATHEM_NAMESPACE = "mathem.se";
    public static enum ProductType { NORMAL, EXTERNAL, TEMPORARY };

    /**
     * Constructor for Product object with all deliveryDays and default
     * days in advance constraint.
     * @param productId - the UUID (type 3) of the product
     * @param productName - the name of the product
     */
    private Product(UUID productId, String productName) {
        this.productId = productId;
        this.productName = productName;
        this.productType = ProductType.NORMAL;
        this.deliveryDays = EnumSet.allOf(DayOfWeek.class);
        this.daysInAdvance = 0;
    };

    /**
     * Constructor for Product object with specified deliveryDays and days in
     * advance constraint.
     * @param productId - the UUID (type 3) of the product
     * @param productName - the name of the product
     * @param productType - the type of the product
     * @param deliveryDays - the delivery days constraints of the product
     * @param daysInAdvance - the days in advance constraints of the product
     */
    private Product(UUID productId, String productName,
                    ProductType productType, EnumSet<DayOfWeek> deliveryDays,
                    int daysInAdvance) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.deliveryDays = deliveryDays;
        this.daysInAdvance = daysInAdvance;
    };

    /**
     * Static factory method for products with default constraints
     * @param name - the name of the product
     * @return an object of class Product initialized by the constructor for
     * Product objects with all deliveryDays and default
     */
    public static Product create(String name) {
        try {
            byte[] bytes = (MATHEM_NAMESPACE + name).getBytes("UTF-8");
            UUID uuid = UUID.nameUUIDFromBytes(bytes);
            return new Product(uuid, name);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Static factory method for products with constraints
     * @param name - the name of the product
     * @param productType - the type of the product
     * @param deliveryDays - the delivery days constraints of the product
     * @param daysInAdvance - the days in advance constraints of the product
     * @return an object of class Product initialized by the constructor for
     * Product objects with all deliveryDays and default
     */
    public static Product create(String name, ProductType productType,
                                 EnumSet<DayOfWeek> deliveryDays,
                                 int daysInAdvance) {
        try {
            byte[] bytes = (MATHEM_NAMESPACE + name).getBytes("UTF-8");
            UUID uuid = UUID.nameUUIDFromBytes(bytes);
            return new Product(uuid, name, productType, deliveryDays, daysInAdvance);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Validates the constraints of a product
     * @return a boolean value indicatinf the validity of the product
     * constraints
     */
    public boolean isValid() {
        return productType.equals(ProductType.NORMAL)
            || (productType.equals(ProductType.EXTERNAL)
                && daysInAdvance >= 5)
            || (productType.equals(ProductType.TEMPORARY)
                && Collections.disjoint(deliveryDays,
                   EnumSet.range(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)));
    }

    public UUID getProductId() {
        return this.productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public ProductType getProductType() {
        return this.productType;
    }

    public EnumSet<DayOfWeek> getDeliveryDays() {
        return this.deliveryDays;
    }

    public int getDaysInAdvance() {
        return this.daysInAdvance;
    }
}