package ru.edu.pr05;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class BusinessTripService {
    private final Map<String, BusinessTrip> trips = new LinkedHashMap<>();

    public BusinessTrip create(String id, LocalDate start, LocalDate end) {
        return create(id, new Employee("E-01", "Крапчатов Г.П."), start, end);
    }

    public BusinessTrip create(String id, Employee employee, LocalDate start, LocalDate end) {
        if (id != null && trips.containsKey(id.trim())) {
            throw new IllegalArgumentException("Командировка с идентификатором " + id + " уже существует");
        }
        BusinessTrip trip = new BusinessTrip(id, employee, start, end);
        trips.put(trip.getId(), trip);
        return trip;
    }

    public List<BusinessTrip> findByStatus(TripStatus status) {
        Objects.requireNonNull(status, "Статус для поиска не может быть null");
        List<BusinessTrip> result = new ArrayList<>();
        for (BusinessTrip trip : trips.values()) {
            if (trip.getStatus() == status) {
                result.add(trip);
            }
        }
        return List.copyOf(result);
    }

    public static void main(String[] args) throws Exception {
        BusinessTripService service = new BusinessTripService();
        File csv = new File("data/sample.csv");
        if (args.length > 0) {
            csv = new File(args[0]);
        }
        if (csv.exists()) {
            try (Scanner scanner = new Scanner(csv, "UTF-8")) {
                boolean first = true;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (first && line.startsWith("id")) {
                        first = false;
                        continue;
                    }
                    first = false;
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(";", -1);
                    if (parts.length < 4) {
                        System.out.println("ОШИБКА формата строки [" + line + "]");
                        continue;
                    }
                    try {
                        String id = parts[0].trim();
                        LocalDate start = LocalDate.parse(parts[1].trim());
                        LocalDate end = LocalDate.parse(parts[2].trim());
                        String action = parts[3].trim().toUpperCase();
                        BusinessTrip trip = service.create(id, start, end);
                        switch (action) {
                            case "SUBMIT" -> trip.submit();
                            case "APPROVE" -> {
                                trip.submit();
                                trip.approve();
                            }
                            case "REJECT" -> {
                                trip.submit();
                                String reason = parts.length > 4 ? parts[4].trim() : "Недостаточно бюджета подразделения";
                                trip.reject(reason);
                            }
                            case "CANCEL" -> trip.cancel();
                            case "COMPLETE" -> {
                                trip.submit();
                                trip.approve();
                                LocalDate completeDate = parts.length > 4 ? LocalDate.parse(parts[4].trim()) : end.plusDays(1);
                                trip.complete(completeDate);
                            }
                            default -> throw new IllegalArgumentException("Неизвестная команда: " + action);
                        }
                        System.out.println("OK [" + line + "] -> Статус: " + trip.getStatus());
                    } catch (Exception e) {
                        System.out.println("ОШИБКА [" + line + "] -> " + e.getMessage());
                    }
                }
            }
        }
    }
}
