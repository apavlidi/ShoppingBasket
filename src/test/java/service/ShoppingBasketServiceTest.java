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

  private static final String CREATION_DATE = "2020-07-12";
  public static final String USER_ID = UUID.randomUUID().toString();
  public static final int QUANTITY = 1;
  public static final String HOBBIT_PRODUCT_ID = UUID.randomUUID().toString();
  public static final Product THE_HOBBIT_PRODUCT = new Product(HOBBIT_PRODUCT_ID);

  @Mock
  private BasketRepository repository;

  @Mock
  private ProductService productService;

  @Mock
  private DateService dateService;

  @Mock
  private LogService logService;

  private ShoppingBasketService service;

  private ArgumentCaptor<Basket> basketArgumentCaptor;

  @BeforeEach
  void setUp() {
    service = new ShoppingBasketService(repository, productService, dateService, logService);
    basketArgumentCaptor = ArgumentCaptor.forClass(Basket.class);
  }

  @Test
  public void addItem_adds_to_repository() {
    Basket basket = new Basket(CREATION_DATE, USER_ID);
    given(productService.getProductById(HOBBIT_PRODUCT_ID)).willReturn(THE_HOBBIT_PRODUCT);
    given(repository.getBasketByUserId(USER_ID)).willReturn(Optional.of(basket));

    service.addItem(USER_ID, HOBBIT_PRODUCT_ID, QUANTITY);

    verify(repository).save(basketArgumentCaptor.capture());
    verify(productService).getProductById(HOBBIT_PRODUCT_ID);
    verify(repository).getBasketByUserId(USER_ID);
  }

  @Test
  public void addItem_adds_to_repository_with_basket_contains_the_product() {
    Basket basket = new Basket(CREATION_DATE, USER_ID);
    given(productService.getProductById(HOBBIT_PRODUCT_ID)).willReturn(THE_HOBBIT_PRODUCT);
    given(repository.getBasketByUserId(USER_ID)).willReturn(Optional.of(basket));

    service.addItem(USER_ID, HOBBIT_PRODUCT_ID, QUANTITY);

    verify(repository).save(basketArgumentCaptor.capture());

    Basket savedBasket = basketArgumentCaptor.getValue();
    assertEquals(THE_HOBBIT_PRODUCT, savedBasket.getProductById(HOBBIT_PRODUCT_ID));
    assertEquals(QUANTITY, savedBasket.getQuantityForProduct(HOBBIT_PRODUCT_ID));
  }

  @Test
  public void if_basket_does_not_exists_it_creates_one() {
    given(productService.getProductById(HOBBIT_PRODUCT_ID)).willReturn(THE_HOBBIT_PRODUCT);
    given(repository.getBasketByUserId(USER_ID)).willReturn(Optional.empty());

    service.addItem(USER_ID, HOBBIT_PRODUCT_ID, QUANTITY);

    verify(repository).save(basketArgumentCaptor.capture());

    Basket savedBasket = basketArgumentCaptor.getValue();
    assertEquals(THE_HOBBIT_PRODUCT, savedBasket.getProductById(HOBBIT_PRODUCT_ID));
    assertEquals(QUANTITY, savedBasket.getQuantityForProduct(HOBBIT_PRODUCT_ID));
  }

  @Test
  public void get_basket_for_userId_asks_repository() {
    given(repository.getBasketByUserId(USER_ID)).willReturn(Optional.of(new Basket(CREATION_DATE,
        USER_ID)));

    service.basketFor(USER_ID);

    verify(repository).getBasketByUserId(USER_ID);
  }

  @Test
  public void throws_basketNotFoundException_when_no_basket_is_found() {
    given(repository.getBasketByUserId(USER_ID)).willReturn(Optional.empty());

    assertThrows(BasketNotFoundException.class, () -> service.basketFor(USER_ID));
  }

  @Test
  public void sets_creation_date_of_basket() {
    given(productService.getProductById(HOBBIT_PRODUCT_ID)).willReturn(THE_HOBBIT_PRODUCT);
    given(dateService.getDate()).willReturn(CREATION_DATE);
    given(repository.getBasketByUserId(USER_ID)).willReturn(Optional.empty());

    service.addItem(USER_ID, HOBBIT_PRODUCT_ID, QUANTITY);

    verify(repository).save(basketArgumentCaptor.capture());
    Basket savedBasket = basketArgumentCaptor.getValue();

    assertEquals(CREATION_DATE, savedBasket.creationDate);
  }

  @Test
  public void logs_creation_date_on_new_basket() {
    given(productService.getProductById(HOBBIT_PRODUCT_ID)).willReturn(THE_HOBBIT_PRODUCT);
    given(dateService.getDate()).willReturn(CREATION_DATE);
    given(repository.getBasketByUserId(USER_ID)).willReturn(Optional.empty());

    service.addItem(USER_ID, HOBBIT_PRODUCT_ID, QUANTITY);

    verify(logService)
        .basketCreationDate(CREATION_DATE, USER_ID);
  }

}