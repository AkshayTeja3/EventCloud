package com.eventcloud.controller;
import com.eventcloud.domain.AppNotification;
import com.eventcloud.repository.NotificationRepository;
import com.eventcloud.service.CurrentUserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@RestController @RequestMapping("/api/notifications")
public class NotificationController {
 private final NotificationRepository repo;private final CurrentUserService current;
 public NotificationController(NotificationRepository r,CurrentUserService c){repo=r;current=c;}
 public record NotificationDto(String id,String title,String message,boolean read,java.time.Instant createdAt){}
 @GetMapping @Transactional(readOnly=true) public List<NotificationDto> list(){return repo.findTop50ByUserIdOrderByCreatedAtDesc(current.require().getId()).stream().map(n->new NotificationDto(n.getId().toString(),n.getTitle(),n.getMessage(),n.isReadFlag(),n.getCreatedAt())).toList();}
 @PostMapping("/{id}/read") @Transactional public void markRead(@PathVariable UUID id){AppNotification n=repo.findById(id).orElseThrow();if(!n.getUser().getId().equals(current.require().getId()))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN);n.markRead();}
}
