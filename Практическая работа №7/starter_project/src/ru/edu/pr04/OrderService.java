package ru.edu.pr04;

import java.io.File;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class OrderService {
    public Order createOrder(Employee employee) {
        return new Order(employee);
    }

    public void validateForConfirm(Order order) {
        Objects.requireNonNull(order, "Заказ (order) не может быть null");
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException("Заказ уже находится в статусе " + order.getStatus());
        }
        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Заказ должен содержать хотя бы одну позицию перед подтверждением");
        }
        if (order.total() <= 0.0) {
            throw new IllegalStateException("Итоговая стоимость заказа должна быть положительной");
        }
    }

    public void confirmOrder(Order order) {
        validateForConfirm(order);
        order.confirm();
    }

    public static void main(String[] args) throws Exception {
        OrderService service = new OrderService();
        Employee emp = new Employee("E-101", "Крапчатов Георгий Павлович");
        Order order = service.createOrder(emp);

        File csv = new File("data/sample.csv");
        if (args.length > 0) {
            csv = new File(args[0]);
        }
        if (csv.exists()) {
            try (Scanner scanner = new Scanner(csv, "UTF-8")) {
                boolean first = true;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (first && line.startsWith("productId")) {
                        first = false;
                        continue;
                    }
                    first = false;
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(";", -1);
                    if (parts.length != 4) {
                        System.out.println("ОШИБКА формата строки [" + line + "]");
                        continue;
                    }
                    try {
                        Product p = new Product(parts[0].trim(), parts[1].trim(), Double.parseDouble(parts[2].trim()));
                        int qty = Integer.parseInt(parts[3].trim());
                        order.addProduct(p, qty);
                        System.out.printf(Locale.US, "ДОБАВЛЕНО [%s] -> Позиций: %d | Текущий итог: %.2f руб.%n",
                                line, order.getItems().size(), order.total());
                    } catch (Exception e) {
                        System.out.println("ОШИБКА [" + line + "] -> " + e.getMessage());
                    }
                }
            }
        }
        service.confirmOrder(order);
        System.out.printf(Locale.US, "ЗАКАЗ ПОДТВЕРЖДЁН (статус=%s, позиций=%d, итого=%.2f руб.)%n",
                order.getStatus(), order.getItems().size(), order.total());
    }
}
