package ru.edu.pr02;

public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = false;
        try {
            boolean basic = TimesheetAnalyzer.total(new int[]{8, 8, 8, 8, 8}) == 40
                    && TimesheetAnalyzer.overtimeDays(new int[]{9, 8, 7, 10, 8}) == 2
                    && Math.abs(TimesheetAnalyzer.average(new int[]{9, 8, 7, 10, 8}) - 8.4) < 0.001;
            boolean bounds = TimesheetAnalyzer.total(TimesheetAnalyzer.parseHours("0;0;0;0;0")) == 0
                    && TimesheetAnalyzer.total(TimesheetAnalyzer.parseHours("16;16;16;16;16")) == 80;
            boolean variant6 = TimesheetAnalyzer.weeklyOvertime(new int[]{7, 7, 7, 7, 7}) == 0
                    && TimesheetAnalyzer.weeklyOvertime(new int[]{8, 8, 8, 8, 8}) == 5;
            boolean err17 = false;
            try {
                TimesheetAnalyzer.parseHours("8;8;17;8;8");
            } catch (IllegalArgumentException e) {
                err17 = e.getMessage().contains("3");
            }
            boolean errEmpty = false;
            try {
                TimesheetAnalyzer.parseHours("");
            } catch (IllegalArgumentException e) {
                errEmpty = true;
            }
            boolean errFormat = false;
            try {
                TimesheetAnalyzer.parseHours("8;abc;8;8;8");
            } catch (IllegalArgumentException e) {
                errFormat = e.getMessage().contains("2");
            }
            ok = basic && bounds && variant6 && err17 && errEmpty && errFormat;
        } catch (Exception e) {
            System.out.println("FAIL exception " + e.getMessage());
        }
        System.out.println(ok ? "PASS" : "FAIL  complete TODO methods");
    }
}
