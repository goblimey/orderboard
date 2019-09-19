package com.goblimey.orderboard;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.goblimey.orderboard.OrderType.BUY;
import static com.goblimey.orderboard.OrderType.SELL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderTests {

    @Test
    public void testCreateBuyOrder() throws Exception {
        final BigDecimal EXPECTED_QUANTITY = Order.createBigDecimal2DecimalPlaces("4.3");
        final BigDecimal EXPECTED_PRICE = Order.createBigDecimal2DecimalPlaces("3.0");
        final OrderType EXPECTED_ORDER_TYPE = OrderType.BUY;

        Order buyOrder = new Order(new BigDecimal("4.3"), new BigDecimal("3"), BUY, Optional.empty());

        assertEquals(0, buyOrder.getQuantityInKilos().compareTo(EXPECTED_QUANTITY));
        assertEquals(0, buyOrder.getPricePerKilo().compareTo(EXPECTED_PRICE));
        assertEquals(buyOrder.getType(), EXPECTED_ORDER_TYPE);
    }

    @Test
    public void testCreateSellOrder() throws Exception {
        final BigDecimal EXPECTED_QUANTITY = Order.createBigDecimal2DecimalPlaces("4.3");
        final BigDecimal EXPECTED_PRICE = Order.createBigDecimal2DecimalPlaces("3.0");
        final OrderType EXPECTED_ORDER_TYPE = OrderType.SELL;
        Order buyOrder = new Order(new BigDecimal("4.3"), new BigDecimal("3"), SELL, Optional.empty());

        assertEquals(0, buyOrder.getQuantityInKilos().compareTo(EXPECTED_QUANTITY));
        assertEquals(0, buyOrder.getPricePerKilo().compareTo(EXPECTED_PRICE));
        assertEquals(buyOrder.getType(), EXPECTED_ORDER_TYPE);
    }

    @Test
    public void testSetPrice() throws Exception {
        final BigDecimal EXPECTED_QUANTITY = Order.createBigDecimal2DecimalPlaces("4.3");
        final BigDecimal EXPECTED_PRICE = Order.createBigDecimal2DecimalPlaces("5.0");
        final OrderType EXPECTED_ORDER_TYPE = OrderType.BUY;

        Order buyOrder = new Order(new BigDecimal("4.3"), new BigDecimal("3"), BUY, Optional.empty());
        buyOrder.setPricePerKilo(EXPECTED_PRICE);

        assertEquals(buyOrder.getQuantityInKilos().compareTo(EXPECTED_QUANTITY), 0);
        assertEquals(buyOrder.getPricePerKilo().compareTo(EXPECTED_PRICE), 0);
        assertEquals(buyOrder.getType(), EXPECTED_ORDER_TYPE);
    }

    @Test
    public void testSetNewTotalQuantity() throws Exception {
        final BigDecimal EXPECTED_TOTAL_QUANTITY = Order.createBigDecimal2DecimalPlaces("4.3");

        Order buyOrder = new Order(new BigDecimal("4.3"), new BigDecimal("5"), BUY, Optional.empty());
        buyOrder.setNewTotalQuantity();

        assertEquals(0, buyOrder.getTotalQuantity().compareTo(EXPECTED_TOTAL_QUANTITY));
    }

    @Test
    public void testUpdateTotalPrice() throws Exception {
        final BigDecimal EXPECTED_TOTAL_QUANTITY = Order.createBigDecimal2DecimalPlaces("10.1");
        Order buyOrder1 = new Order(new BigDecimal("4.1"), new BigDecimal("5"), BUY, Optional.empty());
        buyOrder1.setNewTotalQuantity();
        Order buyOrder2 = new Order(new BigDecimal("6"), new BigDecimal("3"), OrderType.BUY, Optional.empty());
        buyOrder2.updateAndSetTotalQuantity(buyOrder1);

        assertTrue(buyOrder1.getTotalQuantity() == buyOrder2.getTotalQuantity());
        assertEquals(0, buyOrder1.getTotalQuantity().compareTo(EXPECTED_TOTAL_QUANTITY));

        Order sellOrder1 = new Order(new BigDecimal("4"), new BigDecimal("5"), SELL, Optional.empty());
        sellOrder1.setNewTotalQuantity();
        Order sellOrder2 = new Order(new BigDecimal("6.1"), new BigDecimal("3"), SELL, Optional.empty());
        sellOrder2.updateAndSetTotalQuantity(sellOrder1);

        assertTrue(sellOrder1.getTotalQuantity() == sellOrder2.getTotalQuantity());
        assertEquals(0, sellOrder1.getTotalQuantity().compareTo(EXPECTED_TOTAL_QUANTITY));
    }

    @Test(expected = NumberFormatException.class)
    public void testIllegalNumber() {
        BigDecimal junk = Order.createBigDecimal2DecimalPlaces("junk");
    }
}
