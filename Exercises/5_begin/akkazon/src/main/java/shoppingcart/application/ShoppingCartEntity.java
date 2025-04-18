package shoppingcart.application;

import akka.Done;
import akka.javasdk.annotations.ComponentId;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import shoppingcart.domain.ShoppingCart;
import shoppingcart.domain.ShoppingCart.LineItem;
import shoppingcart.domain.ShoppingCartEvent;

import java.util.Collections;

@ComponentId("shopping-cart")
public class ShoppingCartEntity extends EventSourcedEntity<ShoppingCart, ShoppingCartEvent> {

    private final String entityId;

    public ShoppingCartEntity(EventSourcedEntityContext context) {
        this.entityId = context.entityId();
    }

    @Override
    public ShoppingCart emptyState() {
        return new ShoppingCart(entityId, Collections.emptyList(), false);
    }

    @Override
    public ShoppingCart applyEvent(ShoppingCartEvent event) {
        // Event handling will be implemented in the next lesson
        return currentState();
    }

    public Effect<Done> addItem(LineItem item) {
        if (currentState().checkedOut()) {
            return effects().error("Cart is already checked out.");
        }
        if (item.quantity() <= 0) {
            return effects().error("Quantity must be greater than zero.");
        }

        var event = new ShoppingCartEvent.ItemAdded(item);
        return effects()
            .persist(event)
            .thenReply(newState -> Done.getInstance());
    }
}