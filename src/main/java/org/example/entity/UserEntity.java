package org.example.entity;

import java.util.Date;

public class UserEntity {
    private long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private CompanyEntity company;
    private Role role;
    private String title;
    private UserEntity createdBy;
    private Date createdAt;

    public UserEntity(String title, String firstName, String middleName, String lastName,
                      Date createdAt,String email) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.company = company;
        this.role = role;
        this.title = title;
    }

    public UserEntity(long id) {
        this.id = id;
    }

    public UserEntity(long id, String firstName, String middleName, String lastName, String email, String title) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.title = title;
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
    }

    public void setRole(int role) {
        this.role = Role.getRoleById(role);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CompanyEntity getCompany() {
        return company;
    }

    public Role getRole() {
        return role;
    }

    public String getTitle() {
        return title;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
