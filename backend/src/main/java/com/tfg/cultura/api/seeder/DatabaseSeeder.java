package com.tfg.cultura.api.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.seeder.parser.BooksCsvParser;
import com.tfg.cultura.api.seeder.parser.SectionsCsvParser;
import com.tfg.cultura.api.seeder.parser.UserCsvParser;
import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.users.model.User;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(name = "app.seed-enabled", havingValue = "true")
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;

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
        List<Section> secciones = seedSections(usuarios);
        List<Category> categorias = seedCategories();
        seedBooks(secciones, categorias);

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

        User usuario1 = usuarios.get(0);
        User usuario2 = usuarios.get(1);
        User usuario3 = usuarios.get(2);
        User usuario4 = usuarios.get(3);
        User usuario5 = usuarios.get(4);

        Suggestion s1 = Suggestion.builder()
                .title("Añadir torneos de juegos de mesa")
                .description("Organizar torneos mensuales de juegos como Catan, Carcassonne o Terraforming Mars.")
                .type(SuggestionType.EVENT)
                .author(usuario1)
                .totalSupporters(0)
                .build();

        Suggestion s2 = Suggestion.builder()
                .title("Ampliar catálogo de mangas")
                .description("Incluir colecciones populares actuales y completar series incompletas.")
                .type(SuggestionType.CATALOG)
                .author(usuario5)
                .supporters(List.of(usuario1, usuario2, usuario3, usuario4))
                .totalSupporters(4)
                .build();

        Suggestion s3 = Suggestion.builder()
                .title("Talleres de iniciación al rol")
                .description(
                        "Crear talleres para aprender a jugar a rol, incluyendo partidas guiadas para principiantes.")
                .type(SuggestionType.EVENT)
                .author(usuario5)
                .supporters(List.of(usuario3))
                .totalSupporters(1)
                .build();

        Suggestion s4 = Suggestion.builder()
                .title("Ciclo de cine temático")
                .description("Organizar ciclos de cine por temáticas (terror, ciencia ficción, anime, etc.).")
                .type(SuggestionType.EVENT)
                .author(usuario5)
                .totalSupporters(0)
                .build();

        Suggestion s5 = Suggestion.builder()
                .title("Lorem ipsum dolor sit amet, consectetur porttitor.")
                .description(
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam sit amet ex quis velit blandit volutpat et sed mauris. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Duis finibus volutpat risus at dictum. Curabitur nunc tortor orci aliquam. ")
                .type(SuggestionType.OTHER)
                .author(usuario1)
                .supporters(List.of(usuario2, usuario3))
                .totalSupporters(2)
                .build();

        List<Suggestion> sugerencias = List.of(s1, s2, s3, s4, s5);
        mongoTemplate.insertAll(sugerencias);
        logger.info("✅💡 Insertadas {} sugerencias", sugerencias.size());
    }

    private List<Section> seedSections(List<User> usuarios) {
        logger.info("📚 Creando colección: sections");

        Map<String, User> usersByUsername = usuarios.stream()
                .collect(Collectors.toMap(
                        User::getUsername,
                        Function.identity()));

        List<Section> sectionsFromCsv = new SectionsCsvParser().loadSectionsFromCsv(usersByUsername);

        Collection<Section> sections = mongoTemplate.insertAll(sectionsFromCsv);
        logger.info("✅📚 Insertadas {} secciones", sections.size());
        return sections.stream().toList();
    }

    private List<Category> seedCategories() {
        logger.info("🏷️  Creando colección: categories");
        Category c1 = Category.builder().name("Ficción").color("#FF5733").build();
        Category c2 = Category.builder().name("No Ficción").color("#33FF57").build();
        Category c3 = Category.builder().name("Ciencia Ficción").color("#3357FF").build();
        List<Category> categories = List.of(c1, c2, c3);
        mongoTemplate.insertAll(categories);
        logger.info("✅🏷️ Insertadas {} categorías", categories.size());
        return categories;
    }

    private void seedBooks(List<Section> sections, List<Category> categories) {
        logger.info("📖 Creando colección: books");

        Map<String, Section> sectionsByName = sections.stream()
                .collect(Collectors.toMap(
                        Section::getName,
                        Function.identity()));

        Map<String, Category> categoriesByName = categories.stream()
                .collect(Collectors.toMap(
                        Category::getName,
                        Function.identity()));

                        List<Book> booksFromCsv = new BooksCsvParser().loadBooksFromCsv(
                sectionsByName,
                categoriesByName
        );
        Collection<Book> books = mongoTemplate.insertAll(booksFromCsv);
        List<Book> booksWithPrequelsAndSequels = new BooksCsvParser().assignPrequelsAndSequels(books.stream().toList());
        System.out.println("Books with prequels and sequels: " + booksWithPrequelsAndSequels.stream().map(b-> "Libro:" + b.getName()+ " - Prequel: " + b.getPrequel() + " - Sequel: " + b.getSequel()).toList());

        Collection<Book> updatedBooks = bookRepository.saveAll(booksWithPrequelsAndSequels);
        logger.info("✅📖 Insertados {} libros", updatedBooks.size());
    }
}
