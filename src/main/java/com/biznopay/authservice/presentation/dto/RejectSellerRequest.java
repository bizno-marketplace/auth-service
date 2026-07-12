package com.biznopay.authservice.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record RejectSellerRequest(
        @NotEmpty(message = "Reason for rejection is required")
        String reasonForRejection
) {
}
