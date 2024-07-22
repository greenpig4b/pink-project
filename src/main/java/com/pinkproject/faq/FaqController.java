package com.pinkproject.faq;

import com.pinkproject._core.utils.ApiUtil;
import com.pinkproject.admin.Admin;
import com.pinkproject.faq.faqRequest._DetailFaqAdminRecord;
import com.pinkproject.faq.faqRequest._SaveFaqAdminRecord;
import com.pinkproject.admin.AdminService;
import com.pinkproject.admin.SessionAdmin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Controller
public class FaqController {

    private final Logger logger = LoggerFactory.getLogger(FaqController.class);
    private final FaqService faqService;
    private final AdminService adminService;
    private final HttpSession session;

    @GetMapping("/admin/faq")
    public String faq(@RequestParam(value = "keyword", required = false) String keyword,
                      @RequestParam(value = "page", defaultValue = "1") int page,
                      Model model) {
        int pageIndex = page - 1;
        Page<_DetailFaqAdminRecord> faqs = keyword != null && !keyword.isEmpty() ?
                faqService.searchFaqs(keyword, pageIndex) :
                faqService.getFaqs(pageIndex);
        model.addAttribute("faqs", faqs.getContent());
        model.addAttribute("faqCount", faqs.getTotalElements());
        model.addAttribute("keyword", keyword);

        // 페이지네이션 정보 추가
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", faqs.getTotalPages());
        model.addAttribute("hasPrevious", faqs.hasPrevious());
        model.addAttribute("hasNext", faqs.hasNext());
        model.addAttribute("previousPage", page > 1 ? page - 1 : 1);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("lastPage", faqs.getTotalPages());
        model.addAttribute("pages", IntStream.range(1, faqs.getTotalPages() + 1)
                .mapToObj(i -> Map.of("number", i, "isCurrent", i == page))
                .collect(Collectors.toList()));
        model.addAttribute("keyword", keyword);

        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            logger.info("SessionAdmin found: {}", sessionAdmin.getUsername());
            model.addAttribute("username", sessionAdmin.getUsername());
        } else {
            logger.warn("SessionAdmin not found in session");
            session.invalidate();
            return "redirect:/admin";
        }

        // currentDateTime 설정
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedNow = now.format(formatter);
        model.addAttribute("currentDateTime", formattedNow);

        // Log all model attributes
        model.asMap().forEach((key, value) -> logger.info("Model attribute: {} = {}", key, value));

        return "admin/faq";
    }


    @GetMapping("/admin/faq/detail/{id}")
    public String faqDetail(@PathVariable Integer id, Model model) {
        _DetailFaqAdminRecord faqDetail = faqService.detailFaqAdminRecord(id);
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            model.addAttribute("username", sessionAdmin.getUsername());
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNow = now.format(formatter);
            model.addAttribute("currentDateTime", formattedNow);
        } else {
            session.invalidate();
            return "redirect:/admin";
        }
        model.addAttribute("faq", faqDetail);
        model.addAttribute("title", faqDetail.title());
        model.addAttribute("content", faqDetail.content());
        model.addAttribute("date", faqDetail.date());
        model.addAttribute("classification", faqDetail.classification());

        return "admin/faq-detail";
    }

    @DeleteMapping("/admin/faq/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteFaq(@PathVariable Integer id) {
        try {
            faqService.deleteFaq(id);
            return ResponseEntity.ok(new ApiUtil<>(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiUtil<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/admin/faq/save")
    public String saveFaqForm(Model model) {
        SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
        if (sessionAdmin != null) {
            model.addAttribute("username", sessionAdmin.getUsername());
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNow = now.format(formatter);
            model.addAttribute("currentDateTime", formattedNow);
        }
        else {
            session.invalidate();
            return "redirect:/admin";
        }
        return "admin/faq-save";
    }

    @PostMapping("/admin/faq/save")
    public String saveFaq(@ModelAttribute _SaveFaqAdminRecord saveFaqAdminRecord, Model model) {
        try {
            SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("admin");
            if (sessionAdmin == null) {
                throw new IllegalArgumentException("No admin session found");
            }

            logger.info("SessionAdmin: {}", sessionAdmin);
            Admin admin = adminService.findByUsername(sessionAdmin.getUsername());
            if (admin == null) {
                throw new IllegalArgumentException("No admin found with username: " + sessionAdmin.getUsername());
            }

            logger.info("Admin: {}", admin);
            Integer faqId = faqService.saveFaq(saveFaqAdminRecord, admin);
            logger.info("Faq saved with id: {}", faqId);

            return "redirect:/admin/faq";
        } catch (Exception e) {
            logger.error("Error saving FAQ", e);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/faq-save";
        }
    }

}
