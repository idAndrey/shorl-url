package shortlinkapp.user;

import java.util.UUID;
import java.util.function.Consumer;


public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void createUser(String name) {
        User user = userRepository.saveUser(new User(UUID.randomUUID(), name));
        System.out.println("Пользователь зарегистрирован.\n" +
                "Ваш UUID: " + user.getUserUuid() + "\n" +
                "Ваш логин: " + user.getUserName());
    }


    public void loginUser(UUID uuid, Consumer<UUID> setCurrentUser) {
        User user = userRepository.findUser(uuid);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с таким UUID не найден.");
        }
        setCurrentUser.accept(uuid); // Устанавливаем текущего пользователя
        System.out.println("Вы вошли как " + user.getUserName() + "\n" +
                "Ваш UUID: " + user.getUserUuid());
    }

    public User getUser(UUID uuid) {
        User user = userRepository.findUser(uuid);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с UUID " + uuid + " не найден.");
        }
        return user;
    }

    public User getUserByID(String name) {
        User user = userRepository.getUserByID(name);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с ID " + name + " не найден.");
        }
        return user;
    }
}