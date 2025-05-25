package codes.thischwa.ghrnc;

import codes.thischwa.ghrnc.model.Conf;
import codes.thischwa.ghrnc.model.Section;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
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
    Map<String, List<GHIssue>> groupedIssues = groupByConfiguredSections(closedIssues);

    // Generate markdown content
    return generateMarkdown(groupedIssues);
  }

  private Map<String, List<GHIssue>> groupByConfiguredSections(List<GHIssue> issues) {
    return issues.stream().collect(Collectors.groupingBy(issue -> {
      Collection<GHLabel> labels = issue.getLabels();
      if (labels.isEmpty()) {
        return "Uncategorized";
      }
      String labelName = labels.stream().findFirst().map(GHLabel::getName).orElse("Unlabeled");
      return config.ghrnc().sections().stream()
          .filter(section -> section.getLabels().contains(labelName)).findFirst()
          .map(Section::getTitle).orElse("Uncategorized");
    }));
  }

  private String generateMarkdown(Map<String, List<GHIssue>> groupedIssues) {
    StringBuilder markdown = new StringBuilder();
    groupedIssues.forEach((section, issues) -> {
      markdown.append("## ").append(section).append("\n\n");
      for (GHIssue issue : issues) {
        markdown.append("- ").append(issue.getTitle()).append(" [#").append(issue.getNumber())
            .append("](").append(issue.getHtmlUrl()).append(")\n");
      }
      markdown.append("\n");
    });
    return markdown.toString();
  }
}