package com.nado.smartcare.notice.controller;

import com.nado.smartcare.notice.dto.NoticeDto;
import com.nado.smartcare.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public String getAllNotices(Model model) {
        List<NoticeDto> noticeList = noticeService.findAll();
        noticeList.forEach(notice -> log.info("Created At: {}", notice.getCreatedAt())); // 디버깅용 로그
        model.addAttribute("noticeList", noticeList);
        return "notice/list";
    }


    @GetMapping("register")
    public String register() {
        return "notice/register";
    }

    @PostMapping("register")
    public String register(NoticeDto noticeDto, @RequestParam("images")List<MultipartFile> files) {
        log.info("register notice: {}", noticeDto);

        List<String> imagePaths = files.stream()
                .map(this::saveImage) // 이미지 저장 로직 호출
                .toList();

        NoticeDto finalDto = NoticeDto.builder()
                .title(noticeDto.getTitle())
                .content(noticeDto.getContent())
                .employee(noticeDto.getEmployee())
                .imagePaths(imagePaths)
                .build();

        noticeService.saveNotice(finalDto);
        return "redirect:/notice";
    }

    private String saveImage(MultipartFile file) {
        if (file.isEmpty()) {
            return ""; // 이미지가 없을 경우 빈 문자열 반환
        }

        String uploadDir = "uploads/";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }

        return filePath.toString();
    }

    @GetMapping("/{noticeNo}")
    public String getNoticeDetail(@PathVariable("noticeNo") Long noticeNo, Model model) {
        NoticeDto notice = noticeService.findById(noticeNo);
        model.addAttribute("notice", notice);
        return "notice/detail";
    }
}
