package com.gaegxh.firebirdtask2.service.ui;

import com.gaegxh.firebirdtask2.config.RouteConfig;
import com.gaegxh.firebirdtask2.model.*;
import com.gaegxh.firebirdtask2.service.search.SearchService;
import com.gaegxh.firebirdtask2.service.sell.SellService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ConsoleGui {

    private final SearchService searchService;
    private final SellService sellService;
    private final RouteConfig routeConfig;

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Добро пожаловать в систему покупки ж/д билетов ===");
        while (true) {
            try {
                System.out.println("\nДля выхода введите 0 в любом поле.");

                // Step 1: Ввод маршрута
                System.out.print("Введите город отправления: ");
                String from = scanner.nextLine().trim();
                if (from.equals("0")) break;

                System.out.print("Введите город прибытия: ");
                String to = scanner.nextLine().trim();
                if (to.equals("0")) break;

                if (!InputValidator.validateCities(from, to)) {
                    System.out.println("❌ Неверный маршрут. Допустимые маршруты: Paris-Lille, Lille-Paris, Lisbon-Porto, Porto-Lisbon");
                    continue; // цикл повторится
                }

                // Step 2: Ввод даты
                System.out.print("Введите дату поездки (dd.MM.yyyy или yyyy-MM-dd): ");
                String rawDate = scanner.nextLine().trim();
                if (rawDate.equals("0")) break;

                String formattedDate = InputValidator.validateAndFormatDate(rawDate);
                if (formattedDate == null) {
                    System.out.println("❌ Некорректная дата. Попробуйте снова.");
                    continue;
                }

                // Step 3: Выбор режима
                System.out.println("Выберите режим:\n1 - Поиск\n2 - Покупка");
                String mode = scanner.nextLine().trim();
                if (mode.equals("0")) break;

                String bestCriteria;
                if (mode.equals("2")) {
                    System.out.print("Введите желаемое время отправления (HH:mm): ");
                    String timeInput = scanner.nextLine().trim();
                    if (timeInput.equals("0")) break;

                    if (!InputValidator.validateTime(timeInput)) {
                        System.out.println("❌ Некорректный формат времени. Попробуйте снова.");
                        continue;
                    }
                    bestCriteria = "CLOSEST_TO_TIME:" + timeInput;
                } else if (mode.equals("1")) {
                    bestCriteria = "EARLIEST_DEPARTURE";
                } else {
                    System.out.println("❌ Неверный режим. Пожалуйста, введите 1 или 2.");
                    continue;
                }

                SearchCriteria searchCriteria = new SearchCriteria(
                        mode.equals("2") ? SearchCriteria.SearchType.PURCHASE : SearchCriteria.SearchType.SEARCH,
                        bestCriteria
                );

                TicketQueryRequest searchRequest = TicketQueryRequest.builder()
                        .departureStationUuid(routeConfig.getUuid(from))
                        .arrivalStationUuid(routeConfig.getUuid(to))
                        .departureDate(formattedDate)
                        .searchCriteria(searchCriteria)
                        .build();

                if (mode.equals("1")) {
                    System.out.println("\n--- Поиск билетов ---");
                    searchService.search(searchRequest);
                    continue;
                }

                System.out.println("\n--- Ввод данных пассажира ---");
                Passenger passenger = collectPassenger(scanner);
                if (passenger == null) continue;

                PassengerRequest passengerRequest = new PassengerRequest();
                passengerRequest.setPassengers(List.of(passenger));

                System.out.print("Введите email: ");
                String email = scanner.nextLine().trim();
                if (email.equals("0")) break;

                if (!InputValidator.validateEmail(email)) {
                    System.out.println("❌ Некорректный email. Попробуйте снова.");
                    continue;
                }

                ClientDetailsRequest clientDetails = ClientDetailsRequest.builder()
                        .user_email(email)
                        .confirm_user_email(email)
                        .build();

                System.out.println("\n--- Ввод данных оплаты ---");
                PaymentRequestDto payment = collectPayment(scanner, email);
                if (payment == null) continue;

                sellService.sell(searchRequest, passengerRequest, clientDetails, payment);

                System.out.println("\n✅ Операция завершена. Если хотите выйти, введите 0 при следующем вводе.");

            } catch (Exception e) {
                System.out.println("❌ Произошла ошибка: " + e.getMessage());
                System.out.println("Попробуйте снова.");
            }
        }

        System.out.println("\nСпасибо за использование системы. До свидания!");
    }

    private Passenger collectPassenger(Scanner scanner) {
        System.out.print("Имя: ");
        String first = scanner.nextLine().trim();
        if (first.equals("0")) return null;

        System.out.print("Фамилия: ");
        String last = scanner.nextLine().trim();
        if (last.equals("0")) return null;

        System.out.print("Пол (mr/mrs): ");
        String title = scanner.nextLine().trim().toLowerCase();
        if (title.equals("0")) return null;

        System.out.print("Дата рождения (yyyy-MM-dd или dd.MM.yyyy): ");
        String dobInput = scanner.nextLine().trim();
        if (dobInput.equals("0")) return null;

        String dob = InputValidator.validateAndFormatDob(dobInput);

        if (first.isEmpty() || last.isEmpty() || (!title.equals("mr") && !title.equals("mrs")) || dob == null) {
            System.out.println("❌ Неверные данные пассажира. Попробуйте снова.");
            return null;
        }

        return Passenger.builder()
                .first_name(first)
                .last_name(last)
                .full_name(first + " " + last)
                .title(title)
                .dob(dob)
                .category("adult")
                .build();
    }

    private PaymentRequestDto collectPayment(Scanner scanner, String email) {
        System.out.print("Номер карты: ");
        String cardNumber = scanner.nextLine().trim();
        if (cardNumber.equals("0")) return null;

        System.out.print("Тип карты (visa/mastercard): ");
        String cardType = scanner.nextLine().trim().toLowerCase();
        if (cardType.equals("0")) return null;

        System.out.print("Месяц окончания срока действия (MM): ");
        String month = scanner.nextLine().trim();
        if (month.equals("0")) return null;

        System.out.print("Год окончания срока действия (yyyy): ");
        String year = scanner.nextLine().trim();
        if (year.equals("0")) return null;

        System.out.print("CVV/CVC код: ");
        String secretCode = scanner.nextLine().trim();
        if (secretCode.equals("0")) return null;

        System.out.print("Имя, как на карте: ");
        String nameOnCard = scanner.nextLine().trim();
        if (nameOnCard.equals("0")) return null;

        System.out.print("Страна (двухбуквенный ISO-код, например, FR, DE, AR): ");
        String country = scanner.nextLine().trim().toUpperCase();
        if (country.equals("0")) return null;

        System.out.print("Номер телефона: ");
        String phone = scanner.nextLine().trim();
        if (phone.equals("0")) return null;

        CardInfo card = CardInfo.builder()
                .cardNumber(cardNumber)
                .cardType(cardType)
                .expirationMonth(month)
                .expirationYear(year)
                .secretCode(secretCode)
                .build();

        return PaymentRequestDto.builder()
                .paymentMethod("credit_card")
                .card(card)
                .nameOnCard(nameOnCard)
                .email(email)
                .country(country)
                .phoneNumber(phone)
                .termsAndConditions(true)
                .seatPreference(List.of(List.of()))
                .paidServices(new HashMap<>())
                .returnUrl("https://rail.ninja/order/success")
                .screen_height(945)
                .screen_width(1920)
                .build();
    }
}
