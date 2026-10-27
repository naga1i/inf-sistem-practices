package ru.edu.pr05;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class BusinessTrip {
    public static final int MIN_REJECTION_REASON_LENGTH = 10;
    public static final long MAX_TRIP_DURATION_DAYS = 35L;

    private final String id;
    private final Employee employee;
    private final LocalDate start;
    private final LocalDate end;
    private TripStatus status = TripStatus.DRAFT;
    private String rejectionReason;

    public BusinessTrip(String id, LocalDate start, LocalDate end) {
        this(id, new Employee("EMP-DEFAULT", "Сотрудник по умолчанию"), start, end);
    }

    public BusinessTrip(String id, Employee employee, LocalDate start, LocalDate end) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Идентификатор командировки (id) не может быть пустым");
        }
        Objects.requireNonNull(employee, "Сотрудник (employee) не может быть null");
        validateDates(start, end);
        this.id = id.trim();
        this.employee = employee;
        this.start = start;
        this.end = end;
    }

    private static void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Даты начала и окончания командировки обязательны");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Дата окончания (" + end
                    + ") не может быть раньше даты начала (" + start + ")");
        }
        long durationDays = ChronoUnit.DAYS.between(start, end) + 1;
        if (durationDays > MAX_TRIP_DURATION_DAYS) {
            throw new IllegalArgumentException("Длительность командировки (" + durationDays
                    + " дн.) превышает максимально допустимую по варианту 6 ("
                    + MAX_TRIP_DURATION_DAYS + " дн.)");
        }
    }

    public void submit() {
        if (status != TripStatus.DRAFT) {
            throw new IllegalStateException("Отправка (submit) возможна только из статуса DRAFT (текущий: " + status + ")");
        }
        if (id.isBlank() || employee == null || start == null || end == null) {
            throw new IllegalStateException("Не заполнены обязательные поля заявки на командировку");
        }
        this.status = TripStatus.SUBMITTED;
    }

    public void approve() {
        if (status != TripStatus.SUBMITTED) {
            throw new IllegalStateException("Согласование (approve) возможно только из статуса SUBMITTED (текущий: " + status + ")");
        }
        this.status = TripStatus.APPROVED;
    }

    public void reject(String reason) {
        if (status != TripStatus.SUBMITTED) {
            throw new IllegalStateException("Отклонение (reject) возможно только из статуса SUBMITTED (текущий: " + status + ")");
        }
        if (reason == null || reason.trim().length() < MIN_REJECTION_REASON_LENGTH) {
            throw new IllegalArgumentException("Причина отклонения обязательна и должна содержать минимум "
                    + MIN_REJECTION_REASON_LENGTH + " символов");
        }
        this.rejectionReason = reason.trim();
        this.status = TripStatus.REJECTED;
    }

    public void cancel() {
        if (status != TripStatus.DRAFT && status != TripStatus.SUBMITTED && status != TripStatus.APPROVED) {
            throw new IllegalStateException("Отмена (cancel) недопустима из статуса " + status);
        }
        this.status = TripStatus.CANCELLED;
    }

    public void complete(LocalDate today) {
        if (status != TripStatus.APPROVED) {
            throw new IllegalStateException("Завершение (complete) возможно только из статуса APPROVED (текущий: " + status + ")");
        }
        if (today == null) {
            throw new IllegalArgumentException("Текущая дата (today) не может быть null");
        }
        if (!today.isAfter(end)) {
            throw new IllegalArgumentException("Завершение командировки возможно только после даты окончания ("
                    + end + "), передана дата: " + today);
        }
        this.status = TripStatus.COMPLETED;
    }

    public String getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public TripStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}
