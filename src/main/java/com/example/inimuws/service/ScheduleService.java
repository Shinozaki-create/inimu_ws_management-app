package com.example.inimuws.service;

import com.example.inimuws.dto.ScheduleCreateRequest;
import com.example.inimuws.dto.ScheduleResponse;
import com.example.inimuws.dto.ScheduleUpdateRequest;
import com.example.inimuws.dto.TimeSlotCreateRequest;
import com.example.inimuws.dto.TimeSlotResponse;
import com.example.inimuws.dto.TimeSlotUpdateRequest;
import com.example.inimuws.entity.WorkshopSchedule;
import com.example.inimuws.entity.WorkshopTimeSlot;
import com.example.inimuws.exception.BusinessException;
import com.example.inimuws.repository.WorkshopScheduleRepository;
import com.example.inimuws.repository.WorkshopTimeSlotRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final WorkshopScheduleRepository scheduleRepository;
    private final WorkshopTimeSlotRepository timeSlotRepository;

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getPublicSchedules() {
        return scheduleRepository.findAllByOrderByScheduleDateAsc().stream()
                .map(this::toScheduleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getPublicTimeSlots(java.time.LocalDate date) {
        return timeSlotRepository.findBySchedule_ScheduleDateOrderByStartTimeAsc(date).stream()
                .map(this::toTimeSlotResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkshopSchedule> findAllSchedules() {
        return scheduleRepository.findAllByOrderByScheduleDateAsc();
    }

    @Transactional(readOnly = true)
    public List<WorkshopTimeSlot> findSlots(Long scheduleId) {
        return timeSlotRepository.findByScheduleIdOrderByStartTimeAsc(scheduleId);
    }

    @Transactional(readOnly = true)
    public List<WorkshopTimeSlot> findLowAvailabilitySlots(int threshold) {
        return timeSlotRepository.findLowAvailabilitySlots(threshold);
    }

    @Transactional
    public WorkshopSchedule createSchedule(ScheduleCreateRequest request) {
        scheduleRepository.findByScheduleDate(request.getScheduleDate()).ifPresent(existing -> {
            throw new BusinessException("指定日の開催日は既に登録されています");
        });
        WorkshopSchedule schedule = WorkshopSchedule.builder()
                .scheduleDate(request.getScheduleDate())
                .open(request.isOpen())
                .note(request.getNote())
                .build();
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public WorkshopSchedule updateSchedule(Long id, ScheduleUpdateRequest request) {
        WorkshopSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("開催日が見つかりません"));
        schedule.setOpen(Boolean.TRUE.equals(request.getOpen()));
        schedule.setNote(request.getNote());
        return schedule;
    }

    @Transactional
    public WorkshopTimeSlot createTimeSlot(TimeSlotCreateRequest request) {
        WorkshopSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> BusinessException.notFound("開催日が見つかりません"));
        validateTimeRange(request.getStartTime(), request.getEndTime());
        WorkshopTimeSlot slot = WorkshopTimeSlot.builder()
                .schedule(schedule)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .capacity(request.getCapacity())
                .reservedCount(0)
                .active(request.isActive())
                .build();
        return timeSlotRepository.save(slot);
    }

    @Transactional
    public WorkshopTimeSlot updateTimeSlot(Long id, TimeSlotUpdateRequest request) {
        WorkshopTimeSlot slot = timeSlotRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("時間枠が見つかりません"));
        if (request.getCapacity() < slot.getReservedCount()) {
            throw new BusinessException("予約済み人数より少ない定員には変更できません");
        }
        slot.setCapacity(request.getCapacity());
        slot.setActive(request.isActive());
        return slot;
    }

    public ScheduleResponse toScheduleResponse(WorkshopSchedule schedule) {
        List<WorkshopTimeSlot> activeSlots = schedule.getTimeSlots().stream()
                .filter(WorkshopTimeSlot::isActive)
                .toList();
        int totalCapacity = activeSlots.stream().mapToInt(WorkshopTimeSlot::getCapacity).sum();
        int reservedCount = activeSlots.stream().mapToInt(WorkshopTimeSlot::getReservedCount).sum();
        int remainingCount = Math.max(0, totalCapacity - reservedCount);
        boolean fullyBooked = schedule.isOpen() && totalCapacity > 0 && remainingCount == 0;
        return new ScheduleResponse(
                schedule.getScheduleDate(),
                schedule.isOpen(),
                totalCapacity,
                reservedCount,
                remainingCount,
                fullyBooked
        );
    }

    public TimeSlotResponse toTimeSlotResponse(WorkshopTimeSlot slot) {
        return new TimeSlotResponse(
                slot.getId(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getCapacity(),
                slot.getReservedCount(),
                slot.remainingCount(),
                slot.isActive(),
                slot.isFullyBooked()
        );
    }

    private void validateTimeRange(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new BusinessException("終了時刻は開始時刻より後にしてください");
        }
    }
}
