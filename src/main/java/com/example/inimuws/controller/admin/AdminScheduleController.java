package com.example.inimuws.controller.admin;

import com.example.inimuws.dto.ScheduleCreateRequest;
import com.example.inimuws.dto.ScheduleUpdateRequest;
import com.example.inimuws.dto.TimeSlotCreateRequest;
import com.example.inimuws.dto.TimeSlotUpdateRequest;
import com.example.inimuws.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/schedules")
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("schedules", scheduleService.findAllSchedules());
        model.addAttribute("scheduleCreateRequest", new ScheduleCreateRequest());
        model.addAttribute("timeSlotCreateRequest", new TimeSlotCreateRequest());
        return "admin/schedules";
    }

    @PostMapping
    public String createSchedule(@Valid @ModelAttribute ScheduleCreateRequest request) {
        scheduleService.createSchedule(request);
        return "redirect:/admin/schedules";
    }

    @PostMapping("/{id}")
    public String updateSchedule(@PathVariable Long id, @Valid @ModelAttribute ScheduleUpdateRequest request) {
        scheduleService.updateSchedule(id, request);
        return "redirect:/admin/schedules";
    }

    @PostMapping("/slots")
    public String createTimeSlot(@Valid @ModelAttribute TimeSlotCreateRequest request) {
        scheduleService.createTimeSlot(request);
        return "redirect:/admin/schedules";
    }

    @PostMapping("/slots/{id}")
    public String updateTimeSlot(@PathVariable Long id, @Valid @ModelAttribute TimeSlotUpdateRequest request) {
        scheduleService.updateTimeSlot(id, request);
        return "redirect:/admin/schedules";
    }
}
