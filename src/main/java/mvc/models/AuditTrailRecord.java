package mvc.models;

import mvc.controllers.MyController;

public class AuditTrailRecord implements MyController {
    private String date_time;
    private String created_by;
    private String changed_msg;

    public AuditTrailRecord(String date_time, String created_by, String changed_msg) {
        this.date_time = date_time;
        this.created_by = created_by;
        this.changed_msg = changed_msg;
    }

    @Override
    public String toString() {
        return
                "date_Created'" + date_time + '\'' +
                ", Created by='" + created_by + '\'' +
                ", Change msg='" + changed_msg + '\'';
    }

    /** Getters and Setters **/
    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getChanged_msg() {
        return changed_msg;
    }

    public void setChanged_msg(String changed_msg) {
        this.changed_msg = changed_msg;
    }
}
