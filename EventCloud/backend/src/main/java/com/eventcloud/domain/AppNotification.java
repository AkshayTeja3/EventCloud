package com.eventcloud.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="notifications", indexes=@Index(name="idx_notification_user",columnList="user_id,createdAt"))
public class AppNotification {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id") private AppUser user;
 @Column(nullable=false,length=160) private String title;
 @Column(nullable=false,length=1000) private String message;
 @Column(nullable=false) private boolean readFlag=false;
 @Column(nullable=false) private Instant createdAt=Instant.now();
 protected AppNotification(){}
 public AppNotification(AppUser user,String title,String message){this.user=user;this.title=title;this.message=message;}
 public UUID getId(){return id;} public AppUser getUser(){return user;} public String getTitle(){return title;}
 public String getMessage(){return message;} public boolean isReadFlag(){return readFlag;} public Instant getCreatedAt(){return createdAt;}
 public void markRead(){readFlag=true;}
}
