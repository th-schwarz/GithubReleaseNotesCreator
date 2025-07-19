package codes.thischwa.ghrnc;

import codes.thischwa.ghrnc.model.Conf;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHMilestone;

public class ReleaseNotesService {

  private final GithubService githubService;
  private final Conf config;

  public ReleaseNotesService(GithubService githubService, Conf config) {
    this.githubService = githubService;
    this.config = config;
  }

  public String generateChangelog(String milestoneTitle)
      throws IOException, NoSuchElementException {
    // Find the milestone
    GHMilestone milestone = githubService.findMilestone(milestoneTitle);

    // Get closed issues for the milestone
    List<GHIssue> closedIssues = githubService.getClosedIssuesForMilestone(milestone);

    // Group issues by their labels
    Map<String, List<GHIssue>> groupedIssues = groupByConfiguredLabels(closedIssues);

    // Generate markdown content
    return generateMarkdown(groupedIssues);
  }

  private Map<String, List<GHIssue>> groupByConfiguredLabels(List<GHIssue> issues) {
    Map<String, List<GHIssue>> groupedIssues = new HashMap<>();
    config.ghrnc().sections().forEach(section -> {
      section.getLabels().forEach(label -> {
        List<GHIssue> issuesForLabel = issues.stream().filter(issue -> issue.getLabels().stream()
            .anyMatch(ghLabel -> ghLabel.getName().equals(label))).collect(Collectors.toList());
        groupedIssues.put(label, issuesForLabel);
      });
    });
    return groupedIssues;
  }

  private String generateMarkdown(Map<String, List<GHIssue>> groupedIssues) {
    StringBuilder markdown = new StringBuilder();
    config.ghrnc().sections().forEach(section -> {
      section.getLabels().forEach(label -> {
        List<GHIssue> issues = groupedIssues.get(label);
        if (issues != null && !issues.isEmpty()) {
          markdown.append("## ").append(section.getTitle()).append("\n\n");
          for (GHIssue issue : issues) {
            markdown.append("- ").append(issue.getTitle()).append(" [#").append(issue.getNumber())
                .append("](").append(issue.getHtmlUrl()).append(")\n");
          }
          markdown.append("\n");
        }
      });
    });
    return markdown.toString().trim();
  }
}