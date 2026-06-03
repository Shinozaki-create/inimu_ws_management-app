package com.example.inimuws.dto;

import com.example.inimuws.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationStatusUpdateRequest {

    @NotNull
    private ReservationStatus status;

    private Integer participantCount;
    private Integer maleCount;
    private Integer femaleCount;

    private String adminMemo;
}
