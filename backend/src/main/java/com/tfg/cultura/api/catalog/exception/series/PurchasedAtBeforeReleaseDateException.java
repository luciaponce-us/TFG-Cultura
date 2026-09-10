package com.tfg.cultura.api.catalog.exception.series;

import com.tfg.cultura.api.core.exception.ValidationException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurchasedAtBeforeReleaseDateException extends ValidationException {
	private static final Logger logger = LoggerFactory.getLogger("catalogLogger");
	public PurchasedAtBeforeReleaseDateException() {
		super(logger, Map.of("purchasedAt", "La fecha de compra no puede ser anterior a la fecha de estreno"));
	}

}
