package org.api.account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api.account.domain.Account;
import org.api.account.domain.AccountRepository;
import org.api.account.dto.AccountDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public void testLog() {
        log.info("test");
    }

    public void saveAccount(AccountDto accountDto) {
        Account account = accountDto.toEntity();
        accountRepository.save(account);
    }

    @Transactional
    public void completableFutureTest(AccountDto accountDto) {
        // service1
        Account account = accountDto.toEntity();
        accountRepository.save(account);

        // List 생성
        List<AccountDto> list = new ArrayList<>();

        AccountDto accountDto1 = new AccountDto();
        accountDto1.setEmail("seohae1@gmail.com");
        accountDto1.setNickname("seohae1");
        accountDto1.setPassword("1234");

        list.add(accountDto1);

        AccountDto accountDto2 = new AccountDto();
        accountDto2.setEmail("seohae2@gmail.com");
        accountDto2.setNickname("seohae1"); // 중복 오류 발생
        accountDto2.setPassword("1234");

        list.add(accountDto2);

        // service2(accountDto1), service3(accountDto2)
        list.forEach(target -> CompletableFuture.runAsync(() -> {
            log.info("runAsync()...");
            // update 로직 수행
            accountRepository.save(target.toEntity());
        }).exceptionally(throwable -> {
            log.error("error : " + throwable.getMessage());
            return null;
        }));
    }

    @Transactional
    public void completableFutureForEachTest(AccountDto accountDto) {
        // service1
        Account account = accountDto.toEntity();
        accountRepository.save(account);

        // List 생성
        List<AccountDto> list = new ArrayList<>();

        AccountDto accountDto1 = new AccountDto();
        accountDto1.setEmail("seohae5@gmail.com");
        accountDto1.setNickname("seohae1");
        accountDto1.setPassword("1234");

        list.add(accountDto1);

        AccountDto accountDto2 = new AccountDto();
        accountDto2.setEmail("seohae6@gmail.com");
        accountDto2.setNickname("seohae1"); // 중복 오류 발생
        accountDto2.setPassword("1234");

        list.add(accountDto2);

        // service2(accountDto1), service3(accountDto2)
        CompletableFuture.runAsync(() -> {
            execute(list);
        }).exceptionally(throwable -> {
            log.error("error : " + throwable.getMessage());
            return null;
        });
    }

    @Transactional
    public void execute(List<AccountDto> list) {
        list.forEach(target -> accountRepository.save(target.toEntity()));
    }
}
