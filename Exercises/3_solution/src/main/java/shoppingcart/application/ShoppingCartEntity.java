package shoppingcart.application;

import akka.javasdk.annotations.ComponentId;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import shoppingcart.domain.ShoppingCart;
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
        // Will implement later
        return currentState();
    }
}