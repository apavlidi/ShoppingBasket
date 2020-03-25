package Acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import domain.Basket;

import java.text.DecimalFormat;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BasketRepository;
import UI.Console;
import service.DateService;
import service.LogService;
import service.ProductService;
import service.ShoppingBasketService;

@ExtendWith(MockitoExtension.class)
public class ShoppingBasketAcceptance {

  private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
  private static final String USER_ID = UUID.randomUUID().toString();
  private static final String THE_HOBBIT_PRODUCT_ID = "10002";
  private static final String BREAKING_BAD_PRODUCT_ID = "20110";

  @Mock
  private DateService dateService;

  @Mock
  private Console console;

  private ShoppingBasketService service;

  @BeforeEach
  public void setUp() {
    service = new ShoppingBasketService(new BasketRepository(), new ProductService(), dateService,
        new LogService(console));
  }

  @Test
  public void check_basket_contents() {
    service.addItem(USER_ID, THE_HOBBIT_PRODUCT_ID, 2);
    service.addItem(USER_ID, BREAKING_BAD_PRODUCT_ID, 5);

    Basket basket = service.basketFor(USER_ID);

    assertEquals(7, basket.size());
    assertEquals(2, basket.getQuantityForProduct(THE_HOBBIT_PRODUCT_ID));
    assertEquals(5, basket.getQuantityForProduct(BREAKING_BAD_PRODUCT_ID));
  }

  @Test
  public void check_basket_creation_date() {
    given(dateService.getDate()).willReturn("2020-07-12");
    service.addItem(USER_ID, THE_HOBBIT_PRODUCT_ID, 2);
    service.addItem(USER_ID, BREAKING_BAD_PRODUCT_ID, 5);

    Basket basket = service.basketFor(USER_ID);

    assertEquals(7, basket.size());
    assertEquals("2020-07-12", basket.creationDate);
    assertEquals(2, basket.getQuantityForProduct(THE_HOBBIT_PRODUCT_ID));
    assertEquals(5, basket.getQuantityForProduct(BREAKING_BAD_PRODUCT_ID));
  }

  @Test
  public void check_basket_total_amount() {
    given(dateService.getDate()).willReturn("2020-07-12");
    service.addItem(USER_ID, THE_HOBBIT_PRODUCT_ID, 2);
    service.addItem(USER_ID, BREAKING_BAD_PRODUCT_ID, 5);

    Basket basket = service.basketFor(USER_ID);

    assertEquals(45.00, basket.getTotal());
  }

  @Test
  public void logs_creation_date_when_basket_is_created() {
    given(dateService.getDate()).willReturn("2020-07-12");
    service.addItem(USER_ID, BREAKING_BAD_PRODUCT_ID, 5);

    service.basketFor(USER_ID);

    verify(console).print(String.format("[BASKET CREATED]: Created[2020-07-12], User[%s]", USER_ID));
  }

  @Test
  public void logs_item_added_to_basket() {
    int quantity = 5;
    String basketCreation = "2020-07-12";
    String itemAddedDate = "2020-08-14";
    given(dateService.getDate()).willReturn(basketCreation, itemAddedDate);

    service.addItem(USER_ID, BREAKING_BAD_PRODUCT_ID, quantity);

    InOrder inOrder = Mockito.inOrder(console);

    inOrder.verify(console).print(String.format("[BASKET CREATED]: Created[2020-08-14], User[%s]", USER_ID));
    inOrder.verify(console).print(String.format(
        "[ITEM ADDED TO SHOPPING CART]: Added[%s], User[%s], Product[%s], Quantity[%s], Price[£%s]",
        itemAddedDate, USER_ID, BREAKING_BAD_PRODUCT_ID, quantity, decimalFormat.format(7)));
  }

}
