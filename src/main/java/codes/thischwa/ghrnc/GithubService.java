package codes.thischwa.ghrnc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GithubService {
  private static final Logger LOG = LoggerFactory.getLogger(GithubService.class);

  private final GitHub github;
  private final GHRepository repository;

  public GithubService(@Nullable String githubToken, String repositoryName) throws IOException {
    this(null, githubToken, repositoryName);
  }

  public GithubService(@Nullable String baseUrl, @Nullable String githubToken,
      String repositoryName) throws IOException {
    Objects.requireNonNull(repositoryName, "Repository name must not be null");
    this.github = initGithubRepository(baseUrl, githubToken);
    this.repository = github.getRepository(repositoryName);
    LOG.debug("GitHub-Service initialized for {}", repositoryName);
  }

  private GitHub initGithubRepository(@Nullable String baseUrl, @Nullable String githubToken)
      throws IOException {
    GitHubBuilder builder = isBlank(githubToken) ? new GitHubBuilder() :
        new GitHubBuilder().withOAuthToken(githubToken);
    return isBlank(baseUrl) ? builder.build() : builder.withEndpoint(baseUrl).build();
  }

  private boolean isBlank(@Nullable String str) {
    return (str == null || str.isBlank());
  }

  public GHMilestone findMilestone(String title) {
    for (GHMilestone milestone : repository.listMilestones(GHIssueState.ALL)) {
      if (milestone.getTitle().equals(title)) {
        return milestone;
      }
    }
    throw new NoSuchElementException("No such milestone: " + title);
  }

  public List<GHIssue> getClosedIssuesForMilestone(GHMilestone milestone) throws IOException {
    List<GHIssue> closedIssues = repository.getIssues(GHIssueState.CLOSED, milestone);
    LOG.info("Found {} closed issues for milestone {}", closedIssues.size(), milestone.getTitle());
    return closedIssues;
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