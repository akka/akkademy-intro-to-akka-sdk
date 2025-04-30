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

    var putResponse =
        httpClient.PUT("/carts/" + cartId + "/item").withRequestBody(akkaTshirt).invoke();

    Assertions.assertEquals(StatusCodes.OK, putResponse.status());

    var getResponse =
        httpClient.GET("/carts/" + cartId).responseBodyAs(ShoppingCart.class).invoke();

    Assertions.assertEquals(StatusCodes.OK, getResponse.status());
    Assertions.assertEquals(cartId, getResponse.body().cartId());
    Assertions.assertEquals(1, getResponse.body().items().size());
    Assertions.assertEquals(akkaTshirt, getResponse.body().items().get(0));
  }

  @Test
  public void deleteItemRemovesItemFromCartOverHttp() {
    String cartId = UUID.randomUUID().toString();
    var akkaTshirt = new LineItem("akka-tshirt", "Akka T-Shirt", 2);

    var putResponse =
        httpClient.PUT("/carts/" + cartId + "/item").withRequestBody(akkaTshirt).invoke();
    Assertions.assertEquals(StatusCodes.OK, putResponse.status());

    var deleteResponse =
        httpClient.DELETE("/carts/" + cartId + "/item/" + akkaTshirt.productId()).invoke();
    Assertions.assertEquals(StatusCodes.OK, deleteResponse.status());

    var getResponse =
        httpClient.GET("/carts/" + cartId).responseBodyAs(ShoppingCart.class).invoke();
    Assertions.assertEquals(StatusCodes.OK, getResponse.status());
    Assertions.assertEquals(cartId, getResponse.body().cartId());
    Assertions.assertTrue(getResponse.body().items().isEmpty());
  }

  @Test
  public void postCheckoutLocksTheCart() {
    String cartId = UUID.randomUUID().toString();
    var akkaTshirt = new LineItem("akka-tshirt", "Akka T-Shirt", 2);

    var putResponse =
        httpClient.PUT("/carts/" + cartId + "/item").withRequestBody(akkaTshirt).invoke();
    Assertions.assertEquals(StatusCodes.OK, putResponse.status());

    var checkoutResponse = httpClient.POST("/carts/" + cartId + "/checkout").invoke();
    Assertions.assertEquals(StatusCodes.OK, checkoutResponse.status());

    var getResponse =
        httpClient.GET("/carts/" + cartId).responseBodyAs(ShoppingCart.class).invoke();
    Assertions.assertEquals(StatusCodes.OK, getResponse.status());
    Assertions.assertTrue(getResponse.body().checkedOut());

    var rejectAdd =
        httpClient
            .PUT("/carts/" + cartId + "/item")
            .withRequestBody(new LineItem("new-product", "Extra Item", 1))
            .invoke();
    Assertions.assertEquals(StatusCodes.BAD_REQUEST, rejectAdd.status());

    var rejectRemove =
        httpClient.DELETE("/carts/" + cartId + "/item/" + akkaTshirt.productId()).invoke();
    Assertions.assertEquals(StatusCodes.BAD_REQUEST, rejectRemove.status());

    var rejectCheckout = httpClient.POST("/carts/" + cartId + "/checkout").invoke();
    Assertions.assertEquals(StatusCodes.BAD_REQUEST, rejectCheckout.status());
  }
}
