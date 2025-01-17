package shortlinkapp.link;

import java.util.*;

public class ShortLinkRepository {


    private final Map<String, ShortLink> storage = new HashMap<>();


    public void save(ShortLink link) {
        storage.put(link.getShortId(), link);
    }


    public ShortLink findByShortId(String shortId) {
        return storage.get(shortId);
    }


    public void deleteByShortId(String shortId) {
        storage.remove(shortId);
    }


    public Collection<ShortLink> findAll() {
        return storage.values();
    }
}
