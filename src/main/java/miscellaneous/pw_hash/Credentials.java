package miscellaneous.pw_hash;

public class Credentials {
    private String userName;
    private String salt;
    private String saltedHashPassword;

    public Credentials(String userName, String salt, String saltedHashPassword) {
        this.userName = userName;
        this.salt = salt;
        this.saltedHashPassword = saltedHashPassword;
    }

    @Override
    public String toString() {
        return String.format("\tUsername: %s\n\tSalt: %s\n\tSalted Hashed PW: %s", userName, salt, saltedHashPassword);
    }

    // accessors

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSaltedHashPassword() {
        return saltedHashPassword;
    }

    public void setSaltedHashPassword(String saltedHashPassword) {
        this.saltedHashPassword = saltedHashPassword;
    }
}
