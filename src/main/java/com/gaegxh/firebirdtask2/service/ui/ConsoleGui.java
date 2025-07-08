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

    public void start() throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Ввод маршрута
        System.out.print("Введите город отправления: ");
        String from = scanner.nextLine().trim();

        System.out.print("Введите город прибытия: ");
        String to = scanner.nextLine().trim();

        if (!InputValidator.validateCities(from, to)) {
            System.out.println("Неверный маршрут. Допустимые маршруты: Paris-Lille, Lille-Paris, Lisbon-Porto, Porto-Lisbon");
            return;
        }

        // Step 2: Ввод даты
        System.out.print("Введите дату поездки (dd.MM.yyyy или yyyy-MM-dd): ");
        String rawDate = scanner.nextLine().trim();
        String formattedDate = InputValidator.validateAndFormatDate(rawDate);
        if (formattedDate == null) {
            System.out.println("Некорректная дата.");
            return;
        }

        // Step 3: Выбор режима
        System.out.println("Выберите режим:\n1 - Поиск\n2 - Покупка");
        String mode = scanner.nextLine().trim();

        String bestCriteria;
        if (mode.equals("2")) {
            System.out.print("Введите желаемое время отправления (HH:mm): ");
            String timeInput = scanner.nextLine().trim();
            if (!InputValidator.validateTime(timeInput)) {
                System.out.println("Некорректный формат времени.");
                return;
            }
            bestCriteria = "CLOSEST_TO_TIME:" + timeInput;
        } else {
            bestCriteria = "EARLIEST_DEPARTURE";
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
            searchService.search(searchRequest);
            return;
        }

        Passenger passenger = collectPassenger(scanner);
        if (passenger == null) return;
        PassengerRequest passengerRequest = new PassengerRequest();
        passengerRequest.setPassengers(List.of(passenger));

        System.out.print("Введите email: ");
        String email = scanner.nextLine().trim();
        if (!InputValidator.validateEmail(email)) {
            System.out.println("Некорректный email.");
            return;
        }

        ClientDetailsRequest clientDetails = ClientDetailsRequest.builder()
                .user_email(email)
                .confirm_user_email(email)
                .build();

        PaymentRequestDto payment = collectPayment(scanner, email);
        if (payment == null) return;

        sellService.sell(searchRequest, passengerRequest, clientDetails, payment);
    }

    private Passenger collectPassenger(Scanner scanner) {
        System.out.print("Имя: ");
        String first = scanner.nextLine().trim();
        System.out.print("Фамилия: ");
        String last = scanner.nextLine().trim();
        System.out.print("Пол (mr/mrs): ");
        String title = scanner.nextLine().trim().toLowerCase();

        System.out.print("Дата рождения (yyyy-MM-dd или dd.MM.yyyy): ");
        String dobInput = scanner.nextLine().trim();
        String dob = InputValidator.validateAndFormatDob(dobInput);

        if (first.isEmpty() || last.isEmpty() || (!title.equals("mr") && !title.equals("mrs")) || dob == null) {
            System.out.println("Неверные данные пассажира.");
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

        System.out.print("Тип карты (visa/mastercard): ");
        String cardType = scanner.nextLine().trim().toLowerCase();

        System.out.print("Месяц окончания срока действия (MM): ");
        String month = scanner.nextLine().trim();

        System.out.print("Год окончания срока действия (yyyy): ");
        String year = scanner.nextLine().trim();

        System.out.print("CVV/CVC код: ");
        String secretCode = scanner.nextLine().trim();

        System.out.print("Имя, как на карте: ");
        String nameOnCard = scanner.nextLine().trim();

        System.out.print("Страна (двухбуквенный ISO-код, например, FR, DE, AR): ");
        String country = scanner.nextLine().trim().toUpperCase();

        System.out.print("Номер телефона: ");
        String phone = scanner.nextLine().trim();

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
