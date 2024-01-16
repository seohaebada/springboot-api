package org.api.account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api.account.domain.Account;
import org.api.account.domain.AccountRepository;
import org.api.account.dto.AccountDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    public void completableFutureTest() {
        List<AccountDto> list = makeAccountTargetList();

        // accountDto1, accountDto2
        list.forEach(target -> CompletableFuture.runAsync(() -> {
            log.info("runAsync()...");
            log.info("completableFutureTest() Thread Name : " + Thread.currentThread().getName());
            // update 로직 수행
            accountRepository.save(target.toEntity());
        }).exceptionally(throwable -> {
            log.error("error : " + throwable.getMessage());
            return null;
        }));
    }

    @Transactional
    public void completableFutureForEachTest() {
        // 마지막 데이터 조회
        List<AccountDto> list = makeAccountTargetList();

        // service2(accountDto1), service3(accountDto2)
        CompletableFuture.runAsync(() -> {
            log.info("completableFutureForEachTest() Thread Name : " + Thread.currentThread().getName());
            execute(list);
        }).exceptionally(throwable -> {
            log.error("error : " + throwable.getMessage());
            return null;
        });
    }

    private List<AccountDto> makeAccountTargetList() {
        // 마지막 데이터 조회
        List<Account> all = accountRepository.findAll();
        Optional<Account> first = all.stream()
                .sorted(Comparator.comparing(Account::getId, Comparator.reverseOrder()))
                .findFirst();

        Long lastId = 0L;
        if (first.isPresent()) {
            lastId = first.get().getId();
        }

        // service1
        lastId = lastId + 1;

        AccountDto accountDto = new AccountDto();
        accountDto.setEmail("seohae" + lastId + "@gmail.com");
        accountDto.setNickname("seohae" + lastId);
        accountDto.setPassword("1234");

        // accountDto
        accountRepository.save(accountDto.toEntity());

        // List 생성
        List<AccountDto> list = new ArrayList<>();

        lastId = lastId + 1;

        AccountDto accountDto1 = new AccountDto();
        accountDto1.setEmail("seohae" + lastId + "@gmail.com");
        accountDto1.setNickname("seohae" + lastId);
        accountDto1.setPassword("1234");

        list.add(accountDto1);

//        lastId = lastId + 1; // 고의 오류 발생

        AccountDto accountDto2 = new AccountDto();
        accountDto2.setEmail("seohae" + lastId + "@gmail.com");
        accountDto2.setNickname("seohae" + lastId); // 중복 오류 발생
        accountDto2.setPassword("1234");

        list.add(accountDto2);
        return list;
    }

    public void execute(List<AccountDto> list) {
        log.info("execute() Thread Name : " + Thread.currentThread().getName());
        list.forEach(target -> {
            accountRepository.save(target.toEntity());
        });
    }
}
