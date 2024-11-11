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

            for (PurchaseItem item : purchaseItems) {
                store.checkPromotion(item, scanner);
            }

            System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
            String membershipInput;
            while (true) {
                membershipInput = scanner.nextLine();
                if (membershipInput.equalsIgnoreCase("Y") || membershipInput.equalsIgnoreCase("N")) {
                    break;
                }

                System.out.println("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
                System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
            }
            boolean isMembership = membershipInput.equalsIgnoreCase("Y");

            Receipt receipt = store.processPurchase(purchaseItems, isMembership);
            store.printReceipt(receipt);

            System.out.println("\n감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
            if (!scanner.nextLine().equalsIgnoreCase("Y")) {
                break;
            }
        }
    }
}
//public class Application {
//    public static void main(String[] args) {
//        try {
//            Store store = new Store();
//            Scanner scanner = new Scanner(System.in);
//            boolean continueShopping = true;
//
//            System.out.println("안녕하세요. W편의점입니다.");
//
//            while (continueShopping) {
//                // 상품 리스트 출력
//                store.printProducts();
//
//                // 구매 입력
//                System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
//                String userInput = scanner.nextLine();
//
//                // 구매 항목 파싱
//                List<PurchaseItem> purchaseItems = store.parsePurchaseItems(userInput);
//                if (purchaseItems == null) {
//                    continue; // 잘못된 입력일 경우 처음부터 다시 입력
//                }
//
//                // 멤버십 할인 여부 입력
//                System.out.println("멤버십 할인 여부 (Y/N)");
//                String membershipInput = scanner.nextLine();
//                boolean isMembership = "Y".equalsIgnoreCase(membershipInput);
//
//                // 구매 처리
//                Receipt receipt = store.processPurchase(purchaseItems, isMembership);
//
//                // 영수증 출력
//                store.printReceipt(receipt);
//
//                // 추가 구매 여부 확인
//                System.out.println("\n감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
//                String continueInput = scanner.nextLine();
//                continueShopping = "Y".equalsIgnoreCase(continueInput);
//            }
//        } catch (IOException e) {
//            System.out.println("[ERROR] 파일을 읽을 수 없습니다.");
//        }
//    }
//}