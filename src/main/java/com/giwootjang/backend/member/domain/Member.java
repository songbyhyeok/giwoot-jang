package com.giwootjang.backend.member.domain;

import com.giwootjang.backend.member.domain.type.MemberLoginType;
import com.giwootjang.backend.member.domain.type.MemberStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNo;

    @Column(name = "member_id", length = 20, nullable = false, unique = true)
    private String memberId;

    @Column(name = "name", length = 4, nullable = false)
    private String name;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", length = 40, nullable = false)
    private String email;

    @Column(name = "phone", length = 13, nullable = false)
    private String phone;

    @Column(name = "profile", length = 100)
    private String profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private MemberLoginType loginType;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "points", precision = 10, scale = 2)
    private BigDecimal points;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    private Member(String id, String name, String pwd, String email,
                   String phone, String profile, MemberStatus status, MemberLoginType loginType,
                   BigDecimal amount, BigDecimal points) {
        this.memberId = id;
        this.name = name;
        this.password = pwd;
        this.email = email;
        this.phone = phone;
        this.profile = profile;
        this.status = status;
        this.loginType = loginType;
        this.amount = amount;
        this.points = points;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = createdAt;
    }

    public static Member of(String id, String name, String pwd, String email,
                            String phone, String profile, MemberStatus status, MemberLoginType loginType,
                            BigDecimal amount, BigDecimal points
                            ) {
        return new Member(id, name, pwd, email, phone, profile, status, loginType, amount, points);
    }
}

