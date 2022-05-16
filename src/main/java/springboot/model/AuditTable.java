package springboot.model;

import javax.persistence.*;
import java.time.Instant;

@Table(name = "audit_table", indexes = {
        @Index(name = "changed_by", columnList = "changed_by"),
        @Index(name = "person_id", columnList = "person_id")
})
@Entity
public class AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "change_msg", nullable = false, length = 100)
    private String changeMsg;

    @ManyToOne(optional = false)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "when_occured", nullable = false)
    private Instant whenOccured;

    public Instant getWhenOccured() {
        return whenOccured;
    }

    public void setWhenOccured(Instant whenOccured) {
        this.whenOccured = whenOccured;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangeMsg() {
        return changeMsg;
    }

    public void setChangeMsg(String changeMsg) {
        this.changeMsg = changeMsg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}