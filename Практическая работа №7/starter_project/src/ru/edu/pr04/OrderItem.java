package ru.edu.pr04;

import java.util.Objects;

public class OrderItem {
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 100;
    public static final int MAX_MERGED_QUANTITY = 35;

    private final Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = Objects.requireNonNull(product, "Товар (product) не может быть null");
        setQuantity(quantity);
    }

    private void setQuantity(int quantity) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Количество позиции должно быть в диапазоне от "
                    + MIN_QUANTITY + " до " + MAX_QUANTITY + " (получено: " + quantity + ")");
        }
        this.quantity = quantity;
    }

    public void increase(int delta) {
        if (delta < MIN_QUANTITY) {
            throw new IllegalArgumentException("Добавляемое количество должно быть положительным: " + delta);
        }
        int merged = this.quantity + delta;
        if (merged > MAX_MERGED_QUANTITY) {
            throw new IllegalArgumentException("Превышен лимит объединения повторного товара (макс. "
                    + MAX_MERGED_QUANTITY + " шт. по варианту 6, попытка установить: " + merged + ")");
        }
        setQuantity(merged);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return product.getPrice() * quantity;
    }
}
