package codes.thischwa.ghrnc.model;

import java.util.List;

public class Section {
  private String title;
  private List<String> labels;

  // Getter und Setter
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<String> getLabels() {
    return labels;
  }

  public void setLabels(List<String> labels) {
    this.labels = labels;
  }
}
