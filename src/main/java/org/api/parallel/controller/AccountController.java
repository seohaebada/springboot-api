package org.api.parallel.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api.parallel.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/test")
    public String test() {
        accountService.testLog();
        return "success";
    }
}
