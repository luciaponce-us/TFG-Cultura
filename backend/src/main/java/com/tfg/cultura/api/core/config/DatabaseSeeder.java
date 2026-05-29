package com.tfg.cultura.api.core.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DatabaseSeeder implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger("appLogger");

    public DatabaseSeeder(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("🌱 Iniciando Database Seeder...");
        logger.info(" - Database: {}", mongoTemplate.getDb().getName());
        
        try {
            seedDatabase();
            logger.info("✅ Database seeding completado exitosamente");
        } catch (Exception e) {
            logger.error("❌ Error durante el seeding: {}", e.getMessage());
        }
    }

    private void seedDatabase() {
        clearDatabase();
        
        List<User> usuarios = seedUsuarios();
        seedSugerencias(usuarios);
        
        logger.info("💾 Todos los datos se han guardado correctamente");
    }

    private void clearDatabase() {
        logger.info("🗑️  Limpiando base de datos...");
        mongoTemplate.getDb().listCollectionNames().forEach(collectionName -> {
            if (!collectionName.startsWith("system.")) {
                mongoTemplate.dropCollection(collectionName);
                logger.info("   - Colección eliminada: {}", collectionName);
            }
        });
    }

    private List<User> seedUsuarios() {
        logger.info("👥 Creando colección: users");

        String password = "cultura123"; //NOSONAR
        String file = "https://www.soundczech.cz/temp/lorem-ipsum.pdf";

        User coordinador = User.builder()
            .username("coordinador")
            .password(passwordEncoder.encode(password)) //NOSONAR
            .name("Álvaro")
            .surname("Coordinador")
            .dni("33256506R")
            .phone("+34600123456")
            .email("coordinador@cultura.es")
            .paymentReceipt(file)
            .active(true)
            .role(Role.COORDINADOR)
            .createdAt(LocalDateTime.now())
            .build();
        
        User secretario = User.builder()
            .username("secretario")
            .password(passwordEncoder.encode(password)) //NOSONAR
            .name("Aurora")
            .surname("Secretaria")
            .dni("65403949E")
            .phone("+34600123457")
            .email("secretario@cultura.es")
            .paymentReceipt(file)
            .active(true)
            .role(Role.SECRETARIO)
            .createdAt(LocalDateTime.now())
            .build();
        
        User encargado = User.builder()
            .username("encargado")
            .password(passwordEncoder.encode(password)) //NOSONAR
            .name("Luis")
            .surname("Encargado")
            .dni("76824876T")
            .phone("+34600123458")
            .email("encargado@cultura.es")
            .paymentReceipt(file)
            .active(true)
            .role(Role.ENCARGADO)
            .createdAt(LocalDateTime.now())
            .build();
        
        User colaborador = User.builder()
            .username("colaborador")
            .password(passwordEncoder.encode(password)) //NOSONAR
            .name("Atenea")
            .surname("Colaboradora")
            .dni("51417783H")
            .phone("+34600123459")
            .email("colaborador@cultura.es")
            .paymentReceipt(file)
            .active(true)
            .role(Role.COLABORADOR)
            .createdAt(LocalDateTime.now())
            .build();
        
        User socio = User.builder()
            .username("socio")
            .password(passwordEncoder.encode(password)) //NOSONAR
            .name("Lucía")
            .surname("Socia")
            .dni("28712238G")
            .phone("+34600123460")
            .email("socio@cultura.es")
            .paymentReceipt(file)
            .active(true)
            .role(Role.SOCIO)
            .createdAt(LocalDateTime.now())
            .build();
        
        List<User> usuarios = List.of(
            coordinador,
            secretario,
            encargado,
            colaborador,
            socio
        );

        Collection<User> users = mongoTemplate.insertAll(usuarios);
        logger.info("✅👥 Insertados {} usuarios", users.size());
        return users.stream().toList();
    }

    private void seedSugerencias(List<User> usuarios) {
        logger.info("💡 Creando colección: suggestions");

        String idCoordinador = usuarios.get(0).getId();
        String idSecretario = usuarios.get(1).getId();
        String idEncargado = usuarios.get(2).getId();
        String idColaborador = usuarios.get(3).getId();
        String idSocio = usuarios.get(4).getId();

        Suggestion s1 = Suggestion.builder()
            .title("Añadir torneos de juegos de mesa")
            .description("Organizar torneos mensuales de juegos como Catan, Carcassonne o Terraforming Mars.")
            .type(SuggestionType.EVENT)
            .authorId(idColaborador)
            .build();

        Suggestion s2 = Suggestion.builder()
            .title("Ampliar catálogo de mangas")
            .description("Incluir colecciones populares actuales y completar series incompletas.")
            .type(SuggestionType.CATALOG)
            .authorId(idSocio)
            .supportersId(List.of(idColaborador, idSecretario, idCoordinador))
            .build();
        
        Suggestion s3 = Suggestion.builder()
            .title("Talleres de iniciación al rol")
            .description("Crear talleres para aprender a jugar a rol, incluyendo partidas guiadas para principiantes.")
            .type(SuggestionType.EVENT)
            .authorId(idSocio)
            .supportersId(List.of(idEncargado))
            .build();
        
        Suggestion s4 = Suggestion.builder()
            .title("Ciclo de cine temático")
            .description("Organizar ciclos de cine por temáticas (terror, ciencia ficción, anime, etc.).")
            .type(SuggestionType.EVENT)
            .authorId(idSocio)
            .build();

        List<Suggestion> sugerencias = List.of(s1,s2,s3,s4);
        mongoTemplate.insertAll(sugerencias);
        logger.info("✅💡 Insertadas {} sugerencias", sugerencias.size());
    }

}
