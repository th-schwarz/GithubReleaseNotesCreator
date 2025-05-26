package codes.thischwa.ghrnc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;
import org.slf4j.Logger;

public class GithubService {

  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(GithubService.class);
  private final GitHub github;
  private final GHRepository repo;

  public GithubService(String githubToken, String repo) throws IOException {
    assert githubToken != null;
    assert repo != null;
    github = new GitHubBuilder().withOAuthToken(githubToken).build();
    this.repo = github.getRepository(repo);
    LOG.debug("GitHub-Service initialized for {}", repo);
  }

  public GHMilestone findMilestone(String title) throws NoSuchElementException {
    PagedIterable<GHMilestone> milestonePage = repo.listMilestones(GHIssueState.ALL);
    for (GHMilestone mileStone : milestonePage) {
      if (mileStone.getTitle().equals(title)) {
        return mileStone;
      }
    }
    throw new NoSuchElementException("No such milestone: " + title);
  }

  public List<GHIssue> getClosedIssuesForMilestone(GHMilestone milestone) throws IOException {
    List<GHIssue> ghIssues = repo.getIssues(GHIssueState.CLOSED, milestone);
    List<GHIssue> foundIssues = ghIssues.stream().filter(issue -> issue.getPullRequest() == null)
        .collect(Collectors.toList());
    LOG.debug("Found {} closed issues for milestone {}", foundIssues.size(), milestone.getTitle());
    return foundIssues;
  }

  public Map<GHLabel, List<GHIssue>> groupByLabel(List<GHIssue> issues) {
    Map<GHLabel, List<GHIssue>> grouped = new HashMap<>();
    for (GHIssue issue : issues) {
      issue.getLabels().stream().findFirst()
          .ifPresent(label -> grouped.computeIfAbsent(label, k -> new ArrayList<>()).add(issue));
    }
    LOG.debug("{} issues grouped in {} labels.", issues.size(), grouped.size());
    return grouped;
  }
}