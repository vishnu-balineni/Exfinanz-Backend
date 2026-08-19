package com.ezfinanz.los.service;

import com.ezfinanz.los.model.BankDetails;
import com.ezfinanz.los.model.KycDocument;
import com.ezfinanz.los.model.User;
import com.ezfinanz.los.repository.BankDetailsRepository;
import com.ezfinanz.los.repository.KycDocumentRepository;
import com.ezfinanz.los.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final KycDocumentRepository kycRepository;
    private final BankDetailsRepository bankRepository;
    private final UserRepository userRepository;

    public void submitKycDocument(Long userId, String documentType, String documentUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        KycDocument doc = KycDocument.builder()
                .user(user)
                .documentType(documentType)
                .documentUrl(documentUrl)
                .verificationStatus("AUTO_VERIFIED") // Or PENDING based on real logic
                .build();
        kycRepository.save(doc);
    }

    public void submitBankDetails(Long userId, BankDetails request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If exists, update, otherwise save new
        BankDetails bd = bankRepository.findByUser(user).orElse(new BankDetails());
        bd.setUser(user);
        bd.setBankName(request.getBankName());
        bd.setAccountNumber(request.getAccountNumber());
        bd.setIfscCode(request.getIfscCode());
        bd.setAccountHolderName(request.getAccountHolderName());
        bd.setVerified(true);
        bankRepository.save(bd);
    }

    public void finalizeVerification(Long userId, String selfieImageBase64) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Finalize
        user.setKycVerified(true);
        if (selfieImageBase64 != null) {
            user.setSelfieImageBase64(selfieImageBase64);
        }
        userRepository.save(user);
    }

    public List<KycDocument> getUserDocuments(Long userId) {
        return kycRepository.findByUserId(userId);
    }

    public BankDetails getUserBankDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bankRepository.findByUser(user).orElse(null);
    }
}
