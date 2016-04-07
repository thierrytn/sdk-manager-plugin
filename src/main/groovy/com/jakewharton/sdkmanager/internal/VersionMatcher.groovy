package com.jakewharton.sdkmanager.internal

import org.gradle.api.tasks.StopExecutionException

import java.util.regex.Pattern

class VersionMatcher implements Comparable<VersionMatcher> {
  private final int major
  private final int minor
  private final int micro
  private final int preview

  private static final Pattern VERSION_PATTERN =
      Pattern.compile("\\s*([0-9]+)(?:\\.([0-9]+)(?:\\.([0-9]+))?)?\\s*(?:rc([0-9]+))?\\s*")

  public VersionMatcher(int major, int minor, int micro, int preview) {
    this.major = major
    this.minor = minor
    this.micro = micro
    this.preview = preview
  }

  public static VersionMatcher parse(String version) {
    def m = VERSION_PATTERN.matcher(version)
    if (m == null || !m.matches()) {
      throw new StopExecutionException('Version string mismatched')
    }

    int major = Integer.parseInt(m.group(1))
    String s = m.group(2)
    int minor = s == null ? 0 : Integer.parseInt(s)
    s = m.group(3)
    int micro = s == null ? 0 : Integer.parseInt(s)
    s = m.group(4)
    // Something that isn't a release candidate is newer than something that is
    // e.g. "18.0.0 rc1" < "18.0.0"
    int preview = s == null ? Integer.MAX_VALUE : Integer.parseInt(s)

    return new VersionMatcher(major, minor, micro, preview)
  }

  @Override
  public int compareTo(VersionMatcher rhs) {
    int delta = major - rhs.major
    if (delta != 0) {
      return delta
    }

    delta = minor - rhs.minor
    if (delta != 0) {
      return delta
    }

    delta = micro - rhs.micro;
    if (delta != 0) {
      return delta
    }

    delta = preview - rhs.preview;
    return delta
  }
}