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
}