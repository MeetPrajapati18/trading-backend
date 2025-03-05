package com.meet.trading.model;

import com.meet.trading.domain.VerificationType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class TwoFactorAuth {

    private boolean isEnabled = false; // Default value to prevent null issues

    @Enumerated(EnumType.STRING) // Store as a string in the database
    private VerificationType sendTo;
}
