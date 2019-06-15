package project.management_panel.product;

import project.product.Product;

public class PanelProductListEventListener {
    Product product;
    public PanelProductListEventListener(Product product){
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
