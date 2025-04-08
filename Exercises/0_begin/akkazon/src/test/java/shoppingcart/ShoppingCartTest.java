package shoppingcart.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {

    @Test
    void testShoppingCartFields() {
        var item1 = new ShoppingCart.LineItem("item-001", "Akka Hoodie", 2);
        var item2 = new ShoppingCart.LineItem("item-002", "Akka Mug", 1);
        var cart = new ShoppingCart("cart-123", List.of(item1, item2), false);

        assertEquals("cart-123", cart.cartId());
        assertEquals(2, cart.items().size());
        assertFalse(cart.checkedOut());

        var retrievedItem = cart.items().get(0);
        assertEquals("item-001", retrievedItem.productId());
        assertEquals("Akka Hoodie", retrievedItem.name());
        assertEquals(2, retrievedItem.quantity());
    }

    @Test
    void testLineItemWithQuantityCreatesNewInstance() {
        var original = new ShoppingCart.LineItem("item-003", "Akka T-shirt", 1);
        var updated = original.withQuantity(3);

        // Original should be unchanged
        assertEquals(1, original.quantity());

        // New instance should have updated quantity
        assertEquals(3, updated.quantity());

        // Other fields should remain the same
        assertEquals(original.productId(), updated.productId());
        assertEquals(original.name(), updated.name());

        // Ensure they are not the same object
        assertNotSame(original, updated);
    }

    @Test
    void testEmptyCartIsValid() {
        var emptyCart = new ShoppingCart("cart-empty", List.of(), false);

        assertEquals("cart-empty", emptyCart.cartId());
        assertTrue(emptyCart.items().isEmpty());
        assertFalse(emptyCart.checkedOut());
    }
}
