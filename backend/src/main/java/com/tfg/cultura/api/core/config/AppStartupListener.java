package com.tfg.cultura.api.core.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Component
public class AppStartupListener {
    private static final Logger log = LoggerFactory.getLogger("appLogger");

    @Value("${app.seed.enabled}")
    private boolean seedEnabled;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("🚀 Backend listo para recibir peticiones");
        log.info("🌐 Frontend URL: {}", frontendUrl);
        log.info("🔧 Configuración actual: seedEnabled={}", seedEnabled);
    }
}
