package com.omnigroup.omnibank.repository;

import com.omnigroup.omnibank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
