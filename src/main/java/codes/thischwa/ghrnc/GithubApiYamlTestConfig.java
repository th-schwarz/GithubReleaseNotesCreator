package codes.thischwa.ghrnc;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import org.jetbrains.annotations.NotNull;

/**
 * A utility class for configuring an {@link ObjectMapper} with specific settings tailored
 * for processing YAML data related to GitHub API usage in test scenarios.
 */
public class GithubApiYamlTestConfig {

  public static @NotNull ObjectMapper configureObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.setVisibility(new VisibilityChecker.Std(NONE, NONE, NONE, NONE, ANY));
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    // Deaktiviert Injectable Values
    mapper.setInjectableValues(new InjectableValues() {
        @Override
        public Object findInjectableValue(Object valueId, DeserializationContext ctxt, BeanProperty forProperty, Object beanInstance) {
            return null;
        }
    });

    return mapper;
  }
}