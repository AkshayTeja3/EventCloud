package com.eventcloud.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="event_registrations", uniqueConstraints=@UniqueConstraint(name="uk_event_user",columnNames={"event_id","user_id"}), indexes=@Index(name="idx_registration_event",columnList="event_id"))
public class EventRegistration {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="event_id") private Event event;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id") private AppUser user;
 @Column(nullable=false,unique=true,length=36) private String ticketCode;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private RegistrationStatus status=RegistrationStatus.REGISTERED;
 @Column(nullable=false) private Instant registeredAt=Instant.now();
 private Instant checkedInAt;
 protected EventRegistration(){}
 public EventRegistration(Event event,AppUser user,String ticketCode){this.event=event;this.user=user;this.ticketCode=ticketCode;}
 public UUID getId(){return id;} public Event getEvent(){return event;} public AppUser getUser(){return user;}
 public String getTicketCode(){return ticketCode;} public RegistrationStatus getStatus(){return status;}
 public Instant getRegisteredAt(){return registeredAt;} public Instant getCheckedInAt(){return checkedInAt;}
 public void cancel(){if(status==RegistrationStatus.ATTENDED)throw new IllegalStateException("Attended ticket cannot be cancelled");status=RegistrationStatus.CANCELLED;}
 public void checkIn(){if(status!=RegistrationStatus.REGISTERED)throw new IllegalStateException("Only active tickets can be checked in");status=RegistrationStatus.ATTENDED;checkedInAt=Instant.now();}
}
