package com.ezfinanz.los.repository;

import com.ezfinanz.los.model.BankDetails;
import com.ezfinanz.los.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
    Optional<BankDetails> findByUser(User user);
}
