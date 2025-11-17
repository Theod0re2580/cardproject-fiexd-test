package com.example.cardtest.web;

import com.example.cardtest.domain.Role;
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
