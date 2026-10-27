package ru.edu.pr02;

import java.io.File;
import java.util.Locale;
import java.util.Scanner;

public class TimesheetAnalyzer {
    public static final int EXPECTED_DAYS = 5;
    public static final int MIN_HOURS_PER_DAY = 0;
    public static final int MAX_HOURS_PER_DAY = 16;
    public static final int DAILY_NORM_HOURS = 8;
    public static final int WEEKLY_NORM_HOURS = 35;

    public static int[] parseHours(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Пустая строка табеля");
        }
        String[] parts = line.trim().split(";", -1);
        if (parts.length != EXPECTED_DAYS) {
            throw new IllegalArgumentException("Строка должна содержать ровно " + EXPECTED_DAYS
                    + " значений, получено: " + parts.length);
        }
        int[] hours = new int[EXPECTED_DAYS];
        for (int i = 0; i < parts.length; i++) {
            String token = parts[i].trim();
            int position = i + 1;
            if (token.isEmpty()) {
                throw new IllegalArgumentException("Пустое значение на позиции " + position);
            }
            int value;
            try {
                value = Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректный формат целого числа на позиции "
                        + position + ": '" + token + "'");
            }
            if (value < MIN_HOURS_PER_DAY || value > MAX_HOURS_PER_DAY) {
                throw new IllegalArgumentException("Недопустимое количество часов на позиции "
                        + position + ": " + value + " (допустимый диапазон "
                        + MIN_HOURS_PER_DAY + ".." + MAX_HOURS_PER_DAY + ")");
            }
            hours[i] = value;
        }
        return hours;
    }

    public static int total(int[] hours) {
        validateArray(hours);
        int sum = 0;
        for (int i = 0; i < hours.length; i++) {
            sum += hours[i];
        }
        return sum;
    }

    public static double average(int[] hours) {
        validateArray(hours);
        return (double) total(hours) / hours.length;
    }

    public static int overtimeDays(int[] hours) {
        validateArray(hours);
        int count = 0;
        for (int i = 0; i < hours.length; i++) {
            if (hours[i] > DAILY_NORM_HOURS) {
                count++;
            }
        }
        return count;
    }

    public static int weeklyOvertime(int[] hours) {
        validateArray(hours);
        return Math.max(0, total(hours) - WEEKLY_NORM_HOURS);
    }

    public static String formatReport(String line) {
        try {
            int[] hours = parseHours(line);
            int sum = total(hours);
            double avg = average(hours);
            int overDays = overtimeDays(hours);
            int weekOver = weeklyOvertime(hours);
            return String.format(Locale.US,
                    "OK [%s] -> Сумма: %d ч | Среднее: %.2f ч/день | Дней с переработкой (>%d ч): %d | Недельная переработка (норма %d ч): %d ч",
                    line, sum, avg, DAILY_NORM_HOURS, overDays, WEEKLY_NORM_HOURS, weekOver);
        } catch (IllegalArgumentException e) {
            return "ОШИБКА [" + line + "] -> " + e.getMessage();
        }
    }

    private static void validateArray(int[] hours) {
        if (hours == null || hours.length != EXPECTED_DAYS) {
            throw new IllegalArgumentException("Массив часов должен содержать ровно " + EXPECTED_DAYS + " элементов");
        }
        for (int i = 0; i < hours.length; i++) {
            if (hours[i] < MIN_HOURS_PER_DAY || hours[i] > MAX_HOURS_PER_DAY) {
                throw new IllegalArgumentException("Недопустимое значение часов на позиции " + (i + 1) + ": " + hours[i]);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        File csv = new File("data/sample.csv");
        if (args.length > 0) {
            csv = new File(args[0]);
        }
        if (csv.exists()) {
            try (Scanner scanner = new Scanner(csv, "UTF-8")) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println(formatReport(line));
                }
            }
        }
    }
}
