package shortlinkapp.link;

import shortlinkapp.app.Config;

import java.util.*;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class ShortLinkService {


    private final ShortLinkRepository shortLinkRepository;
    private final List<String> notifications; // Для хранения уведомлений


    public ShortLinkService(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
        this.notifications = new ArrayList<>();
    }

    public String createShortLink(String originalUrl, UUID userUuid, int userTTL, int userLimit) {

        System.out.println("\n");
        cleanUpExpiredLinks();


        if (originalUrl == null || originalUrl.isEmpty()) {
            throw new IllegalArgumentException("URL не может быть пустым");
        }
        if (userUuid == null) {
            throw new IllegalArgumentException("UUID пользователя не может быть null");
        }


        int finalTtl = Math.min(Config.getMaxTime(), Math.max(Config.getMinTime(), userTTL));
        int finalLimit = Math.max(Config.getMaxLimit(), Math.max(Config.getMinLimit(), userLimit));
        String domen = Config.getDomen();

        String shortId = generateShortId();

        String shortUrl = domen + "/" + shortId;

        long expiryTime = System.currentTimeMillis() + Math.max(1, finalTtl * 3600000L);


        ShortLink link = new ShortLink(
                shortId,
                shortUrl,
                originalUrl,
                System.currentTimeMillis(),
                expiryTime,
                finalLimit,
                0,
                userUuid
        );


        shortLinkRepository.save(link);

        System.out.println("Ссылка создана:\n" +
                "\tID ссылки:\t\t\t" + shortId + "\n" +
                "\tURL ссылки:\t\t\t" + shortUrl + "\n" +
                "\tВремя жизни:\t\t" + finalTtl + " часов" + "\n" +
                "\tЛимит переходов:\t" + finalLimit + "\n");


        return shortId;
    }


    public ShortLink getOriginalUrl(String shortId) {

        ShortLink link = shortLinkRepository.findByShortId(shortId);


        if (link == null) {
            System.out.println("Ссылка не найдена");
            notifyUser("Ссылка с идентификатором " + shortId + " не найдена.");
            throw new RuntimeException("Ссылка с идентификатором " + shortId + " не найдена.");
        }


        //System.out.println("Текущая метка времени: " + System.currentTimeMillis());
        //System.out.println("Время истечения: " + link.getExpiryTime());
        System.out.println("Срок действия ссылки истекает через: " + (link.getExpiryTime() - System.currentTimeMillis()) / 3600000);

        if (System.currentTimeMillis() > link.getExpiryTime()) {
            notifyUser("Срок действия ссылки с идентификатором " + shortId + " истёк.");
            throw new RuntimeException("Срок действия короткой ссылки истек");
        }


        System.out.println("Текущий счётчик: " + link.getCurrentCount());
        System.out.println("Лимит переходов: " + link.getLimit());
        if (link.getCurrentCount() >= link.getLimit()) {
            notifyUser("Ссылка с идентификатором " + shortId + " превысила лимит переходов.");
            throw new RuntimeException("Количество переходов по короткой ссылке превысило установленный лимит");
        }


        link.setCurrentCount(link.getCurrentCount() + 1);
        System.out.println("Счётчик обновлён: " + link.getCurrentCount());


        shortLinkRepository.save(link);


        return link;
    }

    public void editRedirectLimit(String shortId, int newLimit, UUID userUuid) {

        if (shortId == null || shortId.isEmpty()) {
            throw new IllegalArgumentException("Идентификатор ссылки не может быть пустым");
        }
        if (userUuid == null) {
            throw new IllegalArgumentException("UUID пользователя не может быть null");
        }


        ShortLink link = shortLinkRepository.findByShortId(shortId);


        if (link == null) {
            throw new RuntimeException("Ссылка с идентификатором " + shortId + " не найдена.");
        }


        if (!link.getUserUuid().equals(userUuid)) {
            throw new RuntimeException("Пользователь с UUID " + userUuid + " не имеет прав на изменение ссылки " + shortId + ".");
        }


        int adjustedLimit = Math.min(Config.getMaxLimit(), Math.max(Config.getMinLimit(), newLimit));
        if (adjustedLimit != newLimit) {
            notifyUser("Лимит " + newLimit + " был скорректирован до " + adjustedLimit + " в соответствии с системными ограничениями.");
        }


        link.setLimit(adjustedLimit);


        shortLinkRepository.save(link);


        notifyUser("Лимит переходов для ссылки " + shortId + " успешно изменён на " + adjustedLimit + ".");
    }

    public void editExpiryTime(String shortId, int newTTL, UUID userUuid) {

        if (shortId == null || shortId.isEmpty()) {
            throw new IllegalArgumentException("Идентификатор ссылки не может быть пустым");
        }
        if (userUuid == null) {
            throw new IllegalArgumentException("UUID пользователя не может быть null");
        }


        ShortLink link = shortLinkRepository.findByShortId(shortId);


        if (link == null) {
            throw new RuntimeException("Ссылка с идентификатором " + shortId + " не найдена.");
        }


        if (!link.getUserUuid().equals(userUuid)) {
            throw new RuntimeException("Пользователь с UUID " + userUuid + " не имеет прав на изменение ссылки " + shortId + ".");
        }


        int adjustedTTL = Math.min(Config.getMaxTime(), Math.max(Config.getMinTime(), newTTL));
        if (adjustedTTL != newTTL) {
            notifyUser("Срок действия " + newTTL + " часов был скорректирован до " + adjustedTTL + ".");
        }


        link.setExpiryTime(System.currentTimeMillis() + adjustedTTL * 3600000L);


        shortLinkRepository.save(link);


        notifyUser("Время жизни ссылки " + shortId + " успешно изменено на " + adjustedTTL + " часов.");
    }

    public void deleteShortLink(String shortId, UUID userUuid) {

        if (userUuid == null) {
            throw new IllegalArgumentException("UUID пользователя не может быть null");
        }


        ShortLink link = shortLinkRepository.findByShortId(shortId);


        if (link == null) {
            notifyUser("Ссылка с идентификатором " + shortId + " не найдена.");
            throw new RuntimeException("Короткая ссылка не найдена");
        }


        if (!link.getUserUuid().equals(userUuid)) {
            notifyUser("Пользователь с UUID " + userUuid + " не имеет прав на удаление ссылки с идентификатором " + shortId + ".");
            throw new RuntimeException("Нет доступа к удалению ссылки");
        }


        shortLinkRepository.deleteByShortId(shortId);


        notifyUser("Ссылка с идентификатором " + shortId + " успешно удалена.");
    }

    public void cleanUpExpiredLinks(UUID userUuid) {

        List<ShortLink> allLinks = new ArrayList<>(shortLinkRepository.findAll());
        long currentTime = System.currentTimeMillis();
        int removedCount = 0;

        for (ShortLink link : allLinks) {

            if (userUuid != null && !link.getUserUuid().equals(userUuid)) {
                continue;
            }


            if (currentTime > link.getExpiryTime() || link.getCurrentCount() >= link.getLimit()) {

                notifyUser("Удалена ссылка с идентификатором " + link.getShortId() +
                        (currentTime > link.getExpiryTime() ? " (истёк срок действия)." : " (достигнут лимит переходов)."));


                shortLinkRepository.deleteByShortId(link.getShortId());
                removedCount++;
            }
        }


        if (removedCount > 0) {
            System.out.println("Очистка завершена. Удалено " + removedCount + " устаревших ссылок.");
        } else {
            System.out.println("Очистка завершена. Устаревших ссылок не найдено.");
        }
    }

   public void cleanUpExpiredLinks() {
        cleanUpExpiredLinks(null); // Очистка всех ссылок
    }

    private String generateShortId() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortId = new StringBuilder();
        Random random = new Random();


        for (int i = 0; i < 6; i++) {
            shortId.append(chars.charAt(random.nextInt(chars.length())));
        }

        return shortId.toString();
    }


    private void notifyUser(String message) {
        notifications.add(message); // Добавляем сообщение в список
        System.out.println("Добавлено уведомление: " + message);
    }


    public List<String> getNotifications() {
        System.out.println("Текущие уведомления: " + notifications);
        return notifications; // Метод для получения уведомлений
    }


    public void clearNotifications() {
        notifications.clear();
    }

    public void openInBrowser(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("Ссылка открыта в браузере: " + url);
            } catch (IOException | URISyntaxException e) {
                System.err.println("Ошибка при попытке открыть ссылку: " + e.getMessage());
            }
        } else {
            System.err.println("Операция Desktop не поддерживается на данной системе.");
        }
    }


    public void getUserLinks(UUID userUuid, boolean printLinks) {
        List<ShortLink> userLinks = new ArrayList<>();
        for (ShortLink link : shortLinkRepository.findAll()) {
            if (link.getUserUuid().equals(userUuid)) {
                userLinks.add(link);
            }
        }

        if (printLinks) {
            if (userLinks.isEmpty()) {
                System.out.println("У вас нет созданных ссылок.");
            } else {
                System.out.println("Ваши ссылки:");
                for (ShortLink link : userLinks) {
                    System.out.printf("ID ссылки:\t" + link.getShortId() +
                            "\tURL: " + link.getShortUrl() +
                            " -> %s (лимит: %d, переходов: %d, истекает через: %d часов)\n",
                            link.getOriginalUrl(), link.getLimit(), link.getCurrentCount(),
                            (link.getExpiryTime() - System.currentTimeMillis()) / 3600000);
                }
            }
        }
    }
}