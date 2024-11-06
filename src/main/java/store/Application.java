package store;

import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        Store store = null;
        try {
            store = new Store();
        } catch (IOException e) {
            System.out.println("[ERROR] Store 객체 생성 중 예외 발생: " + e.getMessage());
            return;
        }

        store.printProducts();
    }
}
