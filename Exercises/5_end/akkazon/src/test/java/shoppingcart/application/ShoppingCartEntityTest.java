package shoppingcart.application;

import akka.Done;
import akka.javasdk.testkit.EventSourcedTestKit;
import org.junit.jupiter.api.Test;
import shoppingcart.domain.ShoppingCart;
import shoppingcart.domain.ShoppingCart.LineItem;
import shoppingcart.domain.ShoppingCartEvent;

import static org.junit.jupiter.api.Assertions.*;
import static shoppingcart.domain.ShoppingCartEvent.ItemAdded;

import java.util.List;

public class ShoppingCartEntityTest {

    private final LineItem akkaTshirt = new LineItem("akka-tshirt", "Akka T-Shirt", 3);

    @Test
    void testInitialCartStateIsEmpty() {
        var testKit = EventSourcedTestKit.of(ShoppingCartEntity::new);
        var state = testKit.getState();

        assertNotNull(state);
        assertEquals("testkit-entity-id", state.cartId());
        assertTrue(state.items().isEmpty());
        assertFalse(state.checkedOut());
    }

    @Test
    void testAddItemPersistsEventAndReplies() {
        var testKit = EventSourcedTestKit.of(ShoppingCartEntity::new);

        {
            var result = testKit.method(ShoppingCartEntity::addItem).invoke(akkaTshirt);
            assertEquals(Done.getInstance(), result.getReply());

            var event = result.getNextEventOfType(ItemAdded.class);
            assertEquals("akka-tshirt", event.item().productId());
            assertEquals(3, event.item().quantity());
        }

        {
            assertEquals(1, testKit.getAllEvents().size());
        }
    }

    @Test
    void testRejectZeroQuantity() {
        var testKit = EventSourcedTestKit.of(ShoppingCartEntity::new);
        var invalidItem = akkaTshirt.withQuantity(0);

        var result = testKit.method(ShoppingCartEntity::addItem).invoke(invalidItem);

        assertTrue(result.isError());
        assertEquals("Quantity must be greater than zero.", result.getError());
    }

    @Test
    void applyEventShouldAddItemToState() {
        var testKit = EventSourcedTestKit.of(ShoppingCartEntity::new);

        var result = testKit.method(ShoppingCartEntity::addItem).invoke(akkaTshirt);

        var state = testKit.getState();
        assertEquals(1, state.items().size());

        var item = state.items().get(0);
        assertEquals("akka-tshirt", item.productId());
        assertEquals(3, item.quantity());
    }

    @Test
    void applyEventShouldAccumulateItemQuantities() {
        var testKit = EventSourcedTestKit.of(ShoppingCartEntity::new);

        testKit.method(ShoppingCartEntity::addItem).invoke(akkaTshirt);
        testKit.method(ShoppingCartEntity::addItem).invoke(akkaTshirt.withQuantity(2));

        var state = testKit.getState();
        assertEquals(1, state.items().size());

        var item = state.items().get(0);
        assertEquals("akka-tshirt", item.productId());
        assertEquals(5, item.quantity());
    }
}