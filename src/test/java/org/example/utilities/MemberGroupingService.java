package org.example.utilities;

import org.example.model.JobSearchGroup;
import org.example.model.Member;

import java.util.*;

public class MemberGroupingService {

    public List<JobSearchGroup> groupMembers(List<Member> members) {
        Map<String, JobSearchGroup> groupMap = new LinkedHashMap<>();

        for (Member m : members) {
            String key = buildGroupKey(m);

            JobSearchGroup group = groupMap.get(key);
            if (group == null) {
                group = new JobSearchGroup(normalize(m.getRole()), normalize(m.getLocation()), bucketExperience(m.getExperience()));
                groupMap.put(key, group);
            }
            group.addMember(m.getEmail());
        }
        return new ArrayList<>(groupMap.values());
    }

    private String buildGroupKey(Member m) {
        return normalize(m.getRole()) + "_" + normalize(m.getLocation()) + "_" + bucketExperience(m.getExperience());
    }

    private String normalize(String value) {
        return value.toLowerCase().trim().replaceAll("\\s+", " ");
    }

    // Buckets exact years into bands matching Naukri's experience filter ranges
    private String bucketExperience(String expStr) {
        int exp;
        try {
            exp = Integer.parseInt(expStr.trim());
        } catch (NumberFormatException e) {
            return "unknown";
        }
        if (exp <= 2) return "0-2";
        if (exp <= 5) return "3-5";
        if (exp <= 8) return "6-8";
        return "9+";
    }
}