package com.goblimey.orderboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Order represents a buy or sell order.  Orders with the same price and type share a value
 * which is the total price of those orders.  The method setNewTotalQuantity() creates
 * and sets a new value, updateAndSetTotalQuantity() updates a (presumably shared) value
 * with the quantity from this order and sets that value as its total.  When another class
 * destroys an order, it should decrement the total price.
 */
public class Order implements Comparable<Order> {
    private Integer id;
    private BigDecimal quantityInKilos;
    private BigDecimal pricePerKilo;
    private OrderType type;
    private BigDecimal totalQuantity;

    // Used to generate unique IDs.
    private static int nextId = 0;

    public Order(BigDecimal quantityInKilos, BigDecimal pricePerKilo, OrderType type, Optional<BigDecimal> totalQuantity) {

        this.id = getNextId();

        this.quantityInKilos = createBigDecimal2DecimalPlaces(quantityInKilos);

        this.pricePerKilo = createBigDecimal2DecimalPlaces(pricePerKilo);

        this.type = type;

        if (totalQuantity.isPresent()) {
            this.totalQuantity = totalQuantity.get();
        }
    }

    public Integer getId() {
        return id;
    }

    public BigDecimal getQuantityInKilos() {
        return quantityInKilos;
    }

    public BigDecimal getPricePerKilo() {
        return pricePerKilo;
    }

    public OrderType getType() {
        return type;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setPricePerKilo(BigDecimal pricePerKilo) {
        this.pricePerKilo = pricePerKilo;
    }

    /**
     * Create and set a new totalQuantity using the order's quantity.
     */
    public void setNewTotalQuantity() {
        this.totalQuantity = createBigDecimal2DecimalPlaces(this.getQuantityInKilos());
    }

    public void updateAndSetTotalQuantity(Order order) {
        order.incrementTotalQuantity(this.getQuantityInKilos());
        this.totalQuantity = order.totalQuantity;
    }

    @Override
    public int compareTo(Order order) {
        return this.pricePerKilo.compareTo(order.pricePerKilo);
    }

    public void decrementTotalQuantity(BigDecimal amount) {
        this.totalQuantity = this.totalQuantity.subtract(amount);
    }

    // Converts a numeric string to a BigDecimal with 2 decimal place accuracy.
    // For example "2.0" -> 2.00, "2.005" -> 2.01.
    // This is protected and static so that it can be used in unit tests.
    protected static BigDecimal createBigDecimal2DecimalPlaces(String value)
            throws NumberFormatException {
        try {
            // Enforce a consistent precision so that comparisons work sensibly.
            BigDecimal result = new BigDecimal(value);
            result.setScale(2,  RoundingMode.HALF_UP);
            return result;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("the value must be a number");
        }
    }


    // Create a new BigDecimal with the same value as the given BigDecimal and
    // apply the context.
    private BigDecimal createBigDecimal2DecimalPlaces(BigDecimal value)
            throws NumberFormatException {
            // Enforce a consistent precision so that comparisons work sensibly.
            BigDecimal result = new BigDecimal(value.toString());
            result.setScale(2,  RoundingMode.HALF_UP);
            return result;
    }

    private int getNextId() {
        return nextId++;
    }

    private void incrementTotalQuantity(BigDecimal amount) {
        this.totalQuantity = this.totalQuantity.add(amount);
    }
}
