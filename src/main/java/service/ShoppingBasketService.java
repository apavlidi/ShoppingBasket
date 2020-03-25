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
  private LogService logService;

  public ShoppingBasketService(BasketRepository basketRepo, ProductService productService,
      DateService dateService, LogService logService) {
    this.basketRepo = basketRepo;
    this.productService = productService;
    this.dateService = dateService;
    this.logService = logService;
  }

  public void addItem(String userId, String productId, int quantity) {
    basketRepo.getBasketByUserId(userId).ifPresentOrElse(
        addItemAndSave(productId, quantity),
        createBasketAddItemAndSave(userId, productId, quantity));
  }

  private Consumer<Basket> addItemAndSave(String productId, int quantity) {
    return (basket) -> {
      addItemToBasket(productId, quantity, basket);
      basketRepo.save(basket);
    };
  }

  private Runnable createBasketAddItemAndSave(String userId, String productId, int quantity) {
    return () -> {
      Basket basket = createBasket(userId);
      addItemToBasket(productId, quantity, basket);
      basketRepo.save(basket);
    };
  }

  private Basket createBasket(String userId) {
    Basket basket = new Basket(dateService.getDate(), userId);
    logService.basketCreationDate(dateService.getDate(), userId);
    return basket;
  }

  private void addItemToBasket(String productId, int quantity, Basket basket) {
    Product product = productService.getProductById(productId);
    basket.addItem(product, quantity);
    logService.itemAdded(dateService.getDate(), basket.userId, productId, quantity,
        product.getPrice());
  }

  public Basket basketFor(String userId) {
    return basketRepo.getBasketByUserId(userId).orElseThrow(BasketNotFoundException::new);
  }

}
