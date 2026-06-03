package com.example.inimuws.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.inimuws.dto.ReservationRequest;
import com.example.inimuws.dto.ReservationResponse;
import com.example.inimuws.dto.ReservationStatusUpdateRequest;
import com.example.inimuws.entity.Reservation;
import com.example.inimuws.entity.WorkshopTimeSlot;
import com.example.inimuws.enums.ReservationStatus;
import com.example.inimuws.exception.BusinessException;
import com.example.inimuws.repository.InquiryRepository;
import com.example.inimuws.repository.ReservationRepository;
import com.example.inimuws.repository.WorkshopTimeSlotRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WorkshopTimeSlotRepository timeSlotRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Test
    void createsReservationAndIncreasesReservedCount() {
        ReservationResponse response = reservationService.createReservationOrInquiry(reservationRequest(LocalTime.of(11, 0), 2));

        assertThat(response.type()).isEqualTo("reservation");
        assertThat(response.reservationCode()).startsWith("WS-260606-");

        WorkshopTimeSlot slot = timeSlotRepository.findBySchedule_ScheduleDateOrderByStartTimeAsc(LocalDate.of(2026, 6, 6)).get(0);
        assertThat(slot.getReservedCount()).isEqualTo(2);
    }

    @Test
    void createsInquiryWhenInquiryOnlyIsTrue() {
        long beforeCount = inquiryRepository.count();
        ReservationRequest request = commonRequest();
        request.setInquiryOnly(true);
        request.setCustomerMessage("開催内容について質問があります");

        ReservationResponse response = reservationService.createReservationOrInquiry(request);

        assertThat(response.type()).isEqualTo("inquiry");
        assertThat(inquiryRepository.count()).isEqualTo(beforeCount + 1);
    }

    @Test
    void rejectsReservationWhenSlotIsFull() {
        reservationService.createReservationOrInquiry(reservationRequest(LocalTime.of(13, 0), 10));

        assertThatThrownBy(() -> reservationService.createReservationOrInquiry(reservationRequest(LocalTime.of(13, 0), 1)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("満席");
    }

    @Test
    void cancellingReservationDecreasesReservedCountOnce() {
        ReservationResponse response = reservationService.createReservationOrInquiry(reservationRequest(LocalTime.of(15, 0), 3));
        Reservation reservation = reservationRepository.findByReservationCode(response.reservationCode()).orElseThrow();

        ReservationStatusUpdateRequest updateRequest = new ReservationStatusUpdateRequest();
        updateRequest.setStatus(ReservationStatus.CANCELLED);
        reservationService.updateStatus(reservation.getId(), updateRequest);
        reservationService.updateStatus(reservation.getId(), updateRequest);

        WorkshopTimeSlot slot = timeSlotRepository.findBySchedule_ScheduleDateOrderByStartTimeAsc(LocalDate.of(2026, 6, 6)).get(2);
        assertThat(slot.getReservedCount()).isZero();
    }

    @Test
    void updatingReservationStoresParticipantBreakdownAndRecalculatesAmount() {
        ReservationResponse response = reservationService.createReservationOrInquiry(reservationRequest(LocalTime.of(11, 0), 4));
        Reservation reservation = reservationRepository.findByReservationCode(response.reservationCode()).orElseThrow();

        ReservationStatusUpdateRequest updateRequest = new ReservationStatusUpdateRequest();
        updateRequest.setStatus(ReservationStatus.CONFIRMED);
        updateRequest.setParticipantCount(3);
        updateRequest.setMaleCount(1);
        updateRequest.setFemaleCount(2);
        updateRequest.setAdminMemo("確認済み");

        Reservation updated = reservationService.updateStatus(reservation.getId(), updateRequest);

        assertThat(updated.getParticipantCount()).isEqualTo(3);
        assertThat(updated.getMaleCount()).isEqualTo(1);
        assertThat(updated.getFemaleCount()).isEqualTo(2);
        assertThat(updated.getTotalAmount()).isEqualTo(16500);
    }

    private ReservationRequest reservationRequest(LocalTime time, int count) {
        ReservationRequest request = commonRequest();
        request.setInquiryOnly(false);
        request.setReservationDate(LocalDate.of(2026, 6, 6));
        request.setReservationTime(time);
        request.setReservationCount(count);
        request.setCustomerMessage("友人と参加します");
        return request;
    }

    private ReservationRequest commonRequest() {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerFamilyName("山田");
        request.setCustomerGivenName("花子");
        request.setCustomerFamilyKana("ヤマダ");
        request.setCustomerGivenKana("ハナコ");
        request.setCustomerEmail("hanako@example.com");
        request.setCustomerTel("09012345678");
        request.setPrivacyAccepted(true);
        return request;
    }
}
