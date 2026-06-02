package com.example.inimuws.controller.admin;

import com.example.inimuws.dto.AdminReservationSearchCondition;
import com.example.inimuws.dto.ReservationStatusUpdateRequest;
import com.example.inimuws.entity.Reservation;
import com.example.inimuws.enums.ReservationStatus;
import com.example.inimuws.service.ReservationService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public String list(@ModelAttribute("condition") AdminReservationSearchCondition condition, Model model) {
        model.addAttribute("reservations", reservationService.searchReservations(condition));
        model.addAttribute("reservationStatuses", ReservationStatus.values());
        return "admin/reservations";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findById(id);
        ReservationStatusUpdateRequest updateRequest = new ReservationStatusUpdateRequest();
        updateRequest.setStatus(reservation.getStatus());
        updateRequest.setAdminMemo(reservation.getAdminMemo());
        model.addAttribute("reservation", reservation);
        model.addAttribute("updateRequest", updateRequest);
        model.addAttribute("reservationStatuses", ReservationStatus.values());
        return "admin/reservation-detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @ModelAttribute ReservationStatusUpdateRequest request) {
        reservationService.updateStatus(id, request);
        return "redirect:/admin/reservations/" + id;
    }

    @GetMapping("/csv")
    public void csv(@ModelAttribute AdminReservationSearchCondition condition, HttpServletResponse response) throws IOException {
        List<Reservation> reservations = reservationService.searchReservations(condition);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=reservations.csv");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("予約番号,日付,時刻,人数,氏名,メール,金額,ステータス");
            for (Reservation reservation : reservations) {
                writer.printf("%s,%s,%s,%d,%s,%s,%d,%s%n",
                        csv(reservation.getReservationCode()),
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        reservation.getReservationCount(),
                        csv(reservation.customerName()),
                        csv(reservation.getCustomerEmail()),
                        reservation.getTotalAmount(),
                        reservation.getStatus().name()
                );
            }
        }
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
