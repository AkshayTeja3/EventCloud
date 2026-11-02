package com.eventcloud.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="events", indexes={@Index(name="idx_event_start",columnList="startsAt"),@Index(name="idx_event_status",columnList="status")})
public class Event {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false,length=160) private String title;
 @Column(nullable=false,length=5000) private String description;
 @Column(nullable=false,length=180) private String venue;
 @Column(nullable=false) private Instant startsAt;
 @Column(nullable=false) private Instant endsAt;
 @Column(nullable=false) private int capacity;
 @Column(nullable=false) private int registeredCount=0;
 @Column(length=500) private String posterUrl;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private EventStatus status=EventStatus.DRAFT;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organizer_id") private AppUser organizer;
 @Column(nullable=false) private Instant createdAt=Instant.now();
 @Version private long version;
 protected Event(){}
 public Event(String title,String description,String venue,Instant startsAt,Instant endsAt,int capacity,AppUser organizer){
  this.title=title;this.description=description;this.venue=venue;this.startsAt=startsAt;this.endsAt=endsAt;this.capacity=capacity;this.organizer=organizer;
 }
 public UUID getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;}
 public String getVenue(){return venue;} public Instant getStartsAt(){return startsAt;} public Instant getEndsAt(){return endsAt;}
 public int getCapacity(){return capacity;} public int getRegisteredCount(){return registeredCount;} public String getPosterUrl(){return posterUrl;}
 public EventStatus getStatus(){return status;} public AppUser getOrganizer(){return organizer;} public Instant getCreatedAt(){return createdAt;}
 public void update(String title,String description,String venue,Instant startsAt,Instant endsAt,int capacity){
  if(capacity<registeredCount) throw new IllegalArgumentException("Capacity cannot be below current registrations");
  this.title=title;this.description=description;this.venue=venue;this.startsAt=startsAt;this.endsAt=endsAt;this.capacity=capacity;
 }
 public void publish(){if(status==EventStatus.CANCELLED) throw new IllegalStateException("Cancelled event cannot be published");status=EventStatus.PUBLISHED;}
 public void cancel(){status=EventStatus.CANCELLED;} public void complete(){status=EventStatus.COMPLETED;}
 public void setPosterUrl(String posterUrl){this.posterUrl=posterUrl;}
 public void incrementRegistrations(){if(registeredCount>=capacity)throw new IllegalStateException("Event is full");registeredCount++;}
 public void decrementRegistrations(){if(registeredCount>0)registeredCount--;}
}
