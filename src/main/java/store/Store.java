package store;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Store {
    private List<Product> products;
    private List<Promotion> promotions;

    public Store() throws IOException {
        products = loadProducts();
        promotions = loadPromotions();
    }

    public void printProducts() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.\n");
        for (Product product : products) {
            System.out.printf("- %s %,d원 %d %s\n",
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getPromotion() != null ? product.getPromotion() : ""
            );
        }
    }

    public List<PurchaseItem> parsePurchaseItems(String userInput) {
        List<PurchaseItem> purchaseItems = new ArrayList<>();

        if (!userInput.startsWith("[") || !userInput.endsWith("]")) {
            System.out.println("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
            return null;
        }

        userInput = userInput.substring(1, userInput.length() - 1);
        String[] items = userInput.split("\\],\\[");

        for (String item : items) {
            item = item.trim();

            String[] parts = item.split("-");
            if (parts.length != 2) {
                System.out.println("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
                return null;
            }

            String name = parts[0].trim();
            int quantity;

            try {
                quantity = Integer.parseInt(parts[1].trim());
                if (quantity < 1) {
                    System.out.println("[ERROR] 수량은 1 이상의 숫자여야 합니다. 다시 입력해 주세요.");
                    return null;
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] 수량은 숫자여야 합니다. 다시 입력해 주세요.");
                return null;
            }

            // 상품이 존재하는지 체크
            Product product = getProductByName(name);
            if (product == null) {
                System.out.println("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
                return null;
            }

            // 재고 수량 체크
            if (product.getQuantity() < quantity) {
                System.out.println("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
                return null;
            }

            purchaseItems.add(new PurchaseItem(product, quantity));
            product.reduceQuantity(quantity);
        }

        return purchaseItems;
    }

    public Product getProductByName(String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return product;
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
            promotions.add(new Promotion(name, buy, get));
        }
        return promotions;
    }
}
