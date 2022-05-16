package springboot.model;

import javax.persistence.*;

@Table(name = "session", indexes = {
        @Index(name = "user_id", columnList = "user_id")
})
@Entity
public class Session {
    @Id
    @Column(name = "token", nullable = false)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String id) {
        this.token = id;
    }
}