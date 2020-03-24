package service;

import UI.Console;

public class LogService {

  private Console console;

  public LogService(Console console) {
    this.console = console;
  }

  private void log(String msg) {
    console.print(msg);
  }

  public void basketCreationDate(String date, String userId) {
    log(String.format("[BASKET CREATED]: Created[%s], User[%s]", date, userId));
  }

  public void itemAdded(String date, String userId, String productId, int quantity, int price) {
    log(String.format(
        "[ITEM ADDED TO SHOPPING CART]: Added[%s], User[%s], Product[%s], Quantity[%s], Price[%s]",
        date,userId, productId, quantity, price));
  }
}
