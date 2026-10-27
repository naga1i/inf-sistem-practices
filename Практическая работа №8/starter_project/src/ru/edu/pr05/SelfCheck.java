package ru.edu.pr05;

import java.time.LocalDate;

public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = false;
        try {
            BusinessTripService service = new BusinessTripService();
            BusinessTrip t1 = service.create("T-01", LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 12));
            t1.submit();
            t1.approve();
            t1.complete(LocalDate.of(2026, 10, 13));
            boolean happyPathOk = t1.getStatus() == TripStatus.COMPLETED
                    && service.findByStatus(TripStatus.COMPLETED).size() == 1;

            boolean cancelAfterCompleteBlocked = false;
            try {
                t1.cancel();
            } catch (IllegalStateException e) {
                cancelAfterCompleteBlocked = true;
            }

            BusinessTrip t2 = service.create("T-02", LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 10));
            t2.submit();
            boolean shortReasonBlocked = false;
            try {
                t2.reject("Коротко");
            } catch (IllegalArgumentException e) {
                shortReasonBlocked = t2.getStatus() == TripStatus.SUBMITTED;
            }
            t2.reject("Отсутствует финансирование в квартальном бюджете");
            boolean rejectOk = t2.getStatus() == TripStatus.REJECTED;

            boolean endBeforeStartBlocked = false;
            try {
                service.create("T-ERR-1", LocalDate.of(2026, 10, 12), LocalDate.of(2026, 10, 10));
            } catch (IllegalArgumentException e) {
                endBeforeStartBlocked = true;
            }

            // Вариант 6: максимальная длительность 35 дней (1 окт .. 4 ноя = 35 дней OK, 1 окт .. 5 ноя = 36 дней ОШИБКА)
            BusinessTrip t35 = service.create("T-35", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 4));
            boolean variant6BoundOk = t35.getStatus() == TripStatus.DRAFT;
            boolean variant6ExceededBlocked = false;
            try {
                service.create("T-36", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 5));
            } catch (IllegalArgumentException e) {
                variant6ExceededBlocked = true;
            }

            ok = happyPathOk && cancelAfterCompleteBlocked && shortReasonBlocked
                    && rejectOk && endBeforeStartBlocked && variant6BoundOk && variant6ExceededBlocked;
        } catch (Exception e) {
            System.out.println("FAIL exception " + e.getMessage());
        }
        System.out.println(ok ? "PASS" : "FAIL  complete TODO methods");
    }
}
