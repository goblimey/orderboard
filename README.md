# Live Order Board Exercise

## Building and Running
    gradle build
    gradle test

The test report is in build/reports/tests/test

The unit test testOrderBoard.testLiveBoard() produces and checks the results described in the spec.

Test coverage:

    gradle jacocoTestreport

the report is in build/reports/jacoco/test/html.

##General Principals

The order board is represented by an array of Strings.
each String starts with "BUY" or "SELL",
followed by the data mentioned in the spec.
 
When the spec says "orders for the same price should be merged together",
I assume that buy and sell orders must be kept separate.
To achieve that I created two separate maps buyOrders and sellOrders in OrderBoard.
These each hold merged and unmerged orders, keyed on price.
Using separate objects leads to a bit of repeated code.
An alternative solution would be to use the order type and
the price as a key.

To produce the display of the live order board,
I scan buyOrders and sellOrders
and extract the necessary data.
 
I hold a separate list of orders as an ArrayList,
representing the original unmerged orders.
This is needed to support cancellation of an order.
If three orders are merged,
there are three entries in this list and one of them also appears in
buyOrders or sellOrders. 
 
Each order
contains a reference to a BigDecimal totalPrice
which stores the total price of a set of merged orders.
Merged orders share a single totalPrice object.
Other orders each have a totalPrice object of their own.

If some orders are merged together and one of them is then cancelled,
the total price of the surviving orders
is decremented.

The problem doesn't specify checking of user IDs
so I didn't bother to produce a user table.

Using a BigDecimal as a map key
concerned me a bit because the natural order for that class
is not consistent with equals.
however, Map comparisons use compareTo,
which compares the value, not the scale.
I created a gadget in the Orders class to create these BigDecimals,
which is used everywhere.
It sets the precision and the scale to the same values throughout.

##Testing
Coverage is good - see the report.

For testing I favour stubs over mocks.

I make methods protected final when they
could otherwise be private to allow unit tests to call them.
This allows me to test them thoroughly.
