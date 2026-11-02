package com.eventcloud.controller;
import com.eventcloud.domain.*;
import com.eventcloud.repository.*;
import com.eventcloud.service.CurrentUserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
 private final EventRepository events;
 private final RegistrationRepository regs;
 private final CurrentUserService current;
 public AnalyticsController(EventRepository e,RegistrationRepository r,CurrentUserService c){
  events=e;
  regs=r;
  current=c;
 }
 public record Summary(long events,long registrations,long attendees,long capacity,long availableSeats){

 }
 @GetMapping("/summary") @Transactional(readOnly=true) public Summary summary(){
  AppUser u=current.require();
  if(u.getRole()==Role.USER)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Organizer or admin access required");
  List<Event> list=u.getRole()==Role.ADMIN?events.findAll():events.findByOrganizerIdOrderByCreatedAtDesc(u.getId());
  long totalRegs=0,attendees=0,capacity=0,available=0;
  for(Event e:list){totalRegs+=regs.countByEventIdAndStatus(e.getId(),RegistrationStatus.REGISTERED)+regs.countByEventIdAndStatus(e.getId(),RegistrationStatus.ATTENDED);
   attendees+=regs.countByEventIdAndStatus(e.getId(),RegistrationStatus.ATTENDED);
   capacity+=e.getCapacity();
   available+=Math.max(0,e.getCapacity()-e.getRegisteredCount());
  }
  return new Summary(list.size(),totalRegs,attendees,capacity,available);
 }
}
