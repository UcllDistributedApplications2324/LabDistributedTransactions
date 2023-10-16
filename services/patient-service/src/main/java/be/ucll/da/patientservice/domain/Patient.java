package be.ucll.da.patientservice.domain;

public record Patient(Long id, String firstName, String lastName, String email, Boolean isClient) {}