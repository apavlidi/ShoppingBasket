package domain;

import java.util.HashMap;
import java.util.Map;

public class Basket {

  private Map<String, Product> products;
  private Map<String, Integer> productsQuantity;

  public Basket() {
    this.products = new HashMap<>();
    this.productsQuantity = new HashMap<>();
  }

  public int size() {
    return products.keySet().stream().mapToInt(productId -> productsQuantity.get(productId))
        .sum();
  }

  public int getQuantityForProduct(String productId) {
    return productsQuantity.get(productId);
  }

  public Product getProductById(String productId) {
    return products.get(productId);
  }

  public void addItem(Product product, int quantity) {
    products.put(product.getId(), product);
    productsQuantity.put(product.getId(), quantity);
  }
}
