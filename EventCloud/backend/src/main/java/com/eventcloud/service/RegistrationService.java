package com.eventcloud.service;
import com.eventcloud.domain.*;
import com.eventcloud.dto.EventDtos.RegistrationResponse;
import com.eventcloud.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@Service
public class RegistrationService {
 private final EventRepository events;
 private final RegistrationRepository regs;
 private final CurrentUserService current;
 private final NotificationService notifications;
 public RegistrationService(EventRepository e,RegistrationRepository r,CurrentUserService c,NotificationService n){
  events=e;regs=r;current=c;notifications=n;
 }
 @Transactional
 public RegistrationResponse register(UUID eventId){
  AppUser user=current.require();
  Event event=events.findByIdForUpdate(eventId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found"));
  if(event.getStatus()!=EventStatus.PUBLISHED)throw new ResponseStatusException(HttpStatus.CONFLICT,"This event is not open for registration");
  if(!event.getStartsAt().isAfter(java.time.Instant.now()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Registration for this event has closed");
  if(regs.existsByEventIdAndUserId(eventId,user.getId()))throw new ResponseStatusException(HttpStatus.CONFLICT,"You have already registered for this event");
  if(event.getRegisteredCount()>=event.getCapacity())throw new ResponseStatusException(HttpStatus.CONFLICT,"This event is full");
  event.incrementRegistrations();EventRegistration reg=regs.save(new EventRegistration(event,user,UUID.randomUUID().toString()));
  notifications.notify(user,"Registration confirmed","Your ticket for "+event.getTitle()+" is ready.");
  notifications.notify(event.getOrganizer(),"New registration","A user registered for "+event.getTitle()+".");
  return dto(reg);
 }
 @Transactional
 public RegistrationResponse cancel(UUID registrationId){
  AppUser user=current.require();
  EventRegistration r=regs.findById(registrationId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registration not found"));
  if(!r.getUser().getId().equals(user.getId())&&user.getRole()!=Role.ADMIN)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This ticket does not belong to you");
  if(r.getStatus()!=RegistrationStatus.REGISTERED)throw new ResponseStatusException(HttpStatus.CONFLICT,"Only active registrations can be cancelled");
  Event e=events.findByIdForUpdate(r.getEvent().getId()).orElseThrow();r.cancel();e.decrementRegistrations();return dto(r);
 }
 @Transactional
         (readOnly=true) public List<RegistrationResponse> mine(){return regs.findByUserIdOrderByRegisteredAtDesc(current.require().getId()).stream().map(this::dto).toList();}
 @Transactional
         (readOnly=true) public List<RegistrationResponse> forEvent(UUID eventId){AppUser u=current.require();Event e=events.findById(eventId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found"));if(u.getRole()!=Role.ADMIN&&!e.getOrganizer().getId().equals(u.getId()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Organizer access required");return regs.findByEventIdOrderByRegisteredAtAsc(eventId).stream().map(this::dto).toList();}
 @Transactional
 public RegistrationResponse checkIn(String ticketCode){AppUser u=current.require();EventRegistration r=regs.findByTicketCode(ticketCode).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Ticket not found"));
  Event e=r.getEvent();if(u.getRole()!=Role.ADMIN&&!e.getOrganizer().getId().equals(u.getId()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only this event's organizer can check in tickets");
  try{r.checkIn();
  }
  catch(IllegalStateException ex){
   throw new ResponseStatusException(HttpStatus.CONFLICT,ex.getMessage());}return dto(r);
 }
 public RegistrationResponse dto(EventRegistration r){
  return new RegistrationResponse(r.getId().toString(),r.getEvent().getId().toString(),r.getEvent().getTitle(),r.getTicketCode(),r.getStatus().name(),r.getRegisteredAt(),r.getCheckedInAt());
 }
}
