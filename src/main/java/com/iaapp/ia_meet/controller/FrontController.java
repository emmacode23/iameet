package com.iaapp.ia_meet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping
public class FrontController {

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @PostMapping("/meet/create")
    public String createMeet(){
        String roomId = UUID.randomUUID().toString();
        return "redirect:/meet/" + roomId;
    }

    @GetMapping("/meet/{roomId}")
    public String meet(@PathVariable String roomId, Model model){
        model.addAttribute("roomId", roomId);
        return "meet";
    }
}
