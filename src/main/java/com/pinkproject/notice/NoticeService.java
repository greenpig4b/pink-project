package com.pinkproject.notice;

import com.pinkproject._core.error.exception.Exception404;
import com.pinkproject.admin.Admin;
import com.pinkproject.admin.AdminRequest._DetailNoticeAdminRecord;
import com.pinkproject.admin.AdminRequest._SaveNoticeAdminRecord;
import com.pinkproject.admin.SessionAdmin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final HttpSession session;


    @Transactional
    public List<_DetailNoticeAdminRecord> getNotices() {
        return noticeRepository.findAllWithAdmin().stream()
                .map(notice -> new _DetailNoticeAdminRecord(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getAdmin().getUsername(),
                        notice.getCreatedAt().toLocalDate()
                ))
                .collect(Collectors.toList());
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
    public void deleteNotice(Integer id) {
        noticeRepository.deleteById(id);
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

}
