package codes.thischwa.ghrnc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReleaseNotesServiceTest extends AbstractTest {

 @Test
 void test() throws Exception {
    GithubService service = new GithubService(GITHUB_TOKEN, OWNER, REPO);
    ReleaseNotesService changelogService = new ReleaseNotesService(service, new YamlUtil().readInputStream(this.getClass().getResourceAsStream(
        "/ghrnc.yml")));
    String changelog = changelogService.generateChangelog("0.9.0");
    String expected = readInputStreamToString(this.getClass().getResourceAsStream("/changelog.md"));
    assertEquals(expected, changelog);
  }


  private static String readInputStreamToString(InputStream inputStream) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
  }

}
