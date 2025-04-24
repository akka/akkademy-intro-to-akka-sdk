package shoppingcart;

import akka.http.javadsl.model.StatusCodes;
import akka.javasdk.testkit.TestKitSupport;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shoppingcart.application.ShoppingCartEntity;
import shoppingcart.domain.ShoppingCart;
import shoppingcart.domain.ShoppingCart.LineItem;

public class ShoppingCartIntegrationTest extends TestKitSupport {

  @Test
  public void getCartReturnsCartContentsOverHttp() {
    String cartId = UUID.randomUUID().toString();
    var akkaTshirt = new LineItem("akka-tshirt", "Akka T-Shirt", 2);

    // Add item to the cart using the component client
    var result =
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::addItem)
            .invoke(akkaTshirt);

    Assertions.assertNotNull(result);

    // Verify the cart via the GET endpoint
    var response = httpClient.GET("/carts/" + cartId).responseBodyAs(ShoppingCart.class).invoke();

    Assertions.assertEquals(StatusCodes.OK, response.status());
    Assertions.assertEquals(cartId, response.body().cartId());
    Assertions.assertEquals(1, response.body().items().size());
    Assertions.assertEquals(akkaTshirt, response.body().items().get(0));
  }

  @Test
  public void putItemAddsItemToCartOverHttp() {
    String cartId = UUID.randomUUID().toString();
    var akkaTshirt = new LineItem("akka-tshirt", "Akka T-Shirt", 3);

    // Add item to cart using the PUT HTTP endpoint
    var putResponse =
        httpClient.PUT("/carts/" + cartId + "/item").withRequestBody(akkaTshirt).invoke();

    Assertions.assertEquals(StatusCodes.OK, putResponse.status());

    // Verify the item was added using the GET HTTP endpoint
    var getResponse =
        httpClient.GET("/carts/" + cartId).responseBodyAs(ShoppingCart.class).invoke();

    Assertions.assertEquals(StatusCodes.OK, getResponse.status());
    Assertions.assertEquals(cartId, getResponse.body().cartId());
    Assertions.assertEquals(1, getResponse.body().items().size());
    Assertions.assertEquals(akkaTshirt, getResponse.body().items().get(0));
  }
}
