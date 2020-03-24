package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import domain.Basket;
import domain.Product;
import exceptions.BasketNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BasketRepository;

@ExtendWith(MockitoExtension.class)
class ShoppingBasketServiceTest {

  @Mock
  BasketRepository repository;

  @Mock
  ProductService productService;
  private ShoppingBasketService service;

  @BeforeEach
  void setUp() {
    service = new ShoppingBasketService(repository, productService);
  }

  @Test
  public void addItem_adds_to_repository() {
    String userId = UUID.randomUUID().toString();
    String productId = UUID.randomUUID().toString();
    int quantity = 1;

    Product product = new Product(productId);
    Basket basket = new Basket();
    given(productService.getProductById(productId)).willReturn(product);
    given(repository.getBasketByUserId(userId)).willReturn(Optional.of(basket));

    service.addItem(userId, productId, quantity);

    verify(productService).getProductById(productId);
    verify(repository).getBasketByUserId(userId);

    ArgumentCaptor<Basket> basketArgument = ArgumentCaptor.forClass(Basket.class);
    ArgumentCaptor<String> userIdArgument = ArgumentCaptor.forClass(String.class);
    verify(repository).save(userIdArgument.capture(), basketArgument.capture());
    Basket savedBasket = basketArgument.getValue();
    assertEquals(product, savedBasket.getProductById(productId));
    assertEquals(quantity, savedBasket.getQuantityForProduct(productId));
  }

  @Test
  public void if_basket_does_not_exists_it_creates_one() {
    String userId = UUID.randomUUID().toString();
    String productId = UUID.randomUUID().toString();
    Product product = new Product(productId);
    int quantity = 1;

    given(productService.getProductById(productId)).willReturn(product);
    given(repository.getBasketByUserId(userId)).willReturn(Optional.empty());

    service.addItem(userId, productId, quantity);

    ArgumentCaptor<Basket> basketArgument = ArgumentCaptor.forClass(Basket.class);
    ArgumentCaptor<String> userIdArgument = ArgumentCaptor.forClass(String.class);
    verify(repository).save(userIdArgument.capture(), basketArgument.capture());
    Basket savedBasket = basketArgument.getValue();
    assertEquals(product, savedBasket.getProductById(productId));
    assertEquals(quantity, savedBasket.getQuantityForProduct(productId));
  }

  @Test
  public void get_basket_for_userId_asks_repository() {
    String userId = UUID.randomUUID().toString();
    given(repository.getBasketByUserId(userId)).willReturn(Optional.of(new Basket()));

    service.basketFor(userId);

    verify(repository).getBasketByUserId(userId);
  }

  @Test
  public void throws_basketNotFoundException_when_no_basket_is_found() {
    String userId = UUID.randomUUID().toString();
    given(repository.getBasketByUserId(userId)).willReturn(Optional.empty());

    assertThrows(BasketNotFoundException.class, () -> {
      service.basketFor(userId);
    });
  }

}