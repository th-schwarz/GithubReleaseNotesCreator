package codes.thischwa.ghrnc;

import codes.thischwa.ghrnc.model.Conf;
import codes.thischwa.ghrnc.model.Ghrnc;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;

public class YamlUtil {

  private final ObjectMapper MAPPER;

  public YamlUtil() {
    MAPPER = new ObjectMapper(new YAMLFactory());
    MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
  }

  public Conf readInputStream(InputStream inputStream) {
    try {
      Conf conf = MAPPER.readValue(inputStream, Conf.class);

      if (!conf.sectionsExists()) {
        try (InputStream defaultSectionsStream = this.getClass().getResourceAsStream(
            "/sections-default.yaml")) {
          Conf defaultConf = MAPPER.readValue(defaultSectionsStream, Conf.class);
          Ghrnc ghrnc = new Ghrnc(conf.ghrnc(), defaultConf.ghrnc().sections());
          conf = new Conf(ghrnc);
        }
      }
      return conf;
    } catch ( StreamReadException | DatabindException e) {
      throw new IllegalArgumentException("Error while processing yaml.", e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}