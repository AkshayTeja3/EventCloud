package com.eventcloud.repository;
import com.eventcloud.domain.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface NotificationRepository extends JpaRepository<AppNotification,UUID> {
 List<AppNotification> findTop50ByUserIdOrderByCreatedAtDesc(UUID userId);
}
