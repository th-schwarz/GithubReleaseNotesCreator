package codes.thischwa.ghrnc;

public abstract class AbstractTest {

  static final String GITHUB_TOKEN = System.getenv("GHRNC_GITHUB_TOKEN");
  static final String OWNER = System.getenv("GHRNC_OWNER");
  static final String REPO = System.getenv("GHRNC_REPO");
}
