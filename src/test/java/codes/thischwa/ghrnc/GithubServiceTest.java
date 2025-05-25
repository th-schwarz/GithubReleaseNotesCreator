package codes.thischwa.ghrnc;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHMilestone;

public class GithubServiceTest extends AbstractTest {

  @Test
  void test() throws IOException {
    GithubService service = new GithubService(GITHUB_TOKEN, OWNER, REPO);

    String milestoneTitle = "0.9.0";
    GHMilestone milestone = service.findMilestone(milestoneTitle);
    assertNotNull(milestone);
    assertEquals(milestoneTitle, milestone.getTitle());
    assertEquals(5, milestone.getClosedIssues());

    List<GHIssue> issues = service.getClosedIssuesForMilestone(milestone);
    assertEquals(5, issues.size());
    assertTrue(issues.stream().allMatch(issue -> issue.getPullRequest() == null));
    GHIssue issue = issues.get(0);
    assertEquals(51, issue.getNumber());
    assertEquals("if greeting is disabled, not_found should be thrown", issue.getTitle());
    assertTrue(issue.getUrl().toString().endsWith("51"));

    Map<GHLabel, List<GHIssue>> grouped = service.groupByLabel(issues);
    assertEquals(2, grouped.size());
    GHLabel label = grouped.keySet().iterator().next();
    assertEquals("enhancement", label.getName());
    assertEquals(2, grouped.get(label).size());
    assertTrue(grouped.get(label).stream().allMatch(issue1 -> issue1.getPullRequest() == null));
    GHIssue issue1 = grouped.get(label).get(0);
    assertEquals(50, issue1.getNumber());
    assertEquals("properties: move zones out of dyndrest", issue1.getTitle());
  }
}
