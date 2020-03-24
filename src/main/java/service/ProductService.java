package service;

import domain.Product;
import java.util.Map;

public class ProductService {

  Map<String, Product> products = Map.of(
      "10001",new Product("10001","Lord of the Rings",10),
      "10002",new Product("10002","The Hobbit",5),
      "20001",new Product("20001","Game of Thrones",9),
      "20110",new Product("20110","Breaking Bad",7)
  );

  public Product getProductById(String productId) {
    return products.get(productId);
  }
}
