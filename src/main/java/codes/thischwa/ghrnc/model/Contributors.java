package codes.thischwa.ghrnc.model;

import java.util.List;

public record Contributors(boolean enabled, String title, String message, List<String> excludes) {
}
