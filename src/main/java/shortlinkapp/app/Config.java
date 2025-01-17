package shortlinkapp.app;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try {
            String configFileName = "application.properties";
            properties.load(Config.class.getClassLoader().getResourceAsStream(configFileName));
        } catch (IOException | NullPointerException e) {
            System.err.println("Ошибка при загрузке конфигурационного файла: " + e.getMessage());
            e.printStackTrace(System.err); // Выводим стек вызовов в стандартный поток ошибок
            throw new RuntimeException("Не удалось загрузить конфигурационный файл: " + e.getMessage(), e);
        }
    }

    public static String getDomen() {
        return properties.getProperty("domen", "clck.ru");
    }

    public static int getMinTime() {
        return Integer.parseInt(properties.getProperty("minTime", "1"));
    }

    public static int getMaxTime() {
        return Integer.parseInt(properties.getProperty("maxTime", "7"));
    }

    public static int getMinLimit() {
        return Integer.parseInt(properties.getProperty("minLimit", "10"));
    }

    public static int getMaxLimit() {
        return Integer.parseInt(properties.getProperty("maxLimit", "100"));
    }
}

