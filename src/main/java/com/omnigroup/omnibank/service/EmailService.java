package com.omnigroup.omnibank.service;


import com.omnigroup.omnibank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
