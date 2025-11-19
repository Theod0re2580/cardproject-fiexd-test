package com.example.cardtest.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionMember {
    private Long id;
    private String loginId;
    private Role role;
}
