package com.example.inimuws.entity;

import com.example.inimuws.enums.InquiryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "inquiries")
public class Inquiry extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "customer_message", nullable = false)
    private String customerMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status;

    @Column(name = "admin_memo")
    private String adminMemo;

    public String customerName() {
        return customerFamilyName + " " + customerGivenName;
    }
}
