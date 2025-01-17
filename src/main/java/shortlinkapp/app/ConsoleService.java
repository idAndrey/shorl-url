package shortlinkapp.app;

import shortlinkapp.link.ShortLinkService;
import shortlinkapp.user.User;
import shortlinkapp.user.UserService;

import java.util.Scanner;
import java.util.UUID;

public class ConsoleService {

    private ConsoleApp consoleApp;
    private ShortLinkService shortLinkService;
    private UserService userService;
    private UUID currentUserUUID;
    public Scanner consoleIn;

    public ConsoleService(ShortLinkService ShortLinkService, UserService UserService, UUID CurrentUserUUID){
        shortLinkService = ShortLinkService;
        userService = UserService;
        currentUserUUID = CurrentUserUUID;
    }

    public ConsoleService(ConsoleApp consoleApp){
        shortLinkService = consoleApp.getShortLinkService();
        userService = consoleApp.getUserService();
        currentUserUUID = consoleApp.getCurrentUserUUID();
        consoleIn = consoleApp.getConsoleIn();
        //this.consoleApp = consoleApp;
    }


    public void updateUser(UUID uuid){
        this.currentUserUUID = uuid;
    }

    public void startService() {// Инициализация консоли и приветствие пользователя

        User currentUser = userService.getUser(currentUserUUID);

        System.out.println("\nМеню работы со ссылками\n" +
                "Логин: " + currentUser.getUserName() +
                "UUID: " + currentUser.getUserUuid());
        printLinkMenu(); // Вывод списка операций

        while (true) {
            System.out.print("> ");
            String operation = consoleIn.nextLine();


            if (operation.equals("stop")) {
                System.out.println("Возврат в главное меню.");
                break;
            }
            execLinkOperation(operation); // Обработка операции
        }
    }

    private void printLinkMenu() {
        System.out.println("Доступные команды, укажите номер:");
        System.out.println("\t1 - Создание короткой ссылки");
        System.out.println("\t2 - Просмотр всех ссылок пользователя");
        System.out.println("\t3 - Удаление ссылки <shortId>");
        System.out.println("\t4 - Изменение лимита переходов <shortId> <newLimit>");
        System.out.println("\t5 - Изменение времени жизни ссылки <shortId> <newTTL>");
        System.out.println("\t6 - Переход по ссылке <shortId>");
        System.out.println("\t7 - Удаление устаревших ссылок");
        System.out.println("\tstop: Возврат в главное меню");
        System.out.println("\tlist: Список доступных операций");
    }
    private void execLinkOperation(String operation) {
        String[] parts = operation.split(" ");
        String action = parts[0];

        // Вызов соответствующего метода для обработки команды
        try {
            switch (action) {
                case "1":
                    shortUrl();
                    break;
                case "2":
                    listUrl();
                    break;
             case "3":
                    //deleteUrl(parts);
                    break;
                case "4":
                    //editLimitUrl(parts);
                    break;
                case "5":
                    //editExpiryUrl(parts);
                    break;
                case "6":
                    gotoLink();
                    break;
                case "7":
                    //cleanUrl();
                    break;
                case "list":
                    printLinkMenu();
                    break;
                case "menu":
                    //printMainMenu();
                default:
                    System.out.println("Неизвестная команда. Введите 'list' для списка доступных команд.");
        }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void isLogged() {
        if (currentUserUUID == null) {
            throw new IllegalStateException("Необходимо авторизоваться для выполнения команды.");
        }
    }

    private void shortUrl() {

        System.out.println("Введите длинную ссылку:");
        String longURL = consoleIn.nextLine();

        isLogged(); // Проверка авторизации
        shortLinkService.createShortLink(longURL, currentUserUUID, Config.getMaxTtl(), Config.getMaxLimit());
    }

    private void listUrl() {
        isLogged();
        shortLinkService.getUserLinks(currentUserUUID, true);
    }

    private void gotoLink() {
        System.out.println("Введите идентификатор короткой ссылки:");

        String shortURL = consoleIn.nextLine();
        isLogged(); // Проверка авторизации
        shortLinkService.openInBrowser(shortLinkService.getOriginalUrl(shortURL).getOriginalUrl());
    }


}
