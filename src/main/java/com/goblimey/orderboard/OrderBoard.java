package com.goblimey.orderboard;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.goblimey.orderboard.OrderType.BUY;

/**
 * The OrderBoard represents the order.  Each order contains a reference to a
 * TotalPrice object.  Orders with the same price share one of those and it
 * contains the total price of those orders.
 */
public class OrderBoard {

    private List<Order> orders;
    private Map<BigDecimal,Order> sellOrders;
    private Map<BigDecimal,Order> buyOrders;

    public OrderBoard() {
        orders = new ArrayList<>();
        sellOrders = new HashMap<>();
        buyOrders = new HashMap<>();

    }

    /**
     * Add an order to the board.  If there are other orders with the same price,
     * update the price.
     * @param order to be added.
     */
    public void addOrder(Order order) {

        orders.add(order);

        // Get the first order from the board with the same quantity and type (if any).
        Optional<Order> orderOnBoard = getOrderWithPrice(order);
        if (orderOnBoard.isPresent()) {
            // There are orders with this price.
            order.updateAndSetTotalQuantity(orderOnBoard.get());
        } else {
            // This is the first order with this price.
            // to the board.
            order.setNewTotalQuantity();
        }
        // Add the order to the board.
        if (order.getType() == BUY) {
            buyOrders.put(order.getPricePerKilo(), order);
        } else {
            sellOrders.put(order.getPricePerKilo(), order);
        }
    }

    /**
     * cancelOrder() removes an order from the board.
     * @param id of the order.
     */
    public void cancelOrder(int id) {
        Optional<Order> opOrder = findOrderInOrders(id);
        if (opOrder.isPresent()) {
            Order orderFromOrders = opOrder.get();
            opOrder = findOrderInBoard(orderFromOrders);
            if (opOrder.isPresent()) {
                Order orderFromBoard = opOrder.get();
                if (orderFromBoard.getQuantityInKilos().
                        compareTo(orderFromBoard.getTotalQuantity()) == -1) {
                    // Many orders have the same quantity and share this board entry.
                    orderFromBoard.decrementTotalQuantity(orderFromOrders.getQuantityInKilos());
                } else {
                    // This is the only order on the board with this quantity - remove it.
                    removeOrderFromBoard(orderFromBoard);
                }
            }
            removeOrderFromOrders(id);
        }
    }

    /**
     * Get a view of the board for display, a list of Strings each something like: "SELL 5.5 kg for £306".
     * @return the strings
     */
    public String[] displayLiveBoard() {
        Order[] orderExtract = sellOrders.values().stream().
                collect(Collectors.toMap(
                        order -> order.getTotalQuantity(),
                        order -> order,
                        (existingValue, newValue) -> existingValue)).values().toArray(new Order[0]);
        Stream sellOrders =
                Stream.of(orderExtract).sorted().
                map(order -> "SELL " + order.getTotalQuantity().toString() +
                        " kg for £" + order.getPricePerKilo());

        orderExtract = buyOrders.values().stream().
                collect(Collectors.toMap(
                        order -> order.getTotalQuantity(),
                        order -> order,
                        (existingValue, newValue) -> existingValue)).values().toArray(new Order[0]);
        // Sort buy orders descending.
        Stream buyOrders =
                Stream.of(orderExtract).sorted(Comparator.reverseOrder()).
                        map(order -> "BUY " + order.getTotalQuantity().toString() +
                                " kg for £" + order.getPricePerKilo());

        return (String[])Stream.concat(sellOrders, buyOrders).toArray(String[]::new);
    }

    protected final Map<BigDecimal,Order> getSellOrders() {
        return sellOrders;
    }

    protected final Map<BigDecimal,Order> getBuyOrders() {
        return buyOrders;
    }

    // Get the current state of the board.
    protected final List<Order> getOrders() {
        return orders;
    }

    // findOrderInOrders finds the order with the given ID in the orders list
    protected final Optional<Order> findOrderInOrders(int id) {
        for (Order order:orders) {
            if (order.getId() == id) {
                return Optional.of(order);
            }
        }
        return Optional.empty();
    }

    // findOrderInOrders finds the order with the given ID in the board.
    protected final Optional<Order> findOrderInBoard(Order order) {
        if (order.getType() == BUY) {
            return Optional.of(buyOrders.get(order.getPricePerKilo()));
        } else {
            return Optional.of(sellOrders.get(order.getPricePerKilo()));
        }
    }

    // getOrderWithPrice() gets an order from the board with the given price and type, if any.
    private Optional<Order> getOrderWithPrice(Order order) {
        if (order.getType() == BUY) {
            return buyOrders.values().stream().
                    filter(map -> map.getPricePerKilo().compareTo(order.getPricePerKilo()) == 0).findFirst();
        } else {
            return sellOrders.values().stream().
                    filter(map -> map.getPricePerKilo().compareTo(order.getPricePerKilo()) == 0).findFirst();
        }
    }

    // removeOrderFromBoard removes the order from the orders list.
    private void removeOrderFromOrders(int id) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() == id) {
                orders.remove(i);
                return;
            }
        }
    }

    // removeOrderFromBoard removes the order from the board.
    private void removeOrderFromBoard(Order order) {
        if (order.getType() == BUY) {
            buyOrders.remove(order.getPricePerKilo());
        } else {
            sellOrders.remove(order.getPricePerKilo());
        }
    }
}
