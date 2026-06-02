package com.example.inimuws.service;

import com.example.inimuws.dto.InquiryStatusUpdateRequest;
import com.example.inimuws.dto.ReservationRequest;
import com.example.inimuws.entity.Inquiry;
import com.example.inimuws.enums.InquiryStatus;
import com.example.inimuws.exception.BusinessException;
import com.example.inimuws.repository.InquiryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    public Inquiry createInquiry(ReservationRequest request) {
        if (!StringUtils.hasText(request.getCustomerMessage())) {
            throw new BusinessException("お問い合わせ内容を入力してください");
        }

        Inquiry inquiry = Inquiry.builder()
                .customerFamilyName(request.getCustomerFamilyName())
                .customerGivenName(request.getCustomerGivenName())
                .customerFamilyKana(request.getCustomerFamilyKana())
                .customerGivenKana(request.getCustomerGivenKana())
                .customerEmail(request.getCustomerEmail())
                .customerTel(request.getCustomerTel())
                .customerMessage(request.getCustomerMessage())
                .status(InquiryStatus.OPEN)
                .build();
        return inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public List<Inquiry> findAll() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Inquiry findById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("問い合わせが見つかりません"));
    }

    @Transactional
    public Inquiry updateStatus(Long id, InquiryStatusUpdateRequest request) {
        Inquiry inquiry = findById(id);
        inquiry.setStatus(request.getStatus());
        inquiry.setAdminMemo(request.getAdminMemo());
        return inquiry;
    }

    @Transactional(readOnly = true)
    public long countOpen() {
        return inquiryRepository.countByStatus(InquiryStatus.OPEN);
    }
}
