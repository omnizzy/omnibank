package com.omnigroup.omnibank.service;

import com.omnigroup.omnibank.dto.TransactionDto;
import com.omnigroup.omnibank.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
