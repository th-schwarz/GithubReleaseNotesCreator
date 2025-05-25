package codes.thischwa.ghrnc;

import codes.thischwa.ghrnc.model.Conf;
import codes.thischwa.ghrnc.model.Ghrnc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class YamlUtilTest {

  @Test
  void testCommonConfig() {
    YamlUtil yamlUtil = new YamlUtil();
    Conf result = yamlUtil.readInputStream(this.getClass().getResourceAsStream("/ghrnc.yml"));
    assertNotNull(result);
    Ghrnc config = result.ghrnc();
    check(config, true);
  }

  @Test
  void testMissingSections() {
    YamlUtil yamlUtil = new YamlUtil();
    Conf result = yamlUtil.readInputStream(
        this.getClass().getResourceAsStream("/ghrnc_without-sections.yml"));
    assertNotNull(result);
    Ghrnc config = result.ghrnc();
    assertEquals("owner/project", config.repo());
    check(config, false);
  }

  private static void check(Ghrnc config, boolean checkImprovements) {
    assertEquals("owner/project", config.repo());
    assertEquals("ghp_abcdefghijklmnopqrstxyz0123456789bla", config.githubToken());

    assertEquals("Enhancements", config.sections().get(0).getTitle());
    assertTrue(config.sections().get(0).getLabels().contains("enhancement"));
    assertEquals("Bugs", config.sections().get(1).getTitle());
    assertTrue(config.sections().get(1).getLabels().contains("bug"));
    if (checkImprovements) {
      assertEquals("Improvements", config.sections().get(2).getTitle());
      assertTrue(config.sections().get(2).getLabels().contains("improvement"));
    }
  }
}