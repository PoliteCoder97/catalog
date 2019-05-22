package project.product;

public class ProductEventListener {
  private Product product;

  public ProductEventListener(Product product) {
    this.product = product;
  }
  public Product getProduct() {
    return product;
  }
}
