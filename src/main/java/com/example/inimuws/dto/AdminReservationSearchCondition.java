package com.example.inimuws.dto;

import com.example.inimuws.enums.ReservationStatus;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class AdminReservationSearchCondition {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    private ReservationStatus status;

    private String keyword;
}
