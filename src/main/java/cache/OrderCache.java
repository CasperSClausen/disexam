package cache;

import controllers.OrderController;
import model.Order;
import utils.Config;
import java.util.ArrayList;

//TODO: Build this cache and use it. (FIXED)
public class OrderCache {

    // List of orders
    private ArrayList<Order> orders;

    // Time cache should live. Definere en variabel vi kalder ttl. ttl = time-to-live.
    private long ttl;

    // Sets when the cache has been created
    private long created;

    // Vi har her lavet en konstruktør, hvor vi henter alle ordrerne fra config klassen
    public OrderCache() {
        this.ttl = Config.getOrderTtl();
    }

    public ArrayList<Order> getOrders(Boolean forceUpdate) {
        // If we wish to clear cache, we can set force update.
        // Otherwise we look at the age of the cache and figure out if we should update.
        // If the list is empty we also check for new orders
        if (forceUpdate
                || ((this.created + this.ttl) <= (System.currentTimeMillis() / 1000L))
                || this.orders.isEmpty()) {

            // Get orders from controller, since we wish to update.
            ArrayList<Order> orders = OrderController.getOrders();

            // Set orders for the instance and set created timestamp
            this.orders = orders;
            this.created = System.currentTimeMillis() / 1000L;
        }

        // Return the documents
        return this.orders;
    }
}