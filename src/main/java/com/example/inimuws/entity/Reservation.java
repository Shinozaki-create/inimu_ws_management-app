package com.example.inimuws.entity;

import com.example.inimuws.enums.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_code", nullable = false, unique = true)
    private String reservationCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "time_slot_id", nullable = false)
    private WorkshopTimeSlot timeSlot;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;

    @Column(name = "reservation_count", nullable = false)
    private int reservationCount;

    @Column(name = "customer_family_name", nullable = false)
    private String customerFamilyName;

    @Column(name = "customer_given_name", nullable = false)
    private String customerGivenName;

    @Column(name = "customer_family_kana", nullable = false)
    private String customerFamilyKana;

    @Column(name = "customer_given_kana", nullable = false)
    private String customerGivenKana;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_tel")
    private String customerTel;

    @Column(name = "customer_message")
    private String customerMessage;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "admin_memo")
    private String adminMemo;

    public String customerName() {
        return customerFamilyName + " " + customerGivenName;
    }
}
