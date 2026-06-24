package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class JobSearchGroup {
    private String role;
    private String location;
    private String experience;
    private List<String> memberEmails = new ArrayList<>();

    public JobSearchGroup(String role, String location, String experience) {
        this.role = role;
        this.location = location;
        this.experience = experience;
    }

    public void addMember(String email) { memberEmails.add(email); }

    public String getRole() { return role; }
    public String getLocation() { return location; }
    public String getExperience() { return experience; }
    public List<String> getMemberEmails() { return memberEmails; }
}