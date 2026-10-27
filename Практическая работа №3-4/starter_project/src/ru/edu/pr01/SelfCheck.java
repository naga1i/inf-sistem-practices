package ru.edu.pr01;

public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = false;
        try {
            boolean baseCheck = OrderCalculator.calculateBase(2, 100.0) == 200.0
                    && Math.abs(OrderCalculator.calculateTotal(2, 100.0, 10.0, 20.0) - 216.0) < 0.001;
            boolean lowerBoundCheck = OrderCalculator.isValid(1, 0.01, 0.0)
                    && !OrderCalculator.isValid(0, 1000.0, 10.0);
            boolean upperBoundCheck = OrderCalculator.isValid(10_000, 5_000_000.0, 30.0)
                    && !OrderCalculator.isValid(10_001, 100.0, 10.0)
                    && !OrderCalculator.isValid(5, 5_000_000.01, 10.0)
                    && !OrderCalculator.isValid(3, 999.99, 35.0);
            boolean negativePriceCheck = !OrderCalculator.isValid(5, -20.0, 5.0);
            boolean emptyAndFormatCheck = OrderCalculator.processLine("").startsWith("ОШИБКА")
                    && OrderCalculator.processLine("abc;100;5").startsWith("ОШИБКА");
            boolean variant6Check = !OrderCalculator.isFreeProcessing(34_999.99)
                    && OrderCalculator.isFreeProcessing(35_000.00);

            ok = baseCheck && lowerBoundCheck && upperBoundCheck && negativePriceCheck && emptyAndFormatCheck && variant6Check;
        } catch (Exception e) {
            System.out.println("FAIL exception " + e.getMessage());
        }
        System.out.println(ok ? "PASS" : "FAIL  complete TODO methods");
    }
}
