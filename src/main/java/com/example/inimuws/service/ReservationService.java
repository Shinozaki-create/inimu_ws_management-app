package com.example.inimuws.service;

import com.example.inimuws.dto.AdminReservationSearchCondition;
import com.example.inimuws.dto.ReservationRequest;
import com.example.inimuws.dto.ReservationResponse;
import com.example.inimuws.dto.ReservationStatusUpdateRequest;
import com.example.inimuws.entity.Reservation;
import com.example.inimuws.entity.SystemSetting;
import com.example.inimuws.entity.WorkshopSchedule;
import com.example.inimuws.entity.WorkshopTimeSlot;
import com.example.inimuws.enums.ReservationStatus;
import com.example.inimuws.exception.BusinessException;
import com.example.inimuws.repository.ReservationRepository;
import com.example.inimuws.repository.SystemSettingRepository;
import com.example.inimuws.repository.WorkshopTimeSlotRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final String PRICE_PER_PERSON_KEY = "price_per_person";
    private static final DateTimeFormatter CODE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd");

    private final ReservationRepository reservationRepository;
    private final WorkshopTimeSlotRepository timeSlotRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final InquiryService inquiryService;

    @Transactional
    public ReservationResponse createReservationOrInquiry(ReservationRequest request) {
        validateCommon(request);
        if (request.isInquiryOnly()) {
            inquiryService.createInquiry(request);
            return ReservationResponse.inquiry();
        }

        validateReservation(request);
        WorkshopTimeSlot slot = timeSlotRepository.findByScheduleDateAndStartTimeForUpdate(
                        request.getReservationDate(),
                        request.getReservationTime()
                )
                .orElseThrow(() -> new BusinessException("指定された開催日または時間枠がありません"));

        WorkshopSchedule schedule = slot.getSchedule();
        if (!schedule.isOpen()) {
            throw new BusinessException("指定された開催日は受付できません");
        }
        if (!slot.isActive()) {
            throw new BusinessException("指定された時間枠は受付停止中です");
        }
        int reservationCount = request.getReservationCount();
        if (slot.getReservedCount() + reservationCount > slot.getCapacity()) {
            throw new BusinessException("指定された時間枠は満席です");
        }

        int pricePerPerson = getPricePerPerson();
        int totalAmount = reservationCount * pricePerPerson;
        String reservationCode = generateReservationCode(request.getReservationDate());

        Reservation reservation = Reservation.builder()
                .reservationCode(reservationCode)
                .timeSlot(slot)
                .reservationDate(request.getReservationDate())
                .reservationTime(request.getReservationTime())
                .reservationCount(reservationCount)
                .customerFamilyName(request.getCustomerFamilyName())
                .customerGivenName(request.getCustomerGivenName())
                .customerFamilyKana(request.getCustomerFamilyKana())
                .customerGivenKana(request.getCustomerGivenKana())
                .customerEmail(request.getCustomerEmail())
                .customerTel(request.getCustomerTel())
                .customerMessage(request.getCustomerMessage())
                .totalAmount(totalAmount)
                .status(ReservationStatus.PENDING)
                .build();
        reservationRepository.save(reservation);
        slot.setReservedCount(slot.getReservedCount() + reservationCount);

        return ReservationResponse.reservation(reservationCode);
    }

    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("予約が見つかりません"));
    }

    @Transactional(readOnly = true)
    public List<Reservation> searchReservations(AdminReservationSearchCondition condition) {
        return reservationRepository.findAll(buildSpecification(condition), buildSort(condition));
    }

    @Transactional
    public Reservation updateStatus(Long id, ReservationStatusUpdateRequest request) {
        Reservation reservation = findById(id);
        ReservationStatus before = reservation.getStatus();
        ReservationStatus after = request.getStatus();
        int participantCount = request.getParticipantCount() != null
                ? request.getParticipantCount()
                : reservation.effectiveParticipantCount();
        int maleCount = request.getMaleCount() != null ? request.getMaleCount() : 0;
        int femaleCount = request.getFemaleCount() != null ? request.getFemaleCount() : 0;
        int pricePerPerson = getPricePerPerson();

        if (before != ReservationStatus.CANCELLED && after == ReservationStatus.CANCELLED) {
            WorkshopTimeSlot slot = timeSlotRepository.findByIdForUpdate(reservation.getTimeSlot().getId())
                    .orElseThrow(() -> BusinessException.notFound("時間枠が見つかりません"));
            slot.setReservedCount(Math.max(0, slot.getReservedCount() - reservation.getReservationCount()));
        } else if (before == ReservationStatus.CANCELLED && after != ReservationStatus.CANCELLED) {
            WorkshopTimeSlot slot = timeSlotRepository.findByIdForUpdate(reservation.getTimeSlot().getId())
                    .orElseThrow(() -> BusinessException.notFound("時間枠が見つかりません"));
            if (slot.getReservedCount() + reservation.getReservationCount() > slot.getCapacity()) {
                throw new BusinessException("空席が足りないためキャンセルを戻せません");
            }
            slot.setReservedCount(slot.getReservedCount() + reservation.getReservationCount());
        }

        reservation.setStatus(after);
        reservation.setParticipantCount(participantCount);
        reservation.setMaleCount(maleCount);
        reservation.setFemaleCount(femaleCount);
        reservation.setTotalAmount(participantCount * pricePerPerson);
        reservation.setAdminMemo(request.getAdminMemo());
        return reservation;
    }

    @Transactional(readOnly = true)
    public long countTodayReservations() {
        return reservationRepository.countByReservationDate(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public long countThisMonthReservations() {
        YearMonth month = YearMonth.now();
        return reservationRepository.countByReservationDateBetween(month.atDay(1), month.atEndOfMonth());
    }

    @Transactional(readOnly = true)
    public long countReservationsForMonth(YearMonth month) {
        return reservationRepository.countByReservationDateBetween(month.atDay(1), month.atEndOfMonth());
    }

    @Transactional(readOnly = true)
    public long countPending() {
        return reservationRepository.countByStatus(ReservationStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findRecentReservations() {
        return reservationRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Reservation> findReservationsForMonth(YearMonth month, int limit) {
        return reservationRepository.findByReservationDateBetweenOrderByReservationDateAscReservationTimeAsc(
                        month.atDay(1),
                        month.atEndOfMonth()
                ).stream()
                .limit(limit)
                .toList();
    }

    private Specification<Reservation> buildSpecification(AdminReservationSearchCondition condition) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (condition != null && condition.getDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("reservationDate"), condition.getDate()));
            }
            if (condition != null && condition.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), condition.getStatus()));
            }
            if (condition != null && StringUtils.hasText(condition.getKeyword())) {
                String keyword = "%" + condition.getKeyword().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customerFamilyName")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customerGivenName")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customerEmail")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("reservationCode")), keyword)
                ));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Sort buildSort(AdminReservationSearchCondition condition) {
        if (condition == null || !StringUtils.hasText(condition.getSort())) {
            return Sort.by(
                    Sort.Order.desc("reservationDate"),
                    Sort.Order.asc("reservationTime"),
                    Sort.Order.desc("createdAt")
            );
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(condition.getDirection())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return switch (condition.getSort()) {
            case "reservationCode" -> Sort.by(new Sort.Order(direction, "reservationCode"))
                    .and(Sort.by(Sort.Order.desc("createdAt")));
            case "reservationDate" -> Sort.by(new Sort.Order(direction, "reservationDate"))
                    .and(Sort.by(Sort.Order.asc("reservationTime"), Sort.Order.desc("createdAt")));
            case "reservationTime" -> Sort.by(new Sort.Order(direction, "reservationTime"))
                    .and(Sort.by(Sort.Order.desc("reservationDate"), Sort.Order.desc("createdAt")));
            case "status" -> Sort.by(new Sort.Order(direction, "status"))
                    .and(Sort.by(Sort.Order.desc("reservationDate"), Sort.Order.asc("reservationTime"), Sort.Order.desc("createdAt")));
            default -> Sort.by(
                    Sort.Order.desc("reservationDate"),
                    Sort.Order.asc("reservationTime"),
                    Sort.Order.desc("createdAt")
            );
        };
    }

    private void validateCommon(ReservationRequest request) {
        if (!Boolean.TRUE.equals(request.getPrivacyAccepted())) {
            throw new BusinessException("プライバシーポリシーに同意してください");
        }
        if (!StringUtils.hasText(request.getCustomerFamilyName())
                || !StringUtils.hasText(request.getCustomerGivenName())
                || !StringUtils.hasText(request.getCustomerFamilyKana())
                || !StringUtils.hasText(request.getCustomerGivenKana())
                || !StringUtils.hasText(request.getCustomerEmail())) {
            throw new BusinessException("必須項目を入力してください");
        }
    }

    private void validateReservation(ReservationRequest request) {
        if (request.getReservationDate() == null) {
            throw new BusinessException("予約日を指定してください");
        }
        if (request.getReservationTime() == null) {
            throw new BusinessException("予約時間を指定してください");
        }
        if (request.getReservationCount() == null || request.getReservationCount() < 1 || request.getReservationCount() > 10) {
            throw new BusinessException("予約人数は1〜10名で指定してください");
        }
    }

    private int getPricePerPerson() {
        SystemSetting setting = systemSettingRepository.findBySettingKey(PRICE_PER_PERSON_KEY)
                .orElseThrow(() -> new BusinessException("料金設定が見つかりません"));
        try {
            return Integer.parseInt(setting.getSettingValue());
        } catch (NumberFormatException exception) {
            throw new BusinessException("料金設定が不正です");
        }
    }

    private String generateReservationCode(LocalDate reservationDate) {
        long sequence = reservationRepository.countByReservationDate(reservationDate) + 1;
        String prefix = "WS-" + reservationDate.format(CODE_DATE_FORMAT) + "-";
        String code;
        do {
            code = prefix + String.format("%02d", sequence++);
        } while (reservationRepository.existsByReservationCode(code));
        return code;
    }
}
