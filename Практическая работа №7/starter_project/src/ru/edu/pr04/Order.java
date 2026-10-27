package ru.edu.pr04;

import java.util.*;

public class Order {
    private final Employee employee;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public Order(Employee employee) {
        this.employee = Objects.requireNonNull(employee, "Сотрудник (employee) не может быть null");
    }

    public void addProduct(Product product, int quantity) {
        ensureDraft();
        Objects.requireNonNull(product, "Товар (product) не может быть null");
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                if (Double.compare(item.getProduct().getPrice(), product.getPrice()) != 0) {
                    throw new IllegalArgumentException("Конфликт цены при объединении повторного товара с id=" + product.getId());
                }
                item.increase(quantity);
                return;
            }
        }
        items.add(new OrderItem(product, quantity));
    }

    public void removeProduct(String productId) {
        ensureDraft();
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Идентификатор товара для удаления не может быть пустым");
        }
        boolean removed = items.removeIf(item -> item.getProduct().getId().equals(productId.trim()));
        if (!removed) {
            throw new IllegalArgumentException("Позиция с id товара '" + productId + "' не найдена в заказе");
        }
    }

    public double total() {
        double sum = 0.0;
        for (OrderItem item : items) {
            sum += item.getLineTotal();
        }
        return sum;
    }

    public void confirm() {
        ensureDraft();
        if (items.isEmpty()) {
            throw new IllegalStateException("Нельзя подтвердить пустой заказ без единой позиции");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    private void ensureDraft() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Заказ уже подтверждён (CONFIRMED) и не может быть изменён");
        }
    }

    public Employee getEmployee() {
        return employee;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public OrderStatus getStatus() {
        return status;
    }
}
