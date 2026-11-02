package com.eventcloud.controller;
import com.eventcloud.dto.EventDtos.*;
import com.eventcloud.service.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/events")
public class EventController {
 private final EventService events;private final RegistrationService registrations;
 public EventController(EventService e,RegistrationService r){events=e;registrations=r;}
 @GetMapping public List<EventResponse> list(){return events.publicEvents();}
 @GetMapping("/{id}") public EventResponse get(@PathVariable UUID id){return events.get(id);}
 @PostMapping public EventResponse create(@Valid @RequestBody EventRequest r){return events.create(r);}
 @PutMapping("/{id}") public EventResponse update(@PathVariable UUID id,@Valid @RequestBody EventRequest r){return events.update(id,r);}
 @PostMapping("/{id}/publish") public EventResponse publish(@PathVariable UUID id){return events.publish(id);}
 @PostMapping("/{id}/cancel") public EventResponse cancel(@PathVariable UUID id){return events.cancel(id);}
 @GetMapping("/managed") public List<EventResponse> managed(){return events.mine();}
 @PostMapping("/{id}/registrations") public RegistrationResponse register(@PathVariable UUID id){return registrations.register(id);}
 @GetMapping("/{id}/registrations") public List<RegistrationResponse> registrations(@PathVariable UUID id){return registrations.forEvent(id);}
}
