package domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasketTest {

  private Basket basket;
  private String productId;

  @BeforeEach
  void setUp() {
    basket = new Basket("10/10/2020", UUID.randomUUID().toString());
    productId = UUID.randomUUID().toString();
  }

  @Test
  public void returns_total_of_products_inside_the_basket() {
    basket.addItem(new Product(productId),3);

    assertEquals(3, basket.size());
  }

  @Test
  public void returns_total_amount_of_products_inside_the_basket() {
    basket.addItem(new Product(productId,"The Hobbit",5),3);

    assertEquals(15, basket.getTotal());
  }

}