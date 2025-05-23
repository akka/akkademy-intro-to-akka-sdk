package shoppingcart.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;

import shoppingcart.application.ShoppingCartEntity;
import shoppingcart.domain.ShoppingCart;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/carts")
public class ShoppingCartEndpoint {

    private final ComponentClient componentClient;

    public ShoppingCartEndpoint(ComponentClient componentClient) {
        this.componentClient = componentClient;
    }

    @Get("/{cartId}")
    public ShoppingCart getCart(String cartId) {
        return componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::getCart)
            .invoke();
    }
}