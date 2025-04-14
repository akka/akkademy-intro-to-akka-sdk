package shoppingcart.application;

import akka.javasdk.testkit.EventSourcedTestKit;
import org.junit.jupiter.api.Test;
import shoppingcart.domain.ShoppingCart;
import shoppingcart.domain.ShoppingCartEvent;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartEntityTest {

    @Test
    void entityShouldStartWithEmptyCartState() {
        var testKit = EventSourcedTestKit.of(ShoppingCartEntity::new);
        
        // Query the current state (no commands yet)
        ShoppingCart state = testKit.getState();

        assertNotNull(state);
        assertEquals("testkit-entity-id", state.cartId());
        assertTrue(state.items().isEmpty());
        assertFalse(state.checkedOut());
    }
}