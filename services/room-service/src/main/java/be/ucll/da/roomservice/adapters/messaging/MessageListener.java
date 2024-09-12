package be.ucll.da.roomservice.adapters.messaging;

import be.ucll.da.roomservice.api.messaging.model.ReleaseRoomCommand;
import be.ucll.da.roomservice.api.messaging.model.ReserveRoomCommand;
import be.ucll.da.roomservice.api.messaging.model.RoomReleasedEvent;
import be.ucll.da.roomservice.api.messaging.model.RoomReservedEvent;
import be.ucll.da.roomservice.domain.RoomService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class MessageListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final RoomService roomService;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageListener(RoomService roomService, RabbitTemplate rabbitTemplate) {
        this.roomService = roomService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"q.room-service.book-room"})
    public void onBookRoom(ReserveRoomCommand command) {
        LOGGER.info("Received command: " + command);

        Integer roomId = roomService.reserveRoom(command.getDay());
        RoomReservedEvent event = new RoomReservedEvent();
        event.appointmentId(command.getAppointmentId());
        event.day(command.getDay());
        event.roomAvailable(roomId != -1);
        event.roomId(roomId);

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.room-bookings", "", event);
    }

    @RabbitListener(queues = {"q.room-service.release-room"})
    public void onReleaseRoom(ReleaseRoomCommand command) {
        LOGGER.info("Received command: " + command);
        roomService.releaseRoom(command.getDay(), command.getRoomId());

        RoomReleasedEvent event = new RoomReleasedEvent();
        event.appointmentId(command.getAppointmentId());
        event.day(command.getDay());
        event.roomId(command.getRoomId());

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.room-releases", "", event);
    }
}
