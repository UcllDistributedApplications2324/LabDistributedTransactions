package be.ucll.da.doctorservice.domain;

public record Doctor(Long id, String fieldOfExpertise, String firstName, String lastName, Integer age, String address) {}
