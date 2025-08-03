package codes.thischwa.ghrnc;

import codes.thischwa.ghrnc.model.Ghrnc;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHIssue;

public class ReleaseNotesServiceTest extends AbstractTest {

  @Test
  void testOnline() throws Exception {
    GithubService service = new GithubService(GITHUB_TOKEN, REPO);
    ReleaseNotesService changelogService = new ReleaseNotesService(service,
        new YamlUtil().readInputStream(this.getClass().getResourceAsStream("/ghrnc.yml")));
    String changelog = changelogService.generateChangelog("0.9.0");
    String expected = readInputStreamToString(this.getClass().getResourceAsStream("/changelog.md"));
    assertEquals(expected, changelog);
  }

  @Test
  @Disabled
  void testSpringOnline() throws Exception {
    GithubService service = new GithubService(null, "spring-projects/spring-framework");
    ReleaseNotesService changelogService = new ReleaseNotesService(service,
        new YamlUtil().readInputStream(this.getClass().getResourceAsStream("/spring-framework-contr.yml")));
    String changelog = changelogService.generateChangelog("6.2.5");
    String expected = readInputStreamToString(this.getClass().getResourceAsStream("/changelog-spring-contr.md"));
    assertEquals(expected, changelog);
  }

  @Test
  void testSpringOffline() throws Exception {
    List<GHIssue> issues = GithubApiYamlTestConfig.configureObjectMapper()
        .readValue(this.getClass().getResourceAsStream("/issues_spring-6.2.5.yml"),
            new TypeReference<>() {
            });
    ReleaseNotesService releaseNotesService = new ReleaseNotesService(null,
        new YamlUtil().readInputStream(
            this.getClass().getResourceAsStream("/spring-framework.yml")));
    Map<String, List<GHIssue>> groupedIssues = releaseNotesService.groupBySection(issues);
    String actual = releaseNotesService.generateMarkdown(groupedIssues);
    String expected =
        readInputStreamToString(this.getClass().getResourceAsStream("/changelog-spring.md"));
    assertEquals(expected, actual);
  }

  @Test
  void testGroupByConfiguredLabels() throws IOException {
    List<GHIssue> issues = GithubApiYamlTestConfig.configureObjectMapper()
        .readValue(this.getClass().getResourceAsStream("/issues_spring-6.2.5.yml"),
            new TypeReference<>() {
            });
    ReleaseNotesService changelogService = new ReleaseNotesService(null,
        new YamlUtil().readInputStream(
            this.getClass().getResourceAsStream("/spring-framework.yml")));
    Map<String, List<GHIssue>> groupedIssues = changelogService.groupBySection(issues);
    assertEquals(4, groupedIssues.size());
    assertEquals(1, groupedIssues.get(":hammer: Dependency Upgrades").size());
    assertEquals(3, groupedIssues.get(":lady_beetle: Bug Fixes").size());
    assertEquals(6, groupedIssues.get(":star: New Features").size());
    assertEquals(4, groupedIssues.get(":notebook_with_decorative_cover: Documentation").size());
  }

  private static String readInputStreamToString(InputStream inputStream) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
  }

}