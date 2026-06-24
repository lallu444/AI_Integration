package org.example.model;

public class Member {
    private String role;
    private String location;
    private String email;
    private String experience; // raw value from CSV, e.g. "3"

    public Member(String role, String location, String email, String experience) {
        this.role = role;
        this.location = location;
        this.email = email;
        this.experience = experience;
    }

    public String getRole() { return role; }
    public String getLocation() { return location; }
    public String getEmail() { return email; }
    public String getExperience() { return experience; }
}