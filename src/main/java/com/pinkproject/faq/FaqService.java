package com.pinkproject.faq;

import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRequest._DetailFaqAdminRecord;
import com.pinkproject.admin.AdminRequest._SaveFaqAdminRecord;
import com.pinkproject.admin.enums.FaqEnum;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FaqService {
    private final FaqRepository faqRepository;

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
    public void deleteFaq(Integer id) {
        faqRepository.deleteById(id);
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

}
