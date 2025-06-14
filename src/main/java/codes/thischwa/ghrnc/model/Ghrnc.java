package codes.thischwa.ghrnc.model;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public record Ghrnc(@Nullable String baseUrl, String repo, String githubToken,
                    List<Section> sections) {

  public Ghrnc(Ghrnc ghrnc, List<Section> sections) {
    this(ghrnc.baseUrl(), ghrnc.repo(), ghrnc.githubToken(), sections);
  }
}