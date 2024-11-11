package store;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Store {
    private List<Product> products;
    private List<Promotion> promotions;
    private static final Pattern PURCHASE_PATTERN = Pattern.compile("\\[(\\S+)-(\\d+)]");

    public Store() throws IOException {
        products = loadProducts();
        promotions = loadPromotions();
    }

    public void printProducts() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.\n");
        for (Product product : products) {
            if (product.getQuantity() == 0) {
                System.out.printf("- %s %,d원 %s\n", product.getName(), product.getPrice(), "재고 없음");
            } else {
                String promotion = product.getPromotion();
                if (promotion != null && !promotion.isEmpty()) {
                    System.out.printf("- %s %,d원 %d개 %s\n", product.getName(), product.getPrice(), product.getQuantity(), promotion);
                } else {
                    System.out.printf("- %s %,d원 %d개\n", product.getName(), product.getPrice(), product.getQuantity());
                }
            }
        }
    }

    public void checkPromotion(PurchaseItem item, Scanner scanner) {
        Product product = item.getProduct();
        int quantity = item.getQuantity();
        Promotion promotion = getActivePromotionForProduct(product);

        if (promotion == null) {
            return;
        }

        int buyCount = promotion.getBuy();
        int getCount = promotion.getGet();
        int totalItemsForPromotion = buyCount + getCount;

        if (quantity < buyCount) {
            return;
        }

        int totalEligiblePromotionCount = quantity / totalItemsForPromotion;
        int freeQuantity = totalEligiblePromotionCount * getCount;
        int remainingQuantity = quantity % totalItemsForPromotion;

        if (freeQuantity > 0) {
            System.out.printf("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n", product.getName(), freeQuantity);
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("Y")) {
                    item.incrementQuantity(freeQuantity);
                    break;
                } else if (input.equalsIgnoreCase("N")) {
                    break;
                } else {
                    System.out.println("[ERROR] 잘못된 입력입니다. Y 또는 N만 입력 가능합니다.");
                }
            }
        }

        if (remainingQuantity > 0) {
            if (remainingQuantity == 1) {
                System.out.printf("현재 %s은(는) 정상 가격으로 1개를 구매하시겠습니까? (Y/N)\n", product.getName());
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("Y")) {
                        break;
                    } else if (input.equalsIgnoreCase("N")) {
                        break;
                    } else {
                        System.out.println("[ERROR] 잘못된 입력입니다. Y 또는 N만 입력 가능합니다.");
                    }
                }
            } else if (remainingQuantity == 2) {
                // 2개는 프로모션에 해당하지 않으므로 추가 무료로 받을지 물어봄
                System.out.printf("현재 %s은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n", product.getName());
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("Y")) {
                        item.incrementQuantity(1);
                        break;
                    } else if (input.equalsIgnoreCase("N")) {
                        break;
                    } else {
                        System.out.println("[ERROR] 잘못된 입력입니다. Y 또는 N만 입력 가능합니다.");
                    }
                }
            }
        }
    }


    public List<PurchaseItem> parsePurchaseItems(String userInput) {
        List<PurchaseItem> purchaseItems = new ArrayList<>();
        String[] items = userInput.split(",");

        for (String item : items) {
            Matcher matcher = PURCHASE_PATTERN.matcher(item.trim());
            if (!matcher.matches()) {
                System.out.println("[ERROR] 올바르지 않은 형식으로 입력했습니다.");
                return null;
            }

            String productName = matcher.group(1);
            int quantity = Integer.parseInt(matcher.group(2));

            Product product = getProductByName(productName);
            if (product == null) {
                System.out.println("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
                return null;
            }
            if (product.getQuantity() < quantity) {
                System.out.println("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
                return null;
            }
            purchaseItems.add(new PurchaseItem(product, quantity));
            product.reduceQuantity(quantity);
        }
        return purchaseItems;
    }

    public Receipt processPurchase(List<PurchaseItem> purchaseItems, boolean isMembership) {
        int totalAmountBeforePromotion = 0;
        int totalAmountAfterPromotion = 0;
        int totalPromotionDiscount = 0;
        int totalMembershipDiscount = 0;
        Map<String, Integer> freeItems = new HashMap<>();

        for (PurchaseItem purchaseItem : purchaseItems) {
            Product product = purchaseItem.getProduct();
            int quantity = purchaseItem.getQuantity();
            int price = product.getPrice();

            Promotion promotion = getActivePromotionForProduct(product);
            if (promotion != null) {
                int free = quantity / (promotion.getBuy() + promotion.getGet());
                freeItems.put(product.getName(), free);
                totalAmountBeforePromotion += price * quantity;
                totalAmountAfterPromotion += price * (quantity - free);
                totalPromotionDiscount += price * free;
            } else {
                totalAmountBeforePromotion += price * quantity;
                totalAmountAfterPromotion += price * quantity;
            }
        }

        if (isMembership) {
            int discount = totalAmountBeforePromotion * 30 / 100;
            totalMembershipDiscount = Math.min(discount, 8000);
        }

        int finalAmount = totalAmountAfterPromotion - totalMembershipDiscount;

        return new Receipt(purchaseItems, freeItems, finalAmount, totalPromotionDiscount, totalMembershipDiscount, totalAmountBeforePromotion);
    }

    public void printReceipt(Receipt receipt) {
        System.out.println("\n===========W 편의점=============");
        System.out.println("구매 상품 내역:");
        System.out.println("상품명\t\t수량\t금액");
        for (PurchaseItem item : receipt.getPurchaseItems()) {
            System.out.printf("%s\t\t%d\t%,d\n", item.getProduct().getName(), item.getQuantity(), item.getProduct().getPrice() * item.getQuantity());
        }

        System.out.println("===========증\t정=============");
        for (Map.Entry<String, Integer> entry : receipt.getFreeItems().entrySet()) {
            System.out.printf("%s\t\t%,d\n", entry.getKey(), entry.getValue());
        }

        System.out.println("==============================");
        System.out.printf("총구매액\t\t%,d\n", receipt.getTotalAmountBeforePromotion());
        System.out.printf("행사할인\t\t-%,d\n", receipt.getTotalPromotionDiscount());
        System.out.printf("멤버십할인\t\t-%,d\n", receipt.getTotalMembershipDiscount());
        System.out.printf("내실돈\t\t\t%,d\n", receipt.getTotalAmount());
    }

    private Product getProductByName(String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

    private Promotion getActivePromotionForProduct(Product product) {
        LocalDate today = LocalDate.now();
        for (Promotion promotion : promotions) {
            if (promotion.getName().equals(product.getPromotion())) {
                LocalDate startDate = promotion.getStartDate();
                LocalDate endDate = promotion.getEndDate();
                if (!today.isBefore(startDate) && !today.isAfter(endDate)) {
                    return promotion;
                }
            }
        }
        return null;
    }

    private List<Product> loadProducts() throws IOException {
        List<Product> products = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/products.md"));
        for (String line : lines.subList(1, lines.size())) {
            String[] parts = line.split(",");
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int quantity = Integer.parseInt(parts[2]);
            String promotion = parts[3].equals("null") ? "" : parts[3];
            products.add(new Product(name, price, quantity, promotion));
        }
        return products;
    }

    private List<Promotion> loadPromotions() throws IOException {
        List<Promotion> promotions = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/promotions.md"));
        for (String line : lines.subList(1, lines.size())) {
            String[] parts = line.split(",");
            String name = parts[0];
            int buy = Integer.parseInt(parts[1]);
            int get = Integer.parseInt(parts[2]);
            String startDate = parts[3];
            String endDate = parts[4];
            promotions.add(new Promotion(name, buy, get, startDate, endDate));
        }
        return promotions;
    }
}
