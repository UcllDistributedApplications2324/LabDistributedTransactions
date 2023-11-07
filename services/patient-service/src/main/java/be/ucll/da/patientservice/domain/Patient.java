package be.ucll.da.patientservice.domain;

public record Patient(Integer id, String firstName, String lastName, String email, Boolean isClient) {}