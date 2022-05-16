package miscellaneous.pw_hash;

public class User {
    private String userName;
    private String passwordClear;

    public User(String userName, String passwordClear) {
        this.userName = userName;
        this.passwordClear = passwordClear;
    }

    // accessors

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordClear() {
        return passwordClear;
    }

    public void setPasswordClear(String passwordClear) {
        this.passwordClear = passwordClear;
    }
}
