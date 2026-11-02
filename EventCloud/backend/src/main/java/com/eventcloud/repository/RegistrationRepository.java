package com.eventcloud.repository;

import com.eventcloud.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationRepository
        extends JpaRepository<EventRegistration, UUID> {

 boolean existsByEventIdAndUserId(UUID eventId, UUID userId);

 List<EventRegistration> findByUserIdOrderByRegisteredAtDesc(UUID userId);

 List<EventRegistration> findByEventIdOrderByRegisteredAtAsc(UUID eventId);

 Optional<EventRegistration> findByTicketCode(String ticketCode);

 long countByEventIdAndStatus(UUID eventId, RegistrationStatus status);
}
