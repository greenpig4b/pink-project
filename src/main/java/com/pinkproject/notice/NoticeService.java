package com.pinkproject.notice;

import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRepository;
import com.pinkproject.admin.AdminRequest._DetailNoticeAdminRecord;
import com.pinkproject.admin.AdminRequest._SaveFaqAdminRecord;
import com.pinkproject.admin.AdminRequest._SaveNoticeAdminRecord;
import com.pinkproject.admin.SessionAdmin;
import com.pinkproject.admin.enums.FaqEnum;
import com.pinkproject.faq.Faq;
import com.pinkproject.faq.FaqRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final HttpSession session;
    private final AdminRepository adminRepository;
    private final FaqRepository faqRepository;


    @Transactional
    public Page<_DetailNoticeAdminRecord> searchNotices(String keyword, int page) {
        PageRequest pageRequest = PageRequest.of(page, 5); // 페이지 번호, 페이지 당 크기
        Page<Notice> notices = noticeRepository.findByKeywordWithNotice(keyword, pageRequest);
        return notices.map(notice -> new _DetailNoticeAdminRecord(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getAdmin().getUsername(),
                notice.getCreatedAt().toLocalDate()
        ));
    }

    @Transactional
    public Page<_DetailNoticeAdminRecord> getNotices(int page) {
        PageRequest pageRequest = PageRequest.of(page, 5); // 페이지 번호, 페이지 당 크기
        Page<Notice> notices = noticeRepository.findAllWithAdmin(pageRequest);
        return notices.map(notice -> new _DetailNoticeAdminRecord(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getAdmin().getUsername(),
                notice.getCreatedAt().toLocalDate()
        ));
    }



    @Transactional
    public List<_DetailNoticeAdminRecord> getAllNotices() {
        List<Notice> notices = noticeRepository.findAll();
        System.out.println("Notice 리스트: " + notices); // 디버그 로그 추가

        notices.forEach(notice -> Hibernate.initialize(notice.getAdmin()));

        List<_DetailNoticeAdminRecord> result = notices.stream()
                .map(notice -> new _DetailNoticeAdminRecord(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getAdmin().getUsername(),
                        notice.getCreatedAt().toLocalDate()
                ))
                .collect(Collectors.toList());

        System.out.println("변환된 결과 리스트: " + result); // 디버그 로그 추가
        return result;
    }

    @Transactional
    public _DetailNoticeAdminRecord detailNoticeAdminRecord(Integer id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 없습니다."));
        // admin 엔티티를 초기화
        String username = notice.getAdmin().getUsername(); // 세션 내에서 초기화
        return new _DetailNoticeAdminRecord(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                username,
                notice.getCreatedAt().toLocalDate()
        );
    }

    @Transactional
    public Integer saveNotice(_SaveNoticeAdminRecord saveNoticeAdminRecord, Admin admin) {
        Notice notice = Notice.builder()
                .admin(admin)
                .title(saveNoticeAdminRecord.title())
                .content(saveNoticeAdminRecord.content())
                .build();
        notice = noticeRepository.save(notice);
        return notice.getId();
    }




    @Transactional
    public _DetailNoticeAdminRecord getNoticeById(Integer id) {
        return noticeRepository.findById(id)
                .map(notice -> new _DetailNoticeAdminRecord(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getAdmin().getUsername(),
                        notice.getCreatedAt().toLocalDate()
                ))
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + id));
    }


    @Transactional
    public void deleteNotice(Integer noticeId) {
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin == null) {
            throw new RuntimeException("Admin session not found");
        }

        Admin admin = adminRepository.findById(sessionAdmin.getId())
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + sessionAdmin.getId()));

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + noticeId));

        if (!notice.getAdmin().equals(admin)) {
            throw new RuntimeException("Admin not authorized to delete this notice");
        }

        noticeRepository.delete(notice);
    }

}
