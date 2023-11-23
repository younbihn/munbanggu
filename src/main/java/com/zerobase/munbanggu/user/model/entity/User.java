package com.zerobase.munbanggu.user.model.entity;

import com.zerobase.munbanggu.user.type.AuthProvider;
import com.zerobase.munbanggu.user.type.LoginType;
import com.zerobase.munbanggu.user.type.Role;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import reactor.util.annotation.Nullable;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Table(name="\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true)
    private String email;

    private String name;

    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    @Nullable
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    private String phone;

    private String profileImageUrl;

    @Builder.Default
    private int failedCount = 0;

    @CreatedDate
    private LocalDateTime createdDate;

    public String getRoleKey() {
        return this.role.getKey();
    }

    private LocalDateTime deleteDate;
}
