package shortlinkapp.app;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try {
            // Определяем окружение и имя файла конфигурации
            String env = System.getProperty("env", "default");
            String configFileName = "application.properties";
            if ("test".equals(env)) {
                configFileName = "testconfig.properties";
            }
            // Загружаем свойства из файла конфигурации
            properties.load(Config.class.getClassLoader().getResourceAsStream(configFileName));
        } catch (IOException | NullPointerException e) {
            System.err.println("Ошибка при загрузке конфигурационного файла: " + e.getMessage());
            e.printStackTrace(System.err); // Выводим стек вызовов в стандартный поток ошибок
            throw new RuntimeException("Не удалось загрузить конфигурационный файл: " + e.getMessage(), e);
        }
    }

    public static int getMinTtl() {
        return Integer.parseInt(properties.getProperty("config.ttl.min", "1"));
    }

    public static int getMaxTtl() {
        return Integer.parseInt(properties.getProperty("config.ttl.max", "48"));
    }

    public static int getMinLimit() {
        return Integer.parseInt(properties.getProperty("config.limit.min", "1"));
    }

    public static int getMaxLimit() {
        return Integer.parseInt(properties.getProperty("config.limit.max", "100"));
    }
}

