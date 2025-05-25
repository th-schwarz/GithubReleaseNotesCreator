package codes.thischwa.ghrnc;

import codes.thischwa.ghrnc.model.Conf;
import codes.thischwa.ghrnc.model.Ghrnc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.slf4j.Logger;

public class Starter {

  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Starter.class);

  public static void main(String[] args) {
    if (args.length < 2) {
      LOG.error(
          "Usage: java -jar ghrnc.jar <milestone> <path-to-ghrnc-config.yaml> [<path-to-output-release-notes.md>]");
      System.exit(1);
    }

    String milestone = args[0];
    String configFilePath = args[1];
    String releaseNotesFilePath = (args.length > 2) ? args[2] : "release-notes.md";

    // check if the configuration file exists
    File configFile = new File(configFilePath);
    if (!configFile.exists()) {
      LOG.error("The configuration file does not exist: {}", configFilePath);
      System.exit(1);
    }

    try {
      // Delete the existing changelog file if it exists
      File releaseNotesFile = new File(releaseNotesFilePath);
      if (releaseNotesFile.exists()) {
        if (releaseNotesFile.delete()) {
          LOG.info("Existing release notes file deleted: {}", releaseNotesFilePath);
        } else {
          LOG.error("Failed to delete existing release notes file: {}", releaseNotesFilePath);
          System.exit(1);
        }
      }

      // Read the configuration file
      Conf conf = new YamlUtil().readInputStream(new FileInputStream(configFilePath));
      Ghrnc ghrnc = conf.ghrnc();
      ReleaseNotesService releaseNotesService = new ReleaseNotesService(
          new GithubService(ghrnc.githubToken(), ghrnc.owner(), ghrnc.repo()), conf);

      // Generate the release notes
      String releaseNotes = null;
      try {
        releaseNotes = releaseNotesService.generateChangelog(milestone);
      } catch (NoSuchElementException e) {
        LOG.error("No such milestone ({}) found on {}/{}", milestone, ghrnc.owner(), ghrnc.repo());
        System.exit(2);
      }

      // Save the release notes to the provided file path
      try (FileWriter writer = new FileWriter(releaseNotesFilePath)) {
        writer.write(releaseNotes);
        LOG.info("Release notes generated for {} and saved to: {}", milestone, releaseNotesFilePath);
      }
    } catch (IOException e) {
      System.err.println("An error occurred: " + e.getMessage());
      LOG.error("An error occurred: {}", e.getMessage(), e);
      System.exit(10);
    }
  }
}