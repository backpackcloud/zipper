package com.backpackcloud.cli.commands;

import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.cli.ui.components.FileSuggester;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileSuggesterTest {

  FileSuggester suggester = new FileSuggester();

  @Test
  public void testDefaultSuggestions() {
    List<Suggestion> suggestions = suggester.suggest("");
    assertFalse(suggestions.isEmpty());
    assertContains(suggestions, "pom.xml", "LICENSE", ".gitignore", "src");
  }

  @Test
  public void testRelativePathSuggestions() {
    List<Suggestion> suggestions = suggester.suggest("../");
    assertFalse(suggestions.isEmpty());
    assertContains(suggestions, "../zipper");
  }

  @Test
  public void testNestedPathSuggestions() {
    List<Suggestion> suggestions = suggester.suggest("src");
    assertFalse(suggestions.isEmpty());
    assertContains(suggestions, "src/main", "src/test");
  }

  @Test
  public void testAbsolutePathSuggestions() {
    List<Suggestion> suggestions = suggester.suggest("/");
    assertFalse(suggestions.isEmpty());
  }

  @Test
  public void testIncompletePathSuggestions() {
    List<Suggestion> suggestions = suggester.suggest("src/main/j");
    assertFalse(suggestions.isEmpty());
    assertContains(suggestions, "src/main/java", "src/main/resources");
  }

  @Test
  public void testCompleteDirectoryPathSuggestions() {
    List<Suggestion> suggestions = suggester.suggest("src/main/java");
    assertFalse(suggestions.isEmpty());
    assertContains(suggestions, "src/main/java", "src/main/java/com");
  }

  @Test
  public void testCompleteFilePathSuggestions() {
    List<Suggestion> suggestions = suggester.suggest("pom.xml");
    assertFalse(suggestions.isEmpty());
    assertContains(suggestions, "pom.xml", "LICENSE", ".gitignore", "src");
  }

  @Test
  public void testCompleteNestedFilePathSuggestions() {
    List<Suggestion> suggestions = suggester.suggest("src/main/java/com/backpackcloud/cli/Writer.java");
    assertFalse(suggestions.isEmpty());
    assertContains(
      suggestions,
      "src/main/java/com/backpackcloud/cli/Writer.java",
      "src/main/java/com/backpackcloud/cli/Macro.java",
      "src/main/java/com/backpackcloud/cli/Displayable.java"
    );
  }

  private void assertContains(List<Suggestion> suggestions, String... values) {
    for (String value : values) {
      Optional<Suggestion> result = suggestions.stream()
        .filter(suggestion -> value.equals(suggestion.value()))
        .findAny();
      assertTrue(result.isPresent(), "Suggestions should contain '" + value + "'");
    }
  }

}
