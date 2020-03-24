package Acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import domain.Basket;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BasketRepository;
import UI.Console;
import service.DateService;
import service.LogService;
import service.ProductService;
import service.ShoppingBasketService;

@ExtendWith(MockitoExtension.class)
public class ShoppingBasketAcceptance {

  private ShoppingBasketService service;

  @Mock
  private DateService dateService;

  public static final String USER_ID = UUID.randomUUID().toString();

  @Mock
  private Console console;

  @BeforeEach
  public void setUp() {
    BasketRepository basketRepo = new BasketRepository();
    ProductService productService = new ProductService();
    LogService logService = new LogService(console);

    service = new ShoppingBasketService(basketRepo, productService, dateService, logService);
  }


  @Test
  public void check_basket_contents() {
    service.addItem(USER_ID, "10002", 2);
    service.addItem(USER_ID, "20110", 5);

    Basket basket = service.basketFor(USER_ID);

    assertEquals(7, basket.size());
    assertEquals(2, basket.getQuantityForProduct("10002"));
    assertEquals(5, basket.getQuantityForProduct("20110"));
  }

  @Test
  public void check_basket_creation_date() {
    given(dateService.getDate()).willReturn("2020-07-12");
    service.addItem(USER_ID, "10002", 2);
    service.addItem(USER_ID, "20110", 5);

    Basket basket = service.basketFor(USER_ID);

    assertEquals(7, basket.size());
    assertEquals("2020-07-12", basket.getCreationDate());
    assertEquals(2, basket.getQuantityForProduct("10002"));
    assertEquals(5, basket.getQuantityForProduct("20110"));
  }

  @Test
  public void check_basket_total_amount() {
    given(dateService.getDate()).willReturn("2020-07-12");
    service.addItem(USER_ID, "10002", 2);
    service.addItem(USER_ID, "20110", 5);

    Basket basket = service.basketFor(USER_ID);

    assertEquals(45.00, basket.getTotal());
  }

  @Test
  public void logs_creation_date_when_basket_is_created() {
    given(dateService.getDate()).willReturn("2020-07-12");
    service.addItem(USER_ID, "20110", 5);

    service.basketFor(USER_ID);

    verify(console)
        .print(String.format("[BASKET CREATED]: Created[2020-07-12], User[%s]", USER_ID));
  }

  @Test
  public void logs_item_added_to_basket() {
    String basketCreation = "2020-07-12";
    String itemAddedDate = "2020-08-14";
    given(dateService.getDate()).willReturn(basketCreation, itemAddedDate);
    String productId = "20110";
    int quantity = 5;
    service.addItem(USER_ID, productId, quantity);

    service.basketFor(USER_ID);

    verify(console)
        .print(String.format(
            "[ITEM ADDED TO SHOPPING CART]: Added[%s], User[%s], Product[%s], Quantity[%s], Price[%s]",
            itemAddedDate, USER_ID, productId, quantity, 7));
  }

}
