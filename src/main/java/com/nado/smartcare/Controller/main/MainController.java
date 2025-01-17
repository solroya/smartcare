package com.nado.smartcare.controller.main;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nado.smartcare.notice.dto.NoticeDto;
import com.nado.smartcare.notice.service.NoticeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final NoticeService noticeService;

	@GetMapping("main")
	public String mainForm(Model model) {
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
