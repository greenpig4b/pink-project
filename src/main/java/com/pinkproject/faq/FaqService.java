package com.pinkproject.faq;

import com.pinkproject.admin.AdminRequest._DetailFaqAdminRecord;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FaqService {
    private final FaqRepository faqRepository;

    @Transactional
    public List<_DetailFaqAdminRecord> getFaqs() {
        return faqRepository.findAllWithAdmin().stream()
                .map(faq -> new _DetailFaqAdminRecord(
                        faq.getId(),
                        faq.getTitle(),
                        faq.getContent(),
                        faq.getAdmin().getUsername(), // admin username ���기화
                        faq.getClassification(),
                        faq.getCreatedAt().toLocalDate()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public _DetailFaqAdminRecord detailFaqAdminRecord(Integer id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("자주 찾는 질문이 존재하지 않습니다."));
        // admin 엔티티를 초기화
        String adminUsername = faq.getAdmin().getUsername(); // 세션 내에서 초기화
        return new _DetailFaqAdminRecord(
                faq.getId(),
                faq.getTitle(),
                faq.getContent(),
                faq.getAdmin().getUsername(),
                faq.getClassification(),
                faq.getCreatedAt().toLocalDate()
        );
    }

    @Transactional
    public void deleteFaq(Integer id) {
        faqRepository.deleteById(id);
    }
}
