package com.omnigroup.omnibank.service;

import com.omnigroup.omnibank.dto.BankResponse;
import com.omnigroup.omnibank.dto.CreditDebitRequest;
import com.omnigroup.omnibank.dto.EnquiryRequest;
import com.omnigroup.omnibank.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);

}
