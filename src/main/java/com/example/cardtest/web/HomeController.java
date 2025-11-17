package com.example.cardtest.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** 메인 페이지 (index.html) */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
