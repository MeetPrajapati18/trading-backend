package com.meet.trading.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meet.trading.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth(); // Always initialize

    @Enumerated(EnumType.STRING)
    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER; // Default role

    @JsonProperty("isTwoFactorAuthEnabled") // Custom JSON property
    public boolean getIsTwoFactorAuthEnabled() {
        return this.twoFactorAuth != null && this.twoFactorAuth.isEnabled();
    }
}

