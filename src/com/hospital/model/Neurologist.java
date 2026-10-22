package com.hospital.model;

public class Neurologist extends Doctor {
    public Neurologist(String id, String name, int age, Gender gender, String phone, Address address, double fee) {
        super(id, name, age, gender, phone, address, Department.NEUROLOGY, fee);
    }

    @Override
    public double calculateConsultationFee() {
        return baseConsultationFee + 700.00;
    }
}
