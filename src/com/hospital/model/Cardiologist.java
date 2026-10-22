package com.hospital.model;

public class Cardiologist extends Doctor {
    public Cardiologist(String id, String name, int age, Gender gender, String phone, Address address, double fee) {
        super(id, name, age, gender, phone, address, Department.CARDIOLOGY, fee);
    }

    @Override
    public double calculateConsultationFee() {
        // Specialist surcharge
        return baseConsultationFee + 500.00;
    }
}

