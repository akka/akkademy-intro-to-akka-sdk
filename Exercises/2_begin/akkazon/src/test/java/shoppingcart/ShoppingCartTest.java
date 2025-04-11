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

    // --- Tests for onItemAdded ---

    @Test
    void itemIsAddedToEmptyCart() {
        var cart = new ShoppingCart("cart-1", List.of(), false);
        var item = new ShoppingCart.LineItem("p1", "Akka Hoodie", 2);
        var event = new ShoppingCartEvent.ItemAdded(item);

        var updatedCart = cart.onItemAdded(event);

        assertEquals(1, updatedCart.items().size());
        assertEquals("p1", updatedCart.items().get(0).productId());
        assertEquals(2, updatedCart.items().get(0).quantity());
    }

    @Test
    void itemQuantityIsIncreasedWhenAlreadyInCart() {
        var existingItem = new ShoppingCart.LineItem("p1", "Akka Hoodie", 2);
        var cart = new ShoppingCart("cart-1", List.of(existingItem), false);

        var additional = new ShoppingCart.LineItem("p1", "Akka Hoodie", 3);
        var event = new ShoppingCartEvent.ItemAdded(additional);

        var updatedCart = cart.onItemAdded(event);

        assertEquals(1, updatedCart.items().size());
        assertEquals("p1", updatedCart.items().get(0).productId());
        assertEquals(5, updatedCart.items().get(0).quantity());
    }

    @Test
    void cartImmutabilityIsPreserved() {
        var existingItem = new ShoppingCart.LineItem("p1", "Akka Hoodie", 1);
        var originalCart = new ShoppingCart("cart-1", List.of(existingItem), false);

        var event = new ShoppingCartEvent.ItemAdded(
            new ShoppingCart.LineItem("p1", "Akka Hoodie", 1));

        var updatedCart = originalCart.onItemAdded(event);

        // Original cart should remain unchanged
        assertEquals(1, originalCart.items().get(0).quantity());
        assertEquals(2, updatedCart.items().get(0).quantity());
    }

    @Test
    void itemsAreSortedByProductId() {
        var itemA = new ShoppingCart.LineItem("b-product", "Blue Jeans", 1);
        var cart = new ShoppingCart("cart-1", List.of(itemA), false);

        var event = new ShoppingCartEvent.ItemAdded(
            new ShoppingCart.LineItem("a-product", "Akka Hoodie", 2));

        var updatedCart = cart.onItemAdded(event);

        assertEquals(2, updatedCart.items().size());
        assertEquals("a-product", updatedCart.items().get(0).productId());
        assertEquals("b-product", updatedCart.items().get(1).productId());
    }
}