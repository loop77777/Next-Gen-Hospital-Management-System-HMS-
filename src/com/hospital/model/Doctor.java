package com.hospital.model;

public abstract class Doctor extends Person {
    protected Department department;
    protected double baseConsultationFee;

    public Doctor(String id, String name, int age, Gender gender, String phone, Address address, Department department, double baseConsultationFee) {
        super(id, name, age, gender, phone, address);
        this.department = department;
        this.baseConsultationFee = baseConsultationFee;
    }

    public Department getDepartment() { return department; }
    public abstract double calculateConsultationFee();

    @Override
    public String toString() {
        return String.format("Doctor[ID=%s, Name=%s, Dept=%s, Fee=₹%.2f]", id, name, department, calculateConsultationFee());
    }
}

