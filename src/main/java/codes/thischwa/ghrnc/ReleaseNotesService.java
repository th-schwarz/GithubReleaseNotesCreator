package codes.thischwa.ghrnc;

import codes.thischwa.ghrnc.model.Conf;
import codes.thischwa.ghrnc.model.Ghrnc;
import codes.thischwa.ghrnc.model.Section;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReleaseNotesService {

  private static final Logger LOG = LoggerFactory.getLogger(ReleaseNotesService.class);
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

  Set<GHUser> getContributors(List<GHIssue> closedIssues, Ghrnc ghrnc) {
    Set<GHUser> contributors = new HashSet<>();
    if (!ghrnc.isContributorsEnabled()) {
      return contributors;
    }
    for (GHIssue issue : closedIssues) {
      GHUser contributor = issue.getUser();
      if (contributor != null && !contributor.getLogin().endsWith("[bot]") &&
          !ghrnc.contributors().excludes().contains(contributor.getLogin())) {
        contributors.add(issue.getUser());
        LOG.debug("Contributor found: {}", issue.getUser().getLogin());
      }
    }
    LOG.info("Found {} contributors in {} closed issues", contributors.size(), closedIssues.size());
    return contributors;
  }

  String generateMarkdown(Map<String, List<GHIssue>> groupedIssues) {
    StringBuilder markdown = new StringBuilder();
    List<GHIssue> usedIssues = new ArrayList<>();
    config.ghrnc().sections().forEach(section -> {
      List<GHIssue> issues = groupedIssues.get(section.getTitle());
      if (issues != null && !issues.isEmpty()) {
        usedIssues.addAll(issues);
        markdown.append("## ").append(section.getTitle()).append(NL).append(NL);
        for (GHIssue issue : issues) {
          markdown.append("- ").append(issue.getTitle()).append(" [#").append(issue.getNumber())
              .append("](").append(issue.getHtmlUrl()).append(")").append(NL);
        }
        markdown.append(NL);
      }
    });

    Set<GHUser> contributors = getContributors(usedIssues, config.ghrnc());
    if (config.ghrnc().isContributorsEnabled() && !contributors.isEmpty()) {
      List<GHUser> contributorsSorted = new ArrayList<>(contributors);
      contributorsSorted.sort((u1, u2) -> u1.getLogin().compareToIgnoreCase(u2.getLogin()));
      markdown.append("## ").append(config.ghrnc().contributors().title()).append(NL).append(NL);
      markdown.append(config.ghrnc().contributors().message()).append(NL).append(NL);
      contributorsSorted.forEach(contributor -> markdown.append("[@").append(contributor.getLogin()).append("](")
          .append(contributor.getHtmlUrl()).append(")").append(", "));
      if (markdown.toString().endsWith(", ")) {
        markdown.delete(markdown.length() - 2, markdown.length());
      }
    }
    return markdown.toString().trim();
  }
}