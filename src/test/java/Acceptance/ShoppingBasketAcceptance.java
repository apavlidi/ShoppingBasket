package Acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import domain.Basket;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import repository.BasketRepository;
import service.ProductService;
import service.ShoppingBasketService;

public class ShoppingBasketAcceptance {

  @Test
  public void check_basket_contents() {
    BasketRepository basketRepo = new BasketRepository();
    ProductService productService = new ProductService();
    ShoppingBasketService service = new ShoppingBasketService(basketRepo, productService);

    String userId = UUID.randomUUID().toString();
    service.addItem(userId, "10002", 2);
    service.addItem(userId, "20110", 5);

    Basket basket = service.basketFor(userId);

    assertEquals(7, basket.size());
    assertEquals(2,basket.getQuantityForProduct("10002"));
    assertEquals(5,basket.getQuantityForProduct("20110"));
  }

}
