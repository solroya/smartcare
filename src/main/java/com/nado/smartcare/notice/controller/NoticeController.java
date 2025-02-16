package com.nado.smartcare.notice.controller;

import com.nado.smartcare.notice.dto.NoticeDto;
import com.nado.smartcare.notice.service.NoticeService;
import com.nado.smartcare.page.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public String getAllNotices(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                Model model) {

        PageResponse<NoticeDto> noticeList = noticeService.getAllNotices(pageable);

        model.addAttribute("noticeList", noticeList.getContent());
        model.addAttribute("currentPage", noticeList.getPageNumber());
        model.addAttribute("totalPages", noticeList.getTotalPages());
        model.addAttribute("totalElements", noticeList.getTotalElements());

        return "notice/list";
    }

    @GetMapping("/mainlist")
    public String getAllMainNotices(@RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "size", defaultValue = "10") int size,
                                @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection,
                                @RequestParam(name = "searchTerm", required = false) String searchTerm,
                                Model model) {

        // 정렬 방향 처리
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        // 검색 조건에 따른 공지사항 조회
        List<NoticeDto> noticeList;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            noticeList = noticeService.findByTitleContaining(searchTerm, pageRequest);
        } else {
            noticeList = noticeService.findAll(pageRequest);
        }

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalElements", noticeList.size());
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("sortDirection", sortDirection);

        return "notice/mainlist";
    }

    @GetMapping("/register")
    public String register() {
        return "notice/register";
    }

    @PostMapping("/register")
    public String register(NoticeDto noticeDto, @RequestParam("images") List<MultipartFile> files) {
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
