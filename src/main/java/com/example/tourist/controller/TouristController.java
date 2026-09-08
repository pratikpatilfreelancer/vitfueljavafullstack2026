package com.example.tourist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.tourist.model.Tourist;
import com.example.tourist.service.TouristService;

@Controller
public class TouristController {

    @Autowired
    private TouristService touristService;

    // 1. Display list of tourists (Home page)
    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("listTourists", touristService.getAllTourists());
        return "index"; // loads index.html from src/main/resources/templates/
    }

    // 2. Show Add New Tourist Form
    @GetMapping("/showNewTouristForm")
    public String showNewTouristForm(Model model) {
        Tourist tourist = new Tourist();
        model.addAttribute("tourist", tourist);
        return "new_tourist"; // loads new_tourist.html
    }

    // 3. Save Tourist to Database
    @PostMapping("/saveTourist")
    public String saveTourist(@ModelAttribute("tourist") Tourist tourist) {
        touristService.saveTourist(tourist);
        return "redirect:/"; // redirects back to home page
    }

    // 4. Show Update Form pre-populated with existing data
    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        Tourist tourist = touristService.getTouristById(id);
        model.addAttribute("tourist", tourist);
        return "update_tourist"; // loads update_tourist.html
    }

    // 5. Delete Tourist
    @GetMapping("/deleteTourist/{id}")
    public String deleteTourist(@PathVariable(value = "id") Long id) {
        touristService.deleteTouristById(id);
        return "redirect:/";
    }
}