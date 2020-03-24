package repository;

import domain.Basket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BasketRepository {

  Map<String, Basket> baskets;

  public BasketRepository() {
    this.baskets = new HashMap<>();
  }

  public Optional<Basket> getBasketByUserId(String userId) {
    return Optional.ofNullable(baskets.get(userId));
  }

  public void save(String userId, Basket basket) {
    baskets.put(userId, basket);
  }
}
