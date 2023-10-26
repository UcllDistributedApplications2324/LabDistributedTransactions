package be.ucll.da.roomservice.domain;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class RoomService {

    // Returns roomID is reservation is successful, -1 otherwise
    public Integer reserveRoom(LocalDate day) {
        var roomReservations = getRoomReservations(day);

        for (var roomReservation : roomReservations.entrySet()) {
            if (!roomReservation.getValue()) { // isReserved -> false, so room is available
                roomReservations.put(roomReservation.getKey(), true);
                return roomReservation.getKey();
            }
        }

        return -1;
    }

    public Integer releaseRoom(LocalDate day, Integer roomId) {
        getRoomReservations(day).put(roomId, false);
        return roomId;
    }

    // ---- Internal Code to Simulate Rooms in a Clinic ----

    // There are 3 rooms in the clinic, every room is booked or unbooked for a full day

    // Day -> RoomID -> IsReserved
    private Map<LocalDate, Map<Integer, Boolean>> clinicReservations;

    private Map<Integer, Boolean> getRoomReservations(LocalDate day) {
        if (clinicReservations == null) {
            clinicReservations = new HashMap<>();
        }

        clinicReservations.computeIfAbsent(day, k -> fillInUnreservedRooms());
        return clinicReservations.get(day);
    }

    private Map<Integer, Boolean> fillInUnreservedRooms() {
        var newRoomMap = new HashMap<Integer, Boolean>();
        newRoomMap.put(1, false);
        newRoomMap.put(2, false);
        newRoomMap.put(3, false);

        return newRoomMap;
    }
}
