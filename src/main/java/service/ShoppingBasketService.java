package service;

import domain.Basket;
import domain.Product;
import exceptions.BasketNotFoundException;
import java.util.function.Consumer;
import repository.BasketRepository;

public class ShoppingBasketService {

  private final BasketRepository basketRepo;
  private final ProductService productService;
  private DateService dateService;

  public ShoppingBasketService(BasketRepository basketRepo, ProductService productService,
      DateService dateService) {
    this.basketRepo = basketRepo;
    this.productService = productService;
    this.dateService = dateService;
  }

  public void addItem(String userId, String productId, int quantity) {
    basketRepo.getBasketByUserId(userId).ifPresentOrElse(
        addItemAndSave(userId, productId, quantity),
        createBasketAddItemAndSave(userId, productId, quantity));
  }

  private Consumer<Basket> addItemAndSave(String userId, String productId, int quantity) {
    return (basket) -> {
      addItemToBasket(productId, quantity, basket);
      basketRepo.save(userId, basket);
    };
  }

  private Runnable createBasketAddItemAndSave(String userId, String productId, int quantity) {
    return () -> {
      Basket basket = new Basket(dateService.getDate());
      addItemToBasket(productId, quantity, basket);
      basketRepo.save(userId, basket);
    };
  }

  private void addItemToBasket(String productId, int quantity, Basket basket) {
    Product product = productService.getProductById(productId);
    basket.addItem(product, quantity);
  }

  public Basket basketFor(String userId) {
    return basketRepo.getBasketByUserId(userId).orElseThrow(BasketNotFoundException::new);
  }
}
