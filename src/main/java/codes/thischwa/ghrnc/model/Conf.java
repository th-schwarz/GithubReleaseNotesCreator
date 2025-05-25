package codes.thischwa.ghrnc.model;

public record Conf(Ghrnc ghrnc) {

  public boolean sectionsExists() {
    return ghrnc().sections() != null && !ghrnc().sections().isEmpty();
  }
}

