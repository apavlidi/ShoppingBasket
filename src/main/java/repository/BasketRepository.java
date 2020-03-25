package repository;

import domain.Basket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BasketRepository {

  List<Basket> baskets;

  public BasketRepository() {
    this.baskets = new ArrayList<>();
  }

  public Optional<Basket> getBasketByUserId(String userId) {
    return baskets.stream()
        .filter(basket -> userId.equals(basket.userId))
        .findAny();
  }

  public void save(Basket basket) {
    baskets.add(basket);
  }
}
