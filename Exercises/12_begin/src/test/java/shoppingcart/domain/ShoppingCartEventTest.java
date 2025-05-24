package shoppingcart.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartEventTest {

    @Test
    void itemAddedEventShouldWrapLineItemCorrectly() {
        // Arrange
        ShoppingCart.LineItem item = new ShoppingCart.LineItem("product-123", "Akka Hoodie", 2);

        // Act
        ShoppingCartEvent.ItemAdded event = new ShoppingCartEvent.ItemAdded(item);

        // Assert
        assertNotNull(event);
        assertEquals("product-123", event.item().productId());
        assertEquals("Akka Hoodie", event.item().name());
        assertEquals(2, event.item().quantity());
    }

    @Test
    void itemRemovedEventShouldWrapProductIdCorrectly() {
        // Arrange
        String productId = "product-456";

        // Act
        ShoppingCartEvent.ItemRemoved event = new ShoppingCartEvent.ItemRemoved(productId);

        // Assert
        assertNotNull(event);
        assertEquals("product-456", event.productId());
    }

    @Test
    void checkedOutEventShouldCreateSuccessfully() {
        // Act
        ShoppingCartEvent.CheckedOut event = new ShoppingCartEvent.CheckedOut();

        // Assert
        assertNotNull(event);
    }
}