package com.ezfinanz.los.repository;

import com.ezfinanz.los.model.LoanRepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepaymentScheduleRepository extends JpaRepository<LoanRepaymentSchedule, Long> {
    List<LoanRepaymentSchedule> findByLoanApplicationIdOrderByInstallmentNumberAsc(Long loanApplicationId);
}
