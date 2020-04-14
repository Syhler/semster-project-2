package org.example.entity;


import java.util.Date;

public class UserEntity {
    private String id;
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
                      Date createdAt, String email) {
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

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName()
    {
        return firstName +" "+ middleName +" "+ lastName;
    }
    public String getNameAndTitle() { return firstName +" "+ middleName +" "+ lastName +" - "+ title;}

    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }

}
