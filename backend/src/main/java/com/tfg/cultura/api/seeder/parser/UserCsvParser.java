package com.tfg.cultura.api.seeder.parser;

import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCsvParser extends CsvParser {

	private static final String CSV_FILE_PATH = "data/users.csv";
	private final PasswordEncoder passwordEncoder;

	public List<User> loadUsersFromCsv() {
		return loadCsv(CSV_FILE_PATH, this::mapLine);
	}

	private User mapLine(String line) {
		String[] parts = lineToParts(line);

		return User.builder().username(clean(parts[0])).password(passwordEncoder.encode(clean(parts[1])))
				.name(clean(parts[2])).surname(clean(parts[3])).dni(clean(parts[4])).phone(clean(parts[5]))
				.email(clean(parts[6])).role(Role.valueOf(clean(parts[7]))).paymentReceipt(clean(parts[8]))
				.active(Boolean.parseBoolean(clean(parts[9]))).build();
	}
}
