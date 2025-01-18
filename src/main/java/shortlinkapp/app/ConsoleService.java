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
                "Логин: " + currentUser.getUserName() + "\n" +
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
        System.out.println("Доступные операции, укажите номер:");
        System.out.println("\t1 - Создание короткой ссылки");
        System.out.println("\t2 - Просмотр всех ссылок пользователя");
        System.out.println("\t3 - Удаление ссылки");
        System.out.println("\t4 - Изменение лимита переходов");
        System.out.println("\t5 - Изменение времени жизни ссылки");
        System.out.println("\t6 - Переход по ссылке");
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
                    listLink();
                    break;
             case "3":
                    deleteLink();
                    break;
                case "4":
                    setLinkLimit();
                    break;
                case "5":
                    setLinkTime();
                    break;
                case "6":
                    gotoLink();
                    break;
                case "7":
                    cleanLinks();
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

    // Проверка авторизации
    private void isLogged() {
        if (currentUserUUID == null) {
            throw new IllegalStateException("Необходимо авторизоваться для выполнения команды.");
        }
    }

    private void shortUrl() {

        System.out.println("Введите длинную ссылку:");
        System.out.print("> ");
        String longURL = consoleIn.nextLine();

        isLogged();
        shortLinkService.createShortLink(longURL, currentUserUUID, Config.getMaxTime(), Config.getMaxLimit());

        System.out.println("\nВведите 'list' для вывода списка доступных операций\n" +
                "или 'stop' для возврата в главное меню.");
    }

    private void listLink() {
        isLogged();
        shortLinkService.getUserLinks(currentUserUUID, true);
        System.out.println("\nВведите 'list' для вывода списка доступных операций\n" +
                "или 'stop' для возврата в главное меню.");
    }

    private void deleteLink() {

        System.out.println("Введите короткую ссылку для удаления:");
        System.out.print("> ");
        String shortURL = consoleIn.nextLine();

        isLogged();
        shortLinkService.deleteShortLink(shortURL, currentUserUUID);
        System.out.println("\nВведите 'list' для вывода списка доступных операций\n" +
                "или 'stop' для возврата в главное меню.");
    }

    private void setLinkLimit() {

        System.out.println("Введите через пробел короткую ссылку и новый лимит переходов:");
        System.out.print("> ");
        String stringLine = consoleIn.nextLine();
        String[] stringParts = stringLine.split(" ");
        String shortURL = stringParts[0];
        String newLimit = stringParts[1];

        isLogged();
        shortLinkService.editRedirectLimit(shortURL, Integer.parseInt(newLimit), currentUserUUID);
        System.out.println("\nВведите 'list' для вывода списка доступных операций\n" +
                "или 'stop' для возврата в главное меню.");
    }

    private void setLinkTime() {

        System.out.println("Введите через пробел короткую ссылку и новое время жизни (в часах):");
        System.out.print("> ");
        String stringLine = consoleIn.nextLine();
        String[] stringParts = stringLine.split(" ");
        String shortID = stringParts[0];
        String newTime = stringParts[1];

        isLogged();
        shortLinkService.editExpiryTime(shortID, Integer.parseInt(newTime), currentUserUUID);
        System.out.println("\nВведите 'list' для вывода списка доступных операций\n" +
                "или 'stop' для возврата в главное меню.");
    }

    private void gotoLink() {
        System.out.println("Введите короткую ссылку или URL короткой ссылки для перехода\n" +
                "например, [ Zl9bWO ] или [ clck.ru/Zl9bWO ]:");
        System.out.print("> ");
        String shortID;

        String stringLine = consoleIn.nextLine();
        String[] partsLine = stringLine.split("/");
        if(partsLine.length == 1) shortID = partsLine[0];
        else shortID = partsLine[1];

        isLogged();
        shortLinkService.openInBrowser(shortLinkService.getOriginalUrl(shortID).getOriginalUrl());
        System.out.println("\nВведите 'list' для вывода списка доступных операций\n" +
                "или 'stop' для возврата в главное меню.");
    }

    private void cleanLinks() {
        isLogged(); // Проверка авторизации
        shortLinkService.cleanUpExpiredLinks(currentUserUUID);
        System.out.println("\nВведите 'list' для вывода списка доступных операций\n" +
                "или 'stop' для возврата в главное меню.");
    }

}
