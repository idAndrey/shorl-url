package shortlinkapp.link;

import java.util.UUID;

public class ShortLink {

   private String shortId;


    private String originalUrl;


    private long createdAt;


    private long expiryTime;


    private int limit;


    private int currentCount;


    private UUID userUuid;


    @SuppressWarnings("unused")
    public ShortLink() {
    }


    public ShortLink(String shortId,
                     String originalUrl,
                     long createdAt,
                     long expiryTime,
                     int limit,
                     int currentCount,
                     UUID userUuid) {
        this.shortId = shortId;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expiryTime = expiryTime;
        this.limit = limit;
        this.currentCount = currentCount;
        this.userUuid = userUuid;
    }


    public String getShortId() {
        return shortId;
    }


    public String getOriginalUrl() {
        return originalUrl;
    }


    public long getExpiryTime() {
        return expiryTime;
    }


    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }


    public int getLimit() {
        return limit;
    }


    public void setLimit(int limit) {
        this.limit = limit;
    }


    public int getCurrentCount() {
        return currentCount;
    }


    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }


    public UUID getUserUuid() {
        return userUuid;
    }


    @Override
    public String toString() {
        return "ShortLink{" +
                "shortId='" + shortId + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", createdAt=" + createdAt +
                ", expiryTime=" + expiryTime +
                ", limit=" + limit +
                ", currentCount=" + currentCount +
                ", userUuid=" + userUuid +
                '}';
    }
}
