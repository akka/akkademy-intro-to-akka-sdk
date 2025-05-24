package shoppingcart.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shoppingcart.application.ShoppingCartEntity;
import shoppingcart.domain.ShoppingCart;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/carts")
public class ShoppingCartEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartEndpoint.class);

    private final ComponentClient componentClient;

    public ShoppingCartEndpoint(ComponentClient componentClient) {
        this.componentClient = componentClient;
    }

    // Method for handling GET and PUT will be added in the next lessons
}