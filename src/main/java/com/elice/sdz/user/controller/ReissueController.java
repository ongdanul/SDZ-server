package com.elice.sdz.user.controller;

import com.elice.sdz.user.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/reissue")
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        reissueService.reissue(request, response);

        return ResponseEntity.ok().build();
    }
}
