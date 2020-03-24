package Acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import domain.Basket;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BasketRepository;
import service.DateService;
import service.ProductService;
import service.ShoppingBasketService;

@ExtendWith(MockitoExtension.class)
public class ShoppingBasketAcceptance {

  private ShoppingBasketService service;

  @Mock
  private DateService dateService;

  public static final String USER_ID = UUID.randomUUID().toString();

  @BeforeEach
  public void setUp() {
    BasketRepository basketRepo = new BasketRepository();
    ProductService productService = new ProductService();

    service = new ShoppingBasketService(basketRepo, productService, dateService);
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
    given(dateService.getDate()).willReturn("10/10/2020");
    service.addItem(USER_ID, "10002", 2);
    service.addItem(USER_ID, "20110", 5);

    Basket basket = service.basketFor(USER_ID);

    assertEquals(7, basket.size());
    assertEquals("10/10/2020", basket.getCreationDate());
    assertEquals(2, basket.getQuantityForProduct("10002"));
    assertEquals(5, basket.getQuantityForProduct("20110"));
  }

  @Test
  public void check_basket_total_amount() {
    given(dateService.getDate()).willReturn("10/10/2020");
    service.addItem(USER_ID, "10002", 2);
    service.addItem(USER_ID, "20110", 5);

    Basket basket = service.basketFor(USER_ID);

    assertEquals(45.00, basket.getTotal());
  }

}
