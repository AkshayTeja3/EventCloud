package com.eventcloud.dto;
import jakarta.validation.constraints.*;
import java.time.Instant;
public class EventDtos {
 public record EventRequest(@NotBlank @Size(max=160) String title,@NotBlank @Size(max=5000) String description,@NotBlank @Size(max=180) String venue,@NotNull Instant startsAt,@NotNull Instant endsAt,@Min(1) @Max(100000) int capacity){}
 public record EventResponse(String id,String title,String description,String venue,Instant startsAt,Instant endsAt,int capacity,int registeredCount,int availableSeats,String posterUrl,String status,String organizerId,String organizerName){}
 public record RegistrationResponse(String id,String eventId,String eventTitle,String ticketCode,String status,Instant registeredAt,Instant checkedInAt){}
 public record CheckInRequest(@NotBlank String ticketCode){}
}
