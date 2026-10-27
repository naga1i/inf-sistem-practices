package ru.edu.pr04;

public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = false;
        try {
            OrderService service = new OrderService();
            Employee emp = new Employee("E1", "Иванов И.И.");
            Order order = service.createOrder(emp);
            Product p1 = new Product("P1", "Mouse", 1000.0);
            Product p2 = new Product("P2", "Keyboard", 3000.0);

            order.addProduct(p1, 2);
            order.addProduct(p2, 1);
            order.addProduct(p1, 3); // объединение: 2 + 3 = 5 шт.

            boolean mergeAndTotalOk = order.getItems().size() == 2
                    && order.getItems().get(0).getQuantity() == 5
                    && Math.abs(order.total() - 8000.0) < 0.001;

            boolean variant6MergeLimitOk = false;
            try {
                order.addProduct(p1, 31); // 5 + 31 = 36 > 35 (лимит объединения по варианту 6)
            } catch (IllegalArgumentException e) {
                variant6MergeLimitOk = true;
            }

            order.removeProduct("P2");
            boolean removeOk = order.getItems().size() == 1 && Math.abs(order.total() - 5000.0) < 0.001;

            service.confirmOrder(order);
            boolean confirmOk = order.getStatus() == OrderStatus.CONFIRMED;

            boolean immutableAfterConfirmOk = false;
            try {
                order.addProduct(p2, 1);
            } catch (IllegalStateException e) {
                immutableAfterConfirmOk = true;
            }

            boolean unmodifiableListOk = false;
            try {
                order.getItems().clear();
            } catch (UnsupportedOperationException e) {
                unmodifiableListOk = true;
            }

            ok = mergeAndTotalOk && variant6MergeLimitOk && removeOk && confirmOk && immutableAfterConfirmOk && unmodifiableListOk;
        } catch (Exception e) {
            System.out.println("FAIL exception " + e.getMessage());
        }
        System.out.println(ok ? "PASS" : "FAIL  complete TODO methods");
    }
}
