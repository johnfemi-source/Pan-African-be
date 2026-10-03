package com.panafrican.backend.api.message;

import com.panafrican.backend.api.dto.ContactMessageRequest;
import com.panafrican.backend.api.dto.ContactMessageResponse;
import com.panafrican.backend.api.dto.MessageAcknowledgement;
import com.panafrican.backend.domain.StaffAccount;
import com.panafrican.backend.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {

    private final ContactMessageService contactMessageService;

    public MessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @PostMapping("/api/v1/messages")
    public ResponseEntity<MessageAcknowledgement> createMessage(@Valid @RequestBody ContactMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactMessageService.create(request));
    }

    @GetMapping("/api/v1/admin/messages")
    public List<ContactMessageResponse> getMessages(@AuthenticationPrincipal StaffAccount account) {
        return contactMessageService.getMessages(account.getRole().name());
    }
}
