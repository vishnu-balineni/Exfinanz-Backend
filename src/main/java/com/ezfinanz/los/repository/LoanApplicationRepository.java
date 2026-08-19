package com.ezfinanz.los.repository;

import com.ezfinanz.los.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByApplicantId(Long userId);

    List<LoanApplication> findByStatus(String status);
}
