package service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import UI.Console;
import domain.Basket;

import exceptions.BasketNotFoundException;
import domain.Product;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BasketRepository;

@ExtendWith(MockitoExtension.class)
class ShoppingBasketServiceTest {

  private static final String CREATION_DATE = "2020-07-12";
  public static final String USER_ID = UUID.randomUUID().toString();

  @Mock
  private Console console;

  @Mock
  private BasketRepository repository;

  @Mock
  private ProductService productService;

  @Mock
  private DateService dateService;

  @Mock
  private LogService logService;

  private ShoppingBasketService service;

  @BeforeEach
  void setUp() {
    service = new ShoppingBasketService(repository, productService, dateService, logService);
  }

  @Test
  public void addItem_adds_to_repository() {
    String userId = UUID.randomUUID().toString();
    String productId = UUID.randomUUID().toString();
    int quantity = 1;

    Product product = new Product(productId);
    Basket basket = new Basket(CREATION_DATE, userId);
    given(productService.getProductById(productId)).willReturn(product);
    given(repository.getBasketByUserId(userId)).willReturn(Optional.of(basket));

    service.addItem(userId, productId, quantity);

    verify(productService).getProductById(productId);
    verify(repository).getBasketByUserId(userId);

    ArgumentCaptor<Basket> basketArgument = ArgumentCaptor.forClass(Basket.class);
    ArgumentCaptor<String> userIdArgument = ArgumentCaptor.forClass(String.class);
    verify(repository).save(basketArgument.capture());
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
    verify(repository).save(basketArgument.capture());
    Basket savedBasket = basketArgument.getValue();
    assertEquals(product, savedBasket.getProductById(productId));
    assertEquals(quantity, savedBasket.getQuantityForProduct(productId));
  }

  @Test
  public void get_basket_for_userId_asks_repository() {
    String userId = UUID.randomUUID().toString();
    given(repository.getBasketByUserId(userId)).willReturn(Optional.of(new Basket(CREATION_DATE,
        userId)));

    service.basketFor(userId);

    verify(repository).getBasketByUserId(userId);
  }

  @Test
  public void throws_basketNotFoundException_when_no_basket_is_found() {
    String userId = UUID.randomUUID().toString();
    given(repository.getBasketByUserId(userId)).willReturn(Optional.empty());

    assertThrows(BasketNotFoundException.class, () -> service.basketFor(userId));
  }

  @Test
  public void sets_creation_date_of_basket() {
    String userId = UUID.randomUUID().toString();
    String productId = UUID.randomUUID().toString();
    Product product = new Product(productId);
    int quantity = 1;

    given(productService.getProductById(productId)).willReturn(product);
    given(dateService.getDate()).willReturn("2020-07-12");
    given(repository.getBasketByUserId(userId)).willReturn(Optional.empty());

    service.addItem(userId, productId, quantity);

    ArgumentCaptor<Basket> basketArgument = ArgumentCaptor.forClass(Basket.class);
    ArgumentCaptor<String> userIdArgument = ArgumentCaptor.forClass(String.class);
    verify(repository).save(basketArgument.capture());
    Basket savedBasket = basketArgument.getValue();

    assertEquals("2020-07-12", savedBasket.getCreationDate());
  }

  @Test
  public void logs_creation_date_on_new_basket() {
    String userId = UUID.randomUUID().toString();
    String productId = UUID.randomUUID().toString();
    Product product = new Product(productId);
    int quantity = 1;

    given(productService.getProductById(productId)).willReturn(product);
    given(dateService.getDate()).willReturn("2020-07-12");
    given(repository.getBasketByUserId(userId)).willReturn(Optional.empty());

    service.addItem(userId, productId, quantity);

    verify(logService)
        .basketCreationDate("2020-07-12", userId);
  }

}