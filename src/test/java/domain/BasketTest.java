package domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class BasketTest {

  @Test
  public void returns_total_of_products_inside_the_basket() {
    Basket basket = new Basket("10/10/2020");
    String productId = UUID.randomUUID().toString();
    basket.addItem(new Product(productId),3);

    assertEquals(3, basket.size());
  }

  @Test
  public void returns_total_amount_of_products_inside_the_basket() {
    Basket basket = new Basket("10/10/2020");
    basket.addItem(new Product("10002","The Hobbit",5),3);

    assertEquals(15, basket.getTotal());
  }

}