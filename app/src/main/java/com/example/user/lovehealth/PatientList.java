package com.example.user.lovehealth;

public class PatientList {
    private String pat_id, pat_name, pat_age, pat_gender;

    public PatientList() {
    }

    public PatientList(String pat_id, String pat_name, String pat_age, String pat_gender) {
        this.pat_id = pat_id;
        this.pat_name = pat_name;
        this.pat_age = pat_age;
        this.pat_gender = pat_gender;
    }

    public String getPat_id() {
        return pat_id;
    }

    public String getPat_name() {
        return pat_name;
    }

    public String getPat_age() {
        return pat_age;
    }

    public String getPat_gender() {
        return pat_gender;
    }
}
