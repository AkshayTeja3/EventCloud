package com.eventcloud.controller;
import com.eventcloud.dto.EventDtos.*;
import com.eventcloud.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/registrations")
public class RegistrationController {
 private final RegistrationService service;public RegistrationController(RegistrationService s){service=s;}
 @GetMapping("/mine") public List<RegistrationResponse> mine(){return service.mine();}
 @DeleteMapping("/{id}") public RegistrationResponse cancel(@PathVariable UUID id){return service.cancel(id);}
 @PostMapping("/check-in") public RegistrationResponse checkIn(@Valid @RequestBody CheckInRequest r){return service.checkIn(r.ticketCode());}
}
