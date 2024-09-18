package org.acme.model.dto;

import org.acme.model.entity.Message;
import org.acme.model.entity.User;

import java.util.List;

public class GroupResponseDTO {

    private Long id;

    private String name;

    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

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

}
