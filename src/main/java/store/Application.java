package store;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        Store store = null;
        try {
            store = new Store();
        } catch (IOException e) {
            System.out.println("[ERROR] Store 객체 생성 중 예외 발생: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            store.printProducts();
            System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
            String input = scanner.nextLine();

            List<PurchaseItem> purchaseItems = store.parsePurchaseItems(input);
            if (purchaseItems == null) {
                continue;
            }
        }
    }
}
