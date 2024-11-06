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
            System.out.printf("- %s %d원 %d개 %s\n", product.getName(), product.getPrice(), product.getQuantity(), product.getPromotion() != null ? product.getPromotion() : "없음");
        }
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
