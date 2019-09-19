package com.goblimey.orderboard;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.goblimey.orderboard.OrderType.BUY;
import static com.goblimey.orderboard.OrderType.SELL;
import static org.junit.Assert.*;

public class OrderBoardTests {

    OrderBoard board;
    List<Order> testOrders;


    @Before
    public void setUp() throws Exception {
        testOrders = new ArrayList<Order>();
        Order order = new Order(new BigDecimal("3.5"), new BigDecimal("306"), SELL, Optional.empty());
        testOrders.add(order);
        order = new Order(new BigDecimal("1.2"), new BigDecimal("310"), SELL, Optional.empty());
        testOrders.add(order);
        order = new Order(new BigDecimal("1.5"), new BigDecimal("307"), SELL, Optional.empty());
        testOrders.add(order);
        order = new Order(new BigDecimal("2"), new BigDecimal("306"), SELL, Optional.empty());
        testOrders.add(order);
        order = new Order(new BigDecimal("1"), new BigDecimal("308"), BUY, Optional.empty());
        testOrders.add(order);
        order = new Order(new BigDecimal("2"), new BigDecimal("310"), BUY, Optional.empty());
        testOrders.add(order);
        order = new Order(new BigDecimal("3"), new BigDecimal("310"), BUY, Optional.empty());
        testOrders.add(order);

        board = new OrderBoard();
        for (Order o: testOrders) {
            board.addOrder(o);
        }
    }

    @Test
    public void testBoardSize() throws Exception {
        List<Order> orders = board.getOrders();
        assertEquals(7, orders.size());
        Map<BigDecimal,Order> retrievedBoard = board.getSellOrders();
        assertEquals(3, retrievedBoard.size());
        retrievedBoard = board.getBuyOrders();
        assertEquals(2, retrievedBoard.size());

        String[] totalisedOrders = board.displayLiveBoard();
        assertEquals(5, totalisedOrders.length);
    }

    @Test
    public void testMergedOrders() {
        assertTrue(testOrders.get(0).getTotalQuantity() == testOrders.get(3).getTotalQuantity());
        BigDecimal expectedQuantity = Order.createBigDecimal2DecimalPlaces("5.5");
        assertEquals(expectedQuantity, testOrders.get(0).getTotalQuantity());

        assertTrue(testOrders.get(5).getTotalQuantity() == testOrders.get(6).getTotalQuantity());
        expectedQuantity = Order.createBigDecimal2DecimalPlaces("5");
        assertEquals(expectedQuantity, testOrders.get(5).getTotalQuantity());
    }


    @Test
    public void testLiveBoard() {
        String[] liveBoard = board.displayLiveBoard();
        assertEquals("SELL 5.5 kg for £306", liveBoard[0]);
        assertEquals("SELL 1.5 kg for £307", liveBoard[1]);
        assertEquals("SELL 1.2 kg for £310", liveBoard[2]);
        assertEquals("BUY 5 kg for £310", liveBoard[3]);
        assertEquals("BUY 1 kg for £308", liveBoard[4]);
    }

    @Test
    public void testCancelOrderNotMerged() {
        Optional<Order> order = board.findOrderInOrders(testOrders.get(1).getId());
        assertTrue(order.isPresent());

        board.cancelOrder(testOrders.get(1).getId());
        order = board.findOrderInOrders(testOrders.get(1).getId());
        assertFalse(order.isPresent());

        List<Order> orders = board.getOrders();
        assertEquals(6, orders.size());
        Map<BigDecimal,Order> retrievedBoard = board.getSellOrders();
        assertEquals(2, retrievedBoard.size());
        retrievedBoard = board.getBuyOrders();
        assertEquals(2, retrievedBoard.size());

        order = board.findOrderInOrders(testOrders.get(4).getId());
        assertTrue(order.isPresent());

        board.cancelOrder(testOrders.get(4).getId());
        order = board.findOrderInOrders(testOrders.get(4).getId());
        assertFalse(order.isPresent());

        orders = board.getOrders();
        assertEquals(5, orders.size());
        retrievedBoard = board.getSellOrders();
        assertEquals(2, retrievedBoard.size());
        retrievedBoard = board.getBuyOrders();
        assertEquals(1, retrievedBoard.size());
    }

    @Test
    public void testCancelOrderMerged() {
        Optional<Order> opOrder = board.findOrderInOrders(testOrders.get(0).getId());
        assertTrue(opOrder.isPresent());

        // Remove one of the merged sell orders and check that it's gone.
        board.cancelOrder(testOrders.get(0).getId());
        opOrder = board.findOrderInOrders(testOrders.get(0).getId());
        assertFalse(opOrder.isPresent());

        // Check that the remaining sell order has been decremented.
        opOrder = board.findOrderInOrders(testOrders.get(3).getId());
        assertTrue(opOrder.isPresent());
        BigDecimal expectedTotalQuantity = Order.createBigDecimal2DecimalPlaces("2.0") ;
        assertEquals(expectedTotalQuantity, opOrder.get().getTotalQuantity());

        List<Order> orders = board.getOrders();
        assertEquals(6, orders.size());
        Map<BigDecimal,Order> retrievedBoard = board.getSellOrders();
        assertEquals(3, retrievedBoard.size());
        retrievedBoard = board.getBuyOrders();
        assertEquals(2, retrievedBoard.size());

        opOrder = board.findOrderInOrders(testOrders.get(5).getId());
        assertTrue(opOrder.isPresent());

        board.cancelOrder(testOrders.get(5).getId());
        opOrder = board.findOrderInOrders(testOrders.get(5).getId());
        assertFalse(opOrder.isPresent());
        // Check that the remaining buy order has been decremented.
        opOrder = board.findOrderInOrders(testOrders.get(6).getId());
        assertTrue(opOrder.isPresent());
        expectedTotalQuantity = Order.createBigDecimal2DecimalPlaces("3") ;
        assertEquals(expectedTotalQuantity, opOrder.get().getTotalQuantity());

        orders = board.getOrders();
        assertEquals(5, orders.size());
        retrievedBoard = board.getSellOrders();
        assertEquals(3, retrievedBoard.size());
        retrievedBoard = board.getBuyOrders();
        assertEquals(2, retrievedBoard.size());
    }
}
