package com.hospital.factory;

import com.hospital.model.*;

public class DoctorFactory {
    public static Doctor createDoctor(String type, String id, String name, int age, Gender gender, String phone, Address address, double baseFee) {
        switch (type.toUpperCase()) {
            case "CARDIOLOGY":
                return new Cardiologist(id, name, age, gender, phone, address, baseFee);
            case "NEUROLOGY":
                return new Neurologist(id, name, age, gender, phone, address, baseFee);
            case "GENERAL":
            default:
                return new GeneralPhysician(id, name, age, gender, phone, address, baseFee);
        }
    }
}

