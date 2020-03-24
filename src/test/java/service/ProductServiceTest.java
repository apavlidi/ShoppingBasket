package service;

import static org.junit.jupiter.api.Assertions.*;

import domain.Product;
import org.junit.jupiter.api.Test;

class ProductServiceTest {

  @Test
  public void retrieves_item_based_on_id() {
    ProductService service = new ProductService();
    Product product = service.getProductById("10002");

    assertNotNull(product);
  }

}