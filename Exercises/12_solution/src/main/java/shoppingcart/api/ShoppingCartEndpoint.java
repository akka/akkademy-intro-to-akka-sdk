package shoppingcart.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.annotations.http.Delete;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;

import shoppingcart.application.ShoppingCartEntity;
import shoppingcart.domain.ShoppingCart;
import shoppingcart.domain.ShoppingCart.LineItem;

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

    @Put("/{cartId}/item")
    public HttpResponse addItem(String cartId, LineItem item) {
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::addItem)
            .invoke(item);
        return HttpResponses.ok();
    }

    @Delete("/{cartId}/item/{productId}")
    public HttpResponse removeItem(String cartId, String productId) {
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::removeItem)
            .invoke(productId);
        return HttpResponses.ok();
    }

    @Post("/{cartId}/checkout")
    public HttpResponse checkout(String cartId) {
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::checkout)
            .invoke();
        return HttpResponses.ok();
    }
}