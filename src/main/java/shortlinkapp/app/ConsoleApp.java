package shortlinkapp.app;

import shortlinkapp.link.ShortLinkRepository;
import shortlinkapp.link.ShortLinkService;
import shortlinkapp.user.User;
import shortlinkapp.user.UserRepository;
import shortlinkapp.user.UserService;

import java.util.*;

public class ConsoleApp {

    // Сервисы для работы с юзерами и ссылками
    private shortlinkapp.link.ShortLinkService ShortLinkService = new ShortLinkService(new ShortLinkRepository());
    private shortlinkapp.user.UserService UserService = new UserService(new UserRepository());
    private UUID currentUser;
    private UUID currentUserUUID;
    private String currentUserID;
    private User currentUserObj;
    public static Scanner consoleIn = new Scanner(System.in);

    //private ConsoleService consoleService = new ConsoleService(ShortLinkService, UserService, currentUser);
    private ConsoleService consoleService = new ConsoleService(this);

    public ConsoleApp(){

    };

    // Хранение текущего пользователя
    public void startApp() {
        // Инициализация консоли и приветствие пользователя
        System.out.println("Cервис сокращения ссылок");
        printMainMenu(); // Вывод списка доступных команд

        while (true) {
            System.out.print("> ");
            String command = consoleIn.nextLine();

            // String command = scannerMain.nextLine();

            // Завершение работы приложения
            if (command.equals("exit")) {
                System.out.println("Завершение работы программы");
                break;
            }
            execCommand(command); // Обработка команды
        }
        consoleIn.close();
    }

     private void execCommand(String command) {
        String[] parts = command.split(" ");
        String action = parts[0];

        // Вызов соответствующего метода для обработки команды
        try {
            switch (action) {
                case "1":
                    registerUser(parts);
                    break;
                case "2":
                    loginUUID();
                    break;
                case "3":
                    loginUserID();
                    break;
                case "4":
                    showUser();
                    break;
                case "menu":
                    printMainMenu();
                    break;
                default:
                    System.out.println("Неизвестная команда. Введите 'menu' для списка доступных команд.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }


    private void printMainMenu() {
        System.out.println("Доступные команды, укажите номер:");
        System.out.println("\t1 - Регистрация нового пользователя");
        System.out.println("\t2 - Вход по UUID");
        System.out.println("\t3 - Вход по логину (имя пользователя)");
        System.out.println("\t4 - Текущий пользователь");
        System.out.println("\texit: Завершение работы приложения");
        System.out.println("\tmenu: Список доступных команд");
    }

    private void registerUser(String[] parts) {

        System.out.println("Введите имя пользователя для регистрации");
        System.out.print("> ");
        String userName = consoleIn.nextLine();

        UserService.createUser(userName);
    }

    private void loginUUID() {

        UUID uuid;
        uuid = null;

        System.out.println("Введите UUID для входа");
        System.out.print("> ");
        String strUUID = consoleIn.nextLine();

        try{
            uuid = UUID.fromString(strUUID);
        }
        catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        };
        if(uuid != null) {
            UserService.loginUser(uuid, newUuid -> currentUser = newUuid); // Установка текущего пользователя
            startService();
            printMainMenu();
        } else{
            System.out.println("Ошибка авторизации.");
        }

    }

    private void loginUserID() {

        UUID uuid;
        uuid = null;

        System.out.println("Введите логин (имя пользователя)");
        System.out.print("> ");
        String userName = consoleIn.nextLine();

        try{
            uuid = UserService.getUserByID(userName).getUserUuid();
        }
        catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        };
        if(uuid != null) {
            UserService.loginUser(UserService.getUserByID(userName).getUserUuid(), newUuid -> currentUser = newUuid); // Установка текущего пользователя
            startService();
            printMainMenu();
        } else{
            System.out.println("Ошибка авторизации.");
        }
    }


    private void startService() {
        consoleService.updateUser(currentUser);
        consoleService.startService();
    }

    private void isLoggedIn() {
        if (currentUser == null) {
            throw new IllegalStateException("Необходимо авторизоваться для выполнения команды.");
        }
    }

    private void showUser() {
        isLoggedIn(); // Проверка авторизации
        User user = UserService.getUser(currentUser);
        System.out.println("Текущий пользователь:\n" +
                "\tИмя пользователя (логин): " + user.getUserName() + "\n" +
                "\tUUID пользователя: " + user.getUserUuid() + "\n");
    }

    public ShortLinkService getShortLinkService(){
        return ShortLinkService;
    }


    public UserService getUserService(){
        return UserService;
    }

    public UUID getCurrentUserUUID(){
        return currentUser;
    }
    public Scanner getConsoleIn(){
        return consoleIn;
    }

}
