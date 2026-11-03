package com.tfg.cultura.api.loans.repository;

import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoanRepository extends MongoRepository<Loan, String> {
	boolean existsByCode(String code);
	Set<Loan> findByUserIdAndStatusAndItemType(String userId, LoanStatus status, ItemType itemType);
	Page<Loan> findAll(Pageable pageable);
	Page<Loan> findByUserId(String userId, Pageable pageable);
	Page<Loan> findByItemId(String itemId, Pageable pageable);
	Page<Loan> findByUserIdAndItemId(String userId, String itemId, Pageable pageable);

}
