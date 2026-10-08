package com.tfg.cultura.api.loans.repository;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;

public interface LoanRepository extends MongoRepository<Loan, String> {
    boolean existsByCode(String code);
    Set<Loan> findByUserIdAndStatusAndItemType(String userId, LoanStatus status, ItemType itemType);

}
