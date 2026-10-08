package com.tfg.cultura.api.seeder.parser;

import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LoanCsvParser extends CsvParser {

	private static final String CSV_FILE_PATH = "data/loans.csv";

	public List<Loan> loadLoansFromCsv(Map<String, String> usersIdByUsername, Map<String, String> itemsIdByName) {
		return loadCsv(CSV_FILE_PATH, line -> mapLine(line, usersIdByUsername, itemsIdByName));
	}

	private Loan mapLine(String line, Map<String, String> usersIdByUsername, Map<String, String> itemsIdByName) {
		String[] parts = lineToParts(line);

		return Loan.builder().code(clean(parts[0])).status(LoanStatus.valueOf(clean(parts[1])))
				.requestDate(parseLocalDate(parts[2])).cancelDate(parseNullableLocalDate(parts[3]))
				.startDate(parseNullableLocalDate(parts[4])).dueDate(parseNullableLocalDate(parts[5]))
				.returnDate(parseNullableLocalDate(parts[6])).rejectionDate(parseNullableLocalDate(parts[7]))
				.userId(usersIdByUsername.get(clean(parts[8]))).itemId(itemsIdByName.get(clean(parts[9]))).build();
	}

}
