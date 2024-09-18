package org.acme.model.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "chat_group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "group_members", // Nome da tabela de junção
            joinColumns = @JoinColumn(name = "group_id"), // Coluna que faz referência à tabela `Group`
            inverseJoinColumns = @JoinColumn(name = "user_id") // Coluna que faz referência à tabela `User`
    )
    private List<User> members;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Message> messages;

    @OneToMany(mappedBy = "group")
    private List<GroupAdmin> admins;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
