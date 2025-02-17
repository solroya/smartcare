package com.nado.smartcare.controller.main;

import com.nado.smartcare.config.CustomUserDetails;
import com.nado.smartcare.notice.dto.NoticeDto;
import com.nado.smartcare.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final NoticeService noticeService;

    @GetMapping("/main")
    public String mainForm(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails) {
            try {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                String username = userDetails.getUsername();
                String memberName = userDetails.getMemberName();

                model.addAttribute("memberName", memberName); // memberName도 모델에 추가
                model.addAttribute("loginStatus", "authenticated");
            } catch (Exception e) {
                model.addAttribute("loginStatus", "error");
                model.addAttribute("errorMessage", "사용자 정보를 불러올 수 없습니다.");
            }
        } else {
            model.addAttribute("loginStatus", "anonymous");
        }
        // 최신 공지사항 6개 가져오기
        List<NoticeDto> noticeList = noticeService.findLatestNotices(6);
        if (noticeList == null || noticeList.isEmpty()) {
            model.addAttribute("noticeList", List.of()); // 빈 리스트 전달
        } else {
            model.addAttribute("noticeList", noticeList);
        }
        return "main";
    }

}
