package repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import domain.Basket;
import domain.Product;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasketRepositoryTest {

  private BasketRepository repo;
  public static final String USER_ID = UUID.randomUUID().toString();
  private Basket basket;

  @BeforeEach
  void setUp() {
    repo = new BasketRepository();
    basket = new Basket("10/10/2020", USER_ID);
  }

  @Test
  public void retrieve_basket_by_userId() {
    repo.save(basket);

    assertEquals(basket, repo.getBasketByUserId(USER_ID).get());
  }

  @Test
  public void saves_basket_based_on_userId() {
    basket.addItem(new Product(UUID.randomUUID().toString()),1);

    repo.save(basket);

    assertEquals(basket, repo.getBasketByUserId(USER_ID).get());
  }

}