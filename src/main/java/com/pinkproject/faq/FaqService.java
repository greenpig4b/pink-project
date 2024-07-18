package com.pinkproject.faq;

import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import com.pinkproject.admin.AdminRequest._DetailFaqAdminRecord;
import com.pinkproject.admin.AdminRequest._SaveFaqAdminRecord;
import com.pinkproject.admin.SessionAdmin;
import com.pinkproject.admin.enums.FaqEnum;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FaqService {
    private final FaqRepository faqRepository;
    private final HttpSession session;
    private final AdminRepository adminRepository;

    @Transactional
    public Page<_DetailFaqAdminRecord> getFaqs(int page) {
        PageRequest pageRequest = PageRequest.of(page, 5); // 페이지 번호, 페이지 당 크기
        Page<Faq> faqs = faqRepository.findAllWithAdmin(pageRequest);
        return faqs.map(faq -> new _DetailFaqAdminRecord(
                faq.getId(),
                faq.getTitle(),
                faq.getContent(),
                faq.getAdmin().getUsername(),
                faq.getClassification(),
                faq.getCreatedAt().toLocalDate()
        ));
    }

    @Transactional
    public List<_DetailFaqAdminRecord> getAllFaqs() {
        return faqRepository.findAll()
                .stream()
                .map(faq -> new _DetailFaqAdminRecord(
                        faq.getId(),
                        faq.getTitle(),
                        faq.getContent(),
                        faq.getAdmin().getUsername(),
                        faq.getClassification(), // 추가된 필드
                        faq.getCreatedAt().toLocalDate()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<_DetailFaqAdminRecord> searchFaqs(String keyword, int page) {
        PageRequest pageRequest = PageRequest.of(page, 5); // 페이지 번호, 페이지 당 크기
        Page<Faq> faqs = faqRepository.findByKeywordWithFaq(keyword, pageRequest);
        return faqs.map(faq -> new _DetailFaqAdminRecord(
                faq.getId(),
                faq.getTitle(),
                faq.getContent(),
                faq.getAdmin().getUsername(),
                faq.getClassification(),
                faq.getCreatedAt().toLocalDate()
        ));
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
    public Integer saveFaq(_SaveFaqAdminRecord saveFaqAdminRecord, Admin admin) {
        Faq faq = Faq.builder()
                .admin(admin)
                .title(saveFaqAdminRecord.title())
                .content(saveFaqAdminRecord.content())
                .classification(FaqEnum.fromValue(saveFaqAdminRecord.classification()))
                .build();
        faq = faqRepository.save(faq);
        return faq.getId();
    }



    @Transactional
    public _DetailFaqAdminRecord getFaqById(Integer id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return faqRepository.findById(id)
                .map(faq -> new _DetailFaqAdminRecord(
                        faq.getId(),
                        faq.getTitle(),
                        faq.getContent(),
                        faq.getAdmin().getUsername(),
                        faq.getClassification(), // 추가된 필드
                        faq.getCreatedAt().toLocalDate() // LocalDateTime에서 LocalDate로 변환
                ))
                .orElseThrow(() -> new RuntimeException("FAQ not found with id: " + id));
    }


    @Transactional
    public void deleteFaq(Integer faqId) {
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin == null) {
            throw new RuntimeException("Admin session not found");
        }

        Admin admin = adminRepository.findById(sessionAdmin.getId())
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + sessionAdmin.getId()));

        Faq faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new RuntimeException("FAQ not found with id: " + faqId));

        if (!faq.getAdmin().equals(admin)) {
            throw new RuntimeException("Admin not authorized to delete this FAQ");
        }

        faqRepository.delete(faq);
    }
}
