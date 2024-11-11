package store;

public class PurchaseItem {
    private final Product product;
    private int quantity;

    public PurchaseItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void incrementQuantity(int additionalQuantity) {
        this.quantity += additionalQuantity;
    }
}
