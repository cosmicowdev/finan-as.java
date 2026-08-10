package com.cosmico.finance.cli;

import com.cosmico.finance.application.FinanceService;
import com.cosmico.finance.domain.model.TransactionType;
import com.cosmico.finance.infrastructure.InMemoryTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        FinanceService finance = new FinanceService(new InMemoryTransactionRepository());
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Finance Manager ===");
            System.out.println("1 - Nova receita");
            System.out.println("2 - Nova despesa");
            System.out.println("3 - Listar lançamentos");
            System.out.println("4 - Ver saldo");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");

            String option = scanner.nextLine().trim();
            if (option.equals("0")) break;

            try {
                switch (option) {
                    case "1", "2" -> registerTransaction(scanner, finance, option.equals("1") ? TransactionType.INCOME : TransactionType.EXPENSE);
                    case "3" -> finance.listTransactions().forEach(t ->
                            System.out.printf("%d | %s | R$ %s | %s | %s%n", t.getId(), t.getDescription(), t.getAmount(), t.getType(), t.getDate()));
                    case "4" -> System.out.printf("Saldo: R$ %s%n", finance.calculateBalance());
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Não foi possível registrar: " + e.getMessage());
            }
        }
    }

    private static void registerTransaction(Scanner scanner, FinanceService finance, TransactionType type) {
        System.out.print("Descrição: ");
        String description = scanner.nextLine();
        System.out.print("Valor: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().replace(',', '.'));
        finance.register(description, amount, type, LocalDate.now());
        System.out.println("Lançamento registrado.");
    }
}
