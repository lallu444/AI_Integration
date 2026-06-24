package org.example.utilities;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.model.Member;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvMemberReader {

    // Expects CSV header: role,location,mail,experience
    public List<Member> readMembers(String csvPath) throws IOException {
        List<Member> members = new ArrayList<>();
        try (FileReader reader = new FileReader(csvPath);
             CSVParser parser = CSVParser.parse(reader,
                     CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            for (CSVRecord record : parser) {
                String role = record.get("role").trim();
                String location = record.get("location").trim();
                String email = record.get("mail").trim();
                String experience = record.get("experience").trim();

                if (role.isEmpty() || location.isEmpty() || email.isEmpty()) {
                    System.out.println("Skipping invalid row: " + record);
                    continue;
                }
                members.add(new Member(role, location, email, experience));
            }
        }
        return members;
    }
}