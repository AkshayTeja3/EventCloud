package com.eventcloud.service;
import com.eventcloud.domain.*;
import com.eventcloud.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class NotificationService {
 private final NotificationRepository repo;
 public NotificationService(NotificationRepository repo){this.repo=repo;}
 @Transactional public void notify(AppUser user,String title,String message){repo.save(new AppNotification(user,title,message));}
}
