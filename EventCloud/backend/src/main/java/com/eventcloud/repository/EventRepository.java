package com.eventcloud.repository;
import com.eventcloud.domain.*;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface EventRepository extends JpaRepository<Event,UUID> {
 List<Event> findByStatusOrderByStartsAtAsc(EventStatus status);
 List<Event> findByOrganizerIdOrderByCreatedAtDesc(UUID organizerId);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select e from Event e where e.id=:id") Optional<Event> findByIdForUpdate(@Param("id") UUID id);
}
