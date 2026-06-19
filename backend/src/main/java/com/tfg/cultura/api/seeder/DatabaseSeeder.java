package com.tfg.cultura.api.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.seeder.parser.UserCsvParser;
import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(name = "app.seed-enabled", havingValue = "true")
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger("appLogger");

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

        List<User> usersFromCsv = new UserCsvParser(passwordEncoder).loadUsersFromCsv();

        Collection<User> users = mongoTemplate.insertAll(usersFromCsv);
        
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
                .totalSupporters(0)
                .build();

        Suggestion s2 = Suggestion.builder()
                .title("Ampliar catálogo de mangas")
                .description("Incluir colecciones populares actuales y completar series incompletas.")
                .type(SuggestionType.CATALOG)
                .authorId(idSocio)
                .supportersId(List.of(idColaborador, idSecretario, idCoordinador, idEncargado))
                .totalSupporters(4)
                .build();

        Suggestion s3 = Suggestion.builder()
                .title("Talleres de iniciación al rol")
                .description(
                        "Crear talleres para aprender a jugar a rol, incluyendo partidas guiadas para principiantes.")
                .type(SuggestionType.EVENT)
                .authorId(idSocio)
                .supportersId(List.of(idEncargado))
                .totalSupporters(1)
                .build();

        Suggestion s4 = Suggestion.builder()
                .title("Ciclo de cine temático")
                .description("Organizar ciclos de cine por temáticas (terror, ciencia ficción, anime, etc.).")
                .type(SuggestionType.EVENT)
                .authorId(idSocio)
                .totalSupporters(0)
                .build();

        Suggestion s5 = Suggestion.builder()
                .title("Lorem ipsum dolor sit amet, consectetur porttitor.")
                .description(
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam sit amet ex quis velit blandit volutpat et sed mauris. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Duis finibus volutpat risus at dictum. Curabitur nunc tortor orci aliquam. ")
                .type(SuggestionType.OTHER)
                .authorId(idColaborador)
                .supportersId(List.of(idSocio, idEncargado))
                .totalSupporters(2)
                .build();

        List<Suggestion> sugerencias = List.of(s1, s2, s3, s4, s5);
        mongoTemplate.insertAll(sugerencias);
        logger.info("✅💡 Insertadas {} sugerencias", sugerencias.size());
    }

}
