package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.catalog.model.enumerators.ItemCondition;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.model.dto.SectionReference;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemResponse {

	protected String id;
	protected String name;
	protected String description;
	protected String imageUrl;
	protected ItemCondition condition;
	protected String comments;
	protected Boolean loanAvailable;
	protected Boolean publicated;
	protected LocalDate purchasedAt;
	protected BigDecimal price;
	protected Integer copies;
	protected Integer availableCopies;
	protected Integer loanDays;
	protected SectionReference section;
	protected Set<Category> categories;
	protected LocalDateTime createdAt;

	protected ItemResponse(Item item) {
		this.id = item.getId();
		this.name = item.getName();
		this.description = item.getDescription();
		this.imageUrl = item.getImageUrl();
		this.condition = item.getCondition();
		this.comments = item.getComments();
		this.loanAvailable = item.getLoanAvailable();
		this.publicated = item.getPublicated();
		this.purchasedAt = item.getPurchasedAt();
		this.price = item.getPrice();
		this.copies = item.getCopies();
		this.availableCopies = item.getAvailableCopies();
		this.loanDays = item.getLoanDays();
		this.section = item.getSection() != null ? new SectionReference(item.getSection()) : null;
		this.categories = item.getCategories();
		this.createdAt = item.getCreatedAt();
	}
}
