package be.ucll.da.roomservice.domain;

import java.time.LocalDate;

public record RoomAvailability(String id, LocalDate day, Boolean isAvailable) {}