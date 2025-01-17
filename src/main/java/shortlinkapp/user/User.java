package shortlinkapp.user;

import java.util.UUID;

public class User {

    private UUID userUuid;

    private String name;

    private String userID;

    @SuppressWarnings("unused")
    public User() {
    }

    public User(UUID userUuid, String name) {
        this.userUuid = userUuid;
        this.name = name;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    @SuppressWarnings("unused")
    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setUserName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User { userUuid=" + userUuid + ", name='" + name + "' }";
    }
}