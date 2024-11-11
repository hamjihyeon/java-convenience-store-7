package store;

import java.util.List;
import java.util.Map;

public class Receipt {
    private final List<PurchaseItem> purchaseItems;
    private final Map<String, Integer> freeItems;
    private final int totalAmount;
    private final int totalPromotionDiscount;
    private final int totalMembershipDiscount;
    private final int totalAmountBeforePromotion;

    public Receipt(List<PurchaseItem> purchaseItems, Map<String, Integer> freeItems, int totalAmount, int totalPromotionDiscount, int totalMembershipDiscount, int totalAmountBeforePromotion) {
        this.purchaseItems = purchaseItems;
        this.freeItems = freeItems;
        this.totalAmount = totalAmount;
        this.totalPromotionDiscount = totalPromotionDiscount;
        this.totalMembershipDiscount = totalMembershipDiscount;
        this.totalAmountBeforePromotion = totalAmountBeforePromotion;
    }

    public List<PurchaseItem> getPurchaseItems() {
        return purchaseItems;
    }

    public Map<String, Integer> getFreeItems() {
        return freeItems;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getTotalPromotionDiscount() {
        return totalPromotionDiscount;
    }

    public int getTotalMembershipDiscount() {
        return totalMembershipDiscount;
    }

    public int getTotalAmountBeforePromotion() {
        return totalAmountBeforePromotion;
    }
}
