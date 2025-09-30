package com.omnigroup.omnibank.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXITS_CODE = "001";
    public static final String ACCOUNT_EXITS_MESSAGE = "This user already has an account created";
    public static final String ACCOUNT_CREATION_CODE = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created";
    public static final String ACCOUNT_NOT_EXITS_CODE = "003";
    public static final String ACCOUNT_NOT_EXITS_MESSAGE = "User with the provided Account Number does not exists";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "User account found";
    public static final String ACCOUNT_CREDITED_SUCCESS = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User account was successfully credited";
    public static final String INSUFFICIENT_FUND_CODE = "006";
    public static final String INSUFFICIENT_FUND_MESSAGE = "Insufficient  funds!";
    public static final String ACCOUNT_DEBITED_SUCCESS = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "User account was successfully debited";
    public static final String TRANSFER_SUCCESSFUL_CODE = "008";
    public static final String TRANSFER_SUCCESSFUL_MESSAGE = "Transfer successful !";


    public static String generateAccountNumber(){
//        year + randomSix Digits
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        int randNumber = (int)Math.floor( Math.random() * (max - min + 1) + min );

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();

    }
}
