package org.acme.model.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name= "user1_id", nullable = false)
    private User user1;
    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<Message> message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }
}
