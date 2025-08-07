package shoppingcart.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public record ShoppingCart(String cartId, List<LineItem> items, boolean checkedOut) {

    public record LineItem(String productId, String name, int quantity) {
        public LineItem withQuantity(int quantity) {
            return new LineItem(productId, name, quantity);
        }
    }

    public ShoppingCart onItemAdded(ShoppingCartEvent.ItemAdded itemAdded) {
        var item = itemAdded.item();
        LineItem updatedItem = items.stream()
            .filter(i -> i.productId().equals(item.productId()))
            .findFirst()
            .map(existing -> existing.withQuantity(existing.quantity() + item.quantity()))
            .orElse(item);

        List<LineItem> updatedItems = items.stream()
            .filter(i -> !i.productId().equals(item.productId()))
            .collect(Collectors.toList());

        updatedItems.add(updatedItem);
        updatedItems.sort(Comparator.comparing(LineItem::productId));

        return new ShoppingCart(cartId, updatedItems, checkedOut);
    }

    public ShoppingCart onItemRemoved(ShoppingCartEvent.ItemRemoved itemRemoved) {
        List<LineItem> updatedItems = items.stream()
            .filter(lineItem -> !lineItem.productId().equals(itemRemoved.productId()))
            .collect(Collectors.toList());

        return new ShoppingCart(cartId, updatedItems, checkedOut);
    }

    public ShoppingCart onCheckedOut() {
        return new ShoppingCart(cartId, items, true);
    }
}