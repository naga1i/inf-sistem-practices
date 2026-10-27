package ru.edu.pr01;

import java.io.File;
import java.util.Locale;
import java.util.Scanner;

public class OrderCalculator {
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 10_000;
    public static final double MAX_UNIT_PRICE = 5_000_000.0;
    public static final double MIN_DISCOUNT_PERCENT = 0.0;
    public static final double MAX_DISCOUNT_PERCENT = 30.0;
    public static final double VAT_RATE = 20.0;
    public static final double FREE_PROCESSING_THRESHOLD = 35_000.0;
    public static final double PROCESSING_FEE = 500.0;

    public static boolean isValid(int quantity, double unitPrice, double discountPercent) {
        return validateInput(quantity, unitPrice, discountPercent) == null;
    }

    public static String validateInput(int quantity, double unitPrice, double discountPercent) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            return "Количество должно быть в диапазоне от 1 до 10 000 (получено: " + quantity + ")";
        }
        if (Double.isNaN(unitPrice) || Double.isInfinite(unitPrice) || unitPrice <= 0.0 || unitPrice > MAX_UNIT_PRICE) {
            return "Цена единицы должна быть больше 0 и не превышать 5 000 000 руб. (получено: " + unitPrice + ")";
        }
        if (Double.isNaN(discountPercent) || Double.isInfinite(discountPercent)
                || discountPercent < MIN_DISCOUNT_PERCENT || discountPercent > MAX_DISCOUNT_PERCENT) {
            return "Скидка должна быть в диапазоне от 0% до 30% (получено: " + discountPercent + "%)";
        }
        return null;
    }

    public static double calculateBase(int quantity, double unitPrice) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY || unitPrice <= 0.0 || unitPrice > MAX_UNIT_PRICE) {
            throw new IllegalArgumentException("Некорректные параметры базовой стоимости");
        }
        return quantity * unitPrice;
    }

    public static double applyDiscount(double base, double discountPercent) {
        if (base < 0.0 || discountPercent < MIN_DISCOUNT_PERCENT || discountPercent > MAX_DISCOUNT_PERCENT) {
            throw new IllegalArgumentException("Некорректные параметры скидки");
        }
        return base * (1.0 - discountPercent / 100.0);
    }

    public static double calculateVat(double discounted, double vatPercent) {
        if (discounted < 0.0 || vatPercent < 0.0) {
            throw new IllegalArgumentException("Некорректные параметры расчёта НДС");
        }
        return discounted * (vatPercent / 100.0);
    }

    public static boolean isFreeProcessing(double discountedAmount) {
        return discountedAmount >= FREE_PROCESSING_THRESHOLD;
    }

    public static double calculateProcessingFee(double discountedAmount) {
        return isFreeProcessing(discountedAmount) ? 0.0 : PROCESSING_FEE;
    }

    public static double calculateTotal(int quantity, double unitPrice, double discountPercent, double vatPercent) {
        String error = validateInput(quantity, unitPrice, discountPercent);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        if (Double.isNaN(vatPercent) || Double.isInfinite(vatPercent) || vatPercent < 0.0) {
            throw new IllegalArgumentException("Ставка НДС не может быть отрицательной");
        }
        double base = calculateBase(quantity, unitPrice);
        double discounted = applyDiscount(base, discountPercent);
        double vat = calculateVat(discounted, vatPercent);
        return discounted + vat;
    }

    public static String formatCurrency(double amount) {
        return String.format(Locale.US, "%.2f руб.", amount);
    }

    public static String processLine(String line) {
        if (line == null || line.isBlank()) {
            return "ОШИБКА: Пустая входная строка";
        }
        String[] parts = line.trim().split(";", -1);
        if (parts.length != 3) {
            return "ОШИБКА: Ожидалось 3 поля (quantity;unitPrice;discountPercent), получено " + parts.length;
        }
        int quantity;
        double unitPrice;
        double discountPercent;
        try {
            quantity = Integer.parseInt(parts[0].trim());
            unitPrice = Double.parseDouble(parts[1].trim());
            discountPercent = Double.parseDouble(parts[2].trim());
        } catch (NumberFormatException e) {
            return "ОШИБКА формата числа в строке [" + line + "]: " + e.getMessage();
        }
        String validationError = validateInput(quantity, unitPrice, discountPercent);
        if (validationError != null) {
            return "ОШИБКА валидации [" + line + "]: " + validationError;
        }
        double base = calculateBase(quantity, unitPrice);
        double discounted = applyDiscount(base, discountPercent);
        double vat = calculateVat(discounted, VAT_RATE);
        double total = calculateTotal(quantity, unitPrice, discountPercent, VAT_RATE);
        boolean freeProc = isFreeProcessing(discounted);
        return String.format(Locale.US,
                "OK [%s] -> База: %s | Со скидкой: %s | НДС (%.0f%%): %s | Итого: %s | Бесплатная обработка (>= %.2f): %s",
                line, formatCurrency(base), formatCurrency(discounted), VAT_RATE,
                formatCurrency(vat), formatCurrency(total), FREE_PROCESSING_THRESHOLD, freeProc ? "ДА" : "НЕТ");
    }

    public static void main(String[] args) throws Exception {
        File csv = new File("data/sample.csv");
        if (args.length > 0) {
            csv = new File(args[0]);
        }
        if (csv.exists()) {
            try (Scanner scanner = new Scanner(csv, "UTF-8")) {
                boolean first = true;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (first && line.startsWith("quantity")) {
                        first = false;
                        continue;
                    }
                    first = false;
                    System.out.println(processLine(line));
                }
            }
        }
    }
}
