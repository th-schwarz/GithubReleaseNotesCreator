package codes.thischwa.ghrnc;

import codes.thischwa.ghrnc.model.Conf;
import codes.thischwa.ghrnc.model.Section;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHMilestone;

public class ReleaseNotesService {

  private static final String NL = "\n";

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
    Map<String, List<GHIssue>> groupedIssues = groupBySection(closedIssues);

    // Generate markdown content
    return generateMarkdown(groupedIssues);
  }

  Map<String, List<GHIssue>> groupBySection(List<GHIssue> issues) {
    final Map<String, List<GHIssue>> groupedIssues = new HashMap<>();

    for (GHIssue issue : issues) {
      for (String label : issue.getLabels().stream().map(GHLabel::getName).toList()) {
        for (Section section : config.ghrnc().sections()) {
          if (section.getLabels().contains(label)) {
            String sectionTitle = section.getTitle();
            if (!groupedIssues.containsKey(sectionTitle)) {
              groupedIssues.put(sectionTitle, new ArrayList<>());
            }
            groupedIssues.get(sectionTitle).add(issue);
          }
        }
      }
    }
    return groupedIssues;
  }
  String generateMarkdown(Map<String, List<GHIssue>> groupedIssues) {
    StringBuilder markdown = new StringBuilder();
    config.ghrnc().sections().forEach(section -> {
        List<GHIssue> issues = groupedIssues.get(section.getTitle());
        if (issues != null && !issues.isEmpty()) {
          markdown.append("## ").append(section.getTitle()).append(NL).append(NL);
          for (GHIssue issue : issues) {
            markdown.append("- ").append(issue.getTitle()).append(" [#").append(issue.getNumber())
                .append("](").append(issue.getHtmlUrl()).append(")").append(NL);
          }
          markdown.append(NL);
        }
    });
    return markdown.toString().trim();
  }
}