package com.tfg.cultura.api.sections.model;

import com.tfg.cultura.api.users.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Section {
	@Id
	private String id;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
	private String name;

	@Builder.Default
	@NotNull(message = "La lista de encargados es obligatoria")
	@DocumentReference
	private Set<User> managers = new HashSet<>(); // Encargados de la sección

	@Builder.Default
	@NotNull(message = "La lista de colaboradores es obligatoria")
	@DocumentReference
	private Set<User> collaborators = new HashSet<>(); // Colaboradores de la sección

	@CreatedDate
	private LocalDateTime createdAt;

}
