package com.panafrican.backend.service;

import com.panafrican.backend.api.dto.ContactMessageRequest;
import com.panafrican.backend.api.dto.ContactMessageResponse;
import com.panafrican.backend.api.dto.MessageAcknowledgement;
import com.panafrican.backend.api.exception.ForbiddenException;
import com.panafrican.backend.api.exception.UnauthorizedException;
import com.panafrican.backend.domain.ContactMessage;
import com.panafrican.backend.domain.UserRole;
import com.panafrican.backend.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    public MessageAcknowledgement create(ContactMessageRequest request) {
        ContactMessage message = new ContactMessage(
                UUID.randomUUID().toString(),
                request.name().trim(),
                request.email().trim(),
                request.body().trim(),
                Instant.now()
        );
        ContactMessage saved = contactMessageRepository.save(message);
        return new MessageAcknowledgement(saved.getId());
    }

    public List<ContactMessageResponse> getMessages(String userRoleHeader) {
        UserRole role = parseRole(userRoleHeader);
        if (role != UserRole.SUPER_ADMIN) {
            throw new ForbiddenException("Only super admins can view contact messages");
        }
        return contactMessageRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(message -> new ContactMessageResponse(message.getId(), message.getName(), message.getEmail(), message.getBody(), message.getCreatedAt()))
                .toList();
    }

    private UserRole parseRole(String userRoleHeader) {
        if (userRoleHeader == null || userRoleHeader.isBlank()) {
            throw new UnauthorizedException("Authentication required");
        }
        try {
            return UserRole.valueOf(userRoleHeader.trim().replace('-', '_').toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Unknown user role");
        }
    }
}
