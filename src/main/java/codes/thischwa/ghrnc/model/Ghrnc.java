package codes.thischwa.ghrnc.model;

import java.util.List;

public record Ghrnc(
    String owner, 
    String repo, 
    String githubToken, 
    List<Section> sections
) {

  public Ghrnc(Ghrnc ghrnc, List<Section> sections) {
    this(ghrnc.owner(), ghrnc.repo(), ghrnc.githubToken(), sections);
  }
}