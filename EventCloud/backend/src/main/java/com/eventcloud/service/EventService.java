package com.eventcloud.service;
import com.eventcloud.domain.*;
import com.eventcloud.dto.EventDtos.*;
import com.eventcloud.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.*;
@Service
public class EventService {
 private final EventRepository events;private final RegistrationRepository registrations;private final CurrentUserService current;private final NotificationService notifications;
 public EventService(EventRepository e,RegistrationRepository r,CurrentUserService c,NotificationService n){events=e;registrations=r;current=c;notifications=n;}
 @Transactional(readOnly=true) public List<EventResponse> publicEvents(){return events.findByStatusOrderByStartsAtAsc(EventStatus.PUBLISHED).stream().map(this::dto).toList();}
 @Transactional(readOnly=true) public EventResponse get(UUID id){return dto(events.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found")));}
 @Transactional public EventResponse create(EventRequest r){
  validateDates(r);AppUser user=current.require();if(user.getRole()==Role.USER)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Organizer role required");
  Event e=events.save(new Event(r.title().trim(),r.description().trim(),r.venue().trim(),r.startsAt(),r.endsAt(),r.capacity(),user));return dto(e);
 }
 @Transactional public EventResponse update(UUID id,EventRequest r){
  validateDates(r);AppUser user=current.require();Event e=events.findByIdForUpdate(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found"));ensureManager(e,user);
  if(e.getStatus()==EventStatus.CANCELLED||e.getStatus()==EventStatus.COMPLETED)throw new ResponseStatusException(HttpStatus.CONFLICT,"Closed events cannot be edited");
  try{e.update(r.title().trim(),r.description().trim(),r.venue().trim(),r.startsAt(),r.endsAt(),r.capacity());}catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.CONFLICT,ex.getMessage());}
  return dto(e);
 }
 @Transactional public EventResponse publish(UUID id){AppUser user=current.require();Event e=events.findByIdForUpdate(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found"));ensureManager(e,user);e.publish();return dto(e);}
 @Transactional public EventResponse cancel(UUID id){AppUser user=current.require();Event e=events.findByIdForUpdate(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found"));ensureManager(e,user);e.cancel();return dto(e);}
 @Transactional(readOnly=true) public List<EventResponse> mine(){AppUser u=current.require();if(u.getRole()==Role.USER)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Organizer role required");if(u.getRole()==Role.ADMIN)return events.findAll().stream().map(this::dto).toList();return events.findByOrganizerIdOrderByCreatedAtDesc(u.getId()).stream().map(this::dto).toList();}
 private void ensureManager(Event e,AppUser u){if(u.getRole()!=Role.ADMIN&&!e.getOrganizer().getId().equals(u.getId()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You do not manage this event");}
 private void validateDates(EventRequest r){if(!r.endsAt().isAfter(r.startsAt()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"End time must be after start time");if(r.startsAt().isBefore(Instant.now().minusSeconds(60)))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Event start time must be in the future");}
 public EventResponse dto(Event e){return new EventResponse(e.getId().toString(),e.getTitle(),e.getDescription(),e.getVenue(),e.getStartsAt(),e.getEndsAt(),e.getCapacity(),e.getRegisteredCount(),Math.max(0,e.getCapacity()-e.getRegisteredCount()),e.getPosterUrl(),e.getStatus().name(),e.getOrganizer().getId().toString(),e.getOrganizer().getFullName());}
}
