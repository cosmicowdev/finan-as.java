package com.cosmico.finance;

import com.cosmico.finance.application.AccountService;
import com.cosmico.finance.application.ReportService;
import com.cosmico.finance.application.TransactionService;
import com.cosmico.finance.infrastructure.repository.InMemoryAccountRepository;
import com.cosmico.finance.infrastructure.repository.InMemoryTransactionRepository;
import com.cosmico.finance.presentation.ConsoleMenu;

import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        var accountRepository = new InMemoryAccountRepository();
        var transactionRepository = new InMemoryTransactionRepository();
        var accountService = new AccountService(accountRepository);
        var transactionService = new TransactionService(accountRepository, transactionRepository);
        var reportService = new ReportService(transactionService);

        try (Scanner scanner = new Scanner(System.in)) {
            new ConsoleMenu(scanner, accountService, transactionService, reportService).start();
        }
    }
}
