package com.example.inimuws.controller.admin;

import com.example.inimuws.service.InquiryService;
import com.example.inimuws.service.ReservationService;
import com.example.inimuws.service.SalesService;
import com.example.inimuws.service.ScheduleService;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ReservationService reservationService;
    private final InquiryService inquiryService;
    private final SalesService salesService;
    private final ScheduleService scheduleService;

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("todayReservationCount", reservationService.countTodayReservations());
        model.addAttribute("monthReservationCount", reservationService.countThisMonthReservations());
        model.addAttribute("monthSales", salesService.getMonthlySummary(YearMonth.now()));
        model.addAttribute("openInquiryCount", inquiryService.countOpen());
        model.addAttribute("recentReservations", reservationService.findRecentReservations());
        model.addAttribute("lowSlots", scheduleService.findLowAvailabilitySlots(3));
        return "admin/dashboard";
    }
}
