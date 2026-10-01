package com.tfg.cultura.api.core.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppStartupListener {
	private static final Logger log = LoggerFactory.getLogger("appLogger");

	private final AppProperties appProperties;

	@EventListener(ApplicationReadyEvent.class)
	public void onReady() {
		log.info("🚀 Backend listo para recibir peticiones");
		log.info("🌐 Frontend URL: {}", appProperties.frontendUrl());
		log.info("🔧 Configuración actual: seedEnabled={}", appProperties.seedEnabled());
	}
}
