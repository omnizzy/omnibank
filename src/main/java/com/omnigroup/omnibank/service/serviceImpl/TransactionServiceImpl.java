package com.omnigroup.omnibank.service.serviceImpl;

import com.omnigroup.omnibank.dto.TransactionDto;
import com.omnigroup.omnibank.entity.Transaction;
import com.omnigroup.omnibank.repository.TransactionRepository;
import com.omnigroup.omnibank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transaction);
        System.out.println("Transaction saved successfully");
    }
}
