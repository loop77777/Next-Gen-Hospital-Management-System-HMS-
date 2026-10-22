package com.hospital.service;

import com.hospital.dao.PatientDAOImpl;
import com.hospital.dto.PatientDTO;
import com.hospital.exception.InvalidPatientDataException;
import com.hospital.exception.PatientNotFoundException;
import com.hospital.model.Address;
import com.hospital.model.Patient;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class PatientService {
    private final PatientDAOImpl patientDAO;
    private final AtomicLong idSequence = new AtomicLong(10001);

    public PatientService(PatientDAOImpl patientDAO) {
        this.patientDAO = patientDAO;
    }

    public Patient registerPatient(PatientDTO dto) {
        // Validate payload
        if (dto.getPhone() == null || dto.getPhone().length() < 10) {
            throw new InvalidPatientDataException("Invalid phone number provided.");
        }

        // Check for duplicates
        patientDAO.findByPhone(dto.getPhone()).ifPresent(p -> {
            throw new InvalidPatientDataException("Patient with phone " + dto.getPhone() + " already exists!");
        });

        String generatedId = "PAT-" + idSequence.getAndIncrement();
        Address address = new Address(dto.getStreet(), dto.getCity(), dto.getState(), dto.getZipCode());

        Patient patient = new Patient.Builder()
                .setId(generatedId)
                .setName(dto.getName())
                .setAge(dto.getAge())
                .setGender(dto.getGender())
                .setPhone(dto.getPhone())
                .setAddress(address)
                .setBloodGroup(dto.getBloodGroup())
                .setInsurancePolicyNo(dto.getInsurancePolicyNo())
                .build();

        patientDAO.save(patient);
        return patient;
    }

    public Patient getPatient(String patientId) {
        return patientDAO.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient #" + patientId + " not found."));
    }

    // Java 8 Stream API filtering
    public List<Patient> getSeniorCitizens() {
        return patientDAO.findAll().stream()
                .filter(p -> p.getAge() >= 60)
                .collect(Collectors.toList());
    }
}