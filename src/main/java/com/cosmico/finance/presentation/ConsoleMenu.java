package com.cosmico.finance.presentation;

import com.cosmico.finance.application.AccountService;
import com.cosmico.finance.application.ReportService;
import com.cosmico.finance.application.TransactionService;
import com.cosmico.finance.domain.enums.TransactionType;
import com.cosmico.finance.domain.model.Account;
import com.cosmico.finance.domain.model.Category;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;

public final class ConsoleMenu {
    private final Scanner scanner;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ReportService reportService;

    public ConsoleMenu(Scanner scanner, AccountService accountService,
                       TransactionService transactionService, ReportService reportService) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.reportService = reportService;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> createAccount();
                case "2" -> listAccounts();
                case "3" -> registerTransaction(TransactionType.INCOME);
                case "4" -> registerTransaction(TransactionType.EXPENSE);
                case "5" -> showReport();
                case "0" -> running = false;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Controle Financeiro ===");
        System.out.println("1 - Nova conta");
        System.out.println("2 - Listar contas");
        System.out.println("3 - Registrar receita");
        System.out.println("4 - Registrar despesa");
        System.out.println("5 - Ver resumo");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");
    }

    private void createAccount() {
        System.out.print("Nome da conta: ");
        Account account = accountService.create(scanner.nextLine());
        System.out.println("Conta criada: " + account.getName());
    }

    private void listAccounts() {
        if (accountService.list().isEmpty()) {
            System.out.println("Nenhuma conta cadastrada.");
            return;
        }
        accountService.list().forEach(account ->
                System.out.printf("%s | %s | R$ %s%n", account.getId(), account.getName(), account.getBalance()));
    }

    private void registerTransaction(TransactionType type) {
        try {
            UUID accountId = UUID.fromString(read("ID da conta: "));
            BigDecimal amount = new BigDecimal(read("Valor: "));
            String category = read("Categoria: ");
            String description = read("Descrição: ");
            transactionService.register(accountId, amount, type, new Category(category), description);
            System.out.println("Transação registrada.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            System.out.println("Não foi possível registrar: " + exception.getMessage());
        }
    }

    private void showReport() {
        try {
            UUID accountId = UUID.fromString(read("ID da conta: "));
            System.out.println("Receitas: R$ " + reportService.totalIncome(accountId));
            System.out.println("Despesas: R$ " + reportService.totalExpenses(accountId));
            System.out.println("Saldo: R$ " + reportService.balance(accountId));
        } catch (IllegalArgumentException exception) {
            System.out.println("ID inválido.");
        }
    }

    private String read(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
