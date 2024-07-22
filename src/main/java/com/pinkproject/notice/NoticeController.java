package com.pinkproject.notice;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.admin.Admin;
import com.pinkproject.notice.noticeRequest._DetailNoticeAdminRecord;
import com.pinkproject.notice.noticeRequest._SaveNoticeAdminRecord;
import com.pinkproject.admin.AdminService;
import com.pinkproject.admin.SessionAdmin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Controller
public class NoticeController {

    private final NoticeService noticeService;
    private final AdminService adminService;
    private final HttpSession session;

    @GetMapping("/admin/notice")
    public String notice(@RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(value = "page", defaultValue = "1") int page,
                         HttpServletRequest request) {
        int pageIndex = page - 1; // 페이지 인덱스를 0부터 시작하도록 변환
        Page<_DetailNoticeAdminRecord> notices = keyword != null && !keyword.isEmpty() ?
                noticeService.searchNotices(keyword, pageIndex) :
                noticeService.getNotices(pageIndex);
        request.setAttribute("notices", notices.getContent());

        // 페이지네이션 정보 추가
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", notices.getTotalPages());
        request.setAttribute("hasPrevious", notices.hasPrevious());
        request.setAttribute("hasNext", notices.hasNext());
        request.setAttribute("previousPage", page > 1 ? page - 1 : 1); // 1 페이지 이하로 내려가지 않도록
        request.setAttribute("nextPage", page + 1);
        request.setAttribute("lastPage", notices.getTotalPages());
        request.setAttribute("pages", IntStream.range(1, notices.getTotalPages() + 1)
                .mapToObj(i -> Map.of("number", i, "isCurrent", i == page))
                .collect(Collectors.toList()));
        request.setAttribute("keyword", keyword);

        // 세션에서 admin 객체 가져와서 username 설정
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            request.setAttribute("username", sessionAdmin.getUsername());
        } else {
            session.invalidate();
            return "redirect:/admin";
        }

        // currentDateTime 설정
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedNow = now.format(formatter);
        request.setAttribute("currentDateTime", formattedNow);

        return "admin/notice";
    }

    @GetMapping("/admin/notice/detail/{id}")
    public String noticeDetail(@PathVariable Integer id, HttpServletRequest request) {
        _DetailNoticeAdminRecord noticeDetail = noticeService.detailNoticeAdminRecord(id);
        HttpSession session = request.getSession(); // 세션 객체를 얻음
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            request.setAttribute("username", sessionAdmin.getUsername());
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNow = now.format(formatter);
            request.setAttribute("currentDateTime", formattedNow);
        } else {
            session.invalidate();
            return "redirect:/admin";
        }
        request.setAttribute("notice", noticeDetail);
        request.setAttribute("title", noticeDetail.title());
        request.setAttribute("content", noticeDetail.content());
        request.setAttribute("date", noticeDetail.date());
        return "admin/notice-detail";
    }



    @DeleteMapping("/admin/notice/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteNotice(@PathVariable Integer id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok(new ApiUtil<>(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiUtil<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }



    @GetMapping("/admin/notice/save")
    public String saveNoticeForm(HttpServletRequest request) {
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            request.setAttribute("username", sessionAdmin.getUsername());
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNow = now.format(formatter);
            request.setAttribute("currentDateTime", formattedNow);
        }
        return "admin/notice-save";
    }

    @PostMapping("/admin/notice/save")
    public String saveNotice(@ModelAttribute _SaveNoticeAdminRecord saveNoticeAdminRecord, HttpServletRequest request) {
        try {
            SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
            Admin admin = adminService.findByUsername(sessionAdmin.getUsername());
            noticeService.saveNotice(saveNoticeAdminRecord, admin);
            return "redirect:/admin/notice";
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            return "admin/notice-save";
        }
    }
}
