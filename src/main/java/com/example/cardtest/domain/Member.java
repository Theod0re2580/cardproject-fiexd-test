package com.example.cardtest.domain;

import jakarta.persistence.*;
import lombok.*;

import com.example.cardtest.domain.Role;
import java.time.LocalDate;

@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @Column(name = "MEMBER_ID")
    @SequenceGenerator(
            name = "member_seq_generator",
            sequenceName = "SEQ_MEMBER", // ❗ 없으면 MEMBER_SEQ 만들어줘
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    private Long id;

    @Column(name = "MEMBER_LOGIN_ID", nullable = false, unique = true)
    private String loginId;

    @Column(name = "MEMBER_PW", nullable = false)
    private String password;

    @Column(name = "MEMBER_NAME", nullable = false)
    private String name;

    @Column(name = "MEMBER_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "MEMBER_BIRTH", nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBER_ROLE", nullable = false)
    private Role role;
}
