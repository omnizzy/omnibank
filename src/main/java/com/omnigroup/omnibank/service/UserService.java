package com.omnigroup.omnibank.service;

import com.omnigroup.omnibank.dto.BankResponse;
import com.omnigroup.omnibank.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

}
