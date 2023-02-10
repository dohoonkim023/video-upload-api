package com.example.videouploadapi.api;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class formController {

    @RequestMapping("/form")
    public String form() {
        return "form";
    }


}
