package mvc.gateway;

public class Session {
    /** Variables **/
    private String sessionID;
    private String userFullName;
    private int currentPage;

    /** Constructors **/
    public Session() {
    }

    public Session(String sessionID) {
        this.sessionID = sessionID;
        this.userFullName = "";
        this.currentPage = 0;
    }

    public Session(String sessionID, String userFullName) {
        this.sessionID = sessionID;
        this.userFullName = userFullName;
        this.currentPage = 0;
    }

    /** ToString **/
    @Override
    public String toString() {
        return "Session{" +
                "sessionID='" + sessionID + '\'' +
                ", userFullName='" + userFullName + '\'' +
                '}';
    }

    /** Getters and Setters **/
    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int pageNumber) {
        this.currentPage = pageNumber;
    }
}
