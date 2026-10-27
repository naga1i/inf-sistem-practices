package ru.edu.pr03;

import java.io.File;
import java.util.Scanner;

public class ApprovalRouter {
    public static final long FINANCE_LIMIT = 500_000L;
    public static final long URGENT_FAST_TRACK_LIMIT = 350_000L;

    public ApprovalRoute route(long amount, boolean urgent, RiskLevel risk, boolean containsPersonalData) {
        validateInput(amount, risk);
        if (requiresSecurityReview(containsPersonalData)) {
            return ApprovalRoute.SECURITY_REVIEW;
        }
        if (requiresDirectorReview(risk)) {
            return ApprovalRoute.DIRECTOR_REVIEW;
        }
        if (requiresFinanceReview(amount)) {
            return ApprovalRoute.FINANCE_REVIEW;
        }
        if (isEligibleForFastTrack(amount, urgent)) {
            return ApprovalRoute.FAST_TRACK;
        }
        return ApprovalRoute.STANDARD;
    }

    private void validateInput(long amount, RiskLevel risk) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма заявки должна быть положительной (получено: " + amount + ")");
        }
        if (risk == null) {
            throw new IllegalArgumentException("Уровень риска (RiskLevel) не может быть null");
        }
    }

    private boolean requiresSecurityReview(boolean containsPersonalData) {
        return containsPersonalData;
    }

    private boolean requiresDirectorReview(RiskLevel risk) {
        return risk == RiskLevel.HIGH;
    }

    private boolean requiresFinanceReview(long amount) {
        return amount >= FINANCE_LIMIT;
    }

    private boolean isEligibleForFastTrack(long amount, boolean urgent) {
        return urgent && amount <= URGENT_FAST_TRACK_LIMIT;
    }

    public static String processLine(ApprovalRouter router, String line) {
        if (line == null || line.trim().isEmpty()) {
            return "ОШИБКА: Пустая строка";
        }
        String[] parts = line.trim().split(";", -1);
        if (parts.length != 4) {
            return "ОШИБКА [" + line + "]: Ожидалось 4 поля (amount;urgent;risk;personal), получено " + parts.length;
        }
        try {
            long amount = Long.parseLong(parts[0].trim());
            String urgentStr = parts[1].trim();
            if (!urgentStr.equalsIgnoreCase("true") && !urgentStr.equalsIgnoreCase("false")) {
                return "ОШИБКА [" + line + "]: Некорректный флаг urgent: " + urgentStr;
            }
            boolean urgent = Boolean.parseBoolean(urgentStr);
            RiskLevel risk = RiskLevel.valueOf(parts[2].trim().toUpperCase());
            String personalStr = parts[3].trim();
            if (!personalStr.equalsIgnoreCase("true") && !personalStr.equalsIgnoreCase("false")) {
                return "ОШИБКА [" + line + "]: Некорректный флаг personal: " + personalStr;
            }
            boolean personal = Boolean.parseBoolean(personalStr);
            ApprovalRoute result = router.route(amount, urgent, risk, personal);
            return "OK [" + line + "] -> Маршрут: " + result;
        } catch (IllegalArgumentException e) {
            return "ОШИБКА [" + line + "] -> " + e.getMessage();
        }
    }

    public static void main(String[] args) throws Exception {
        ApprovalRouter router = new ApprovalRouter();
        File csv = new File("data/sample.csv");
        if (args.length > 0) {
            csv = new File(args[0]);
        }
        if (csv.exists()) {
            try (Scanner scanner = new Scanner(csv, "UTF-8")) {
                boolean first = true;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (first && line.startsWith("amount")) {
                        first = false;
                        continue;
                    }
                    first = false;
                    System.out.println(processLine(router, line));
                }
            }
        }
    }
}
