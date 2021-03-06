/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.jamon.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.StringTokenizer;

import org.jamon.api.TemplateLocation;
import org.jamon.api.TemplateSource;

/**
 * The standard implementation of {@link TemplateSource} which retrieves templates from the
 * filesystem location under a specified root directory. By default, templates are expected to have
 * extens ".jamon"; this can be overridden.
 */
public class FileTemplateSource implements TemplateSource {
  /**
   * Construct a FileTemplateSource
   *
   * @param templateSourceDir the source directory
   */
  public FileTemplateSource(String templateSourceDir) {
    this(new File(templateSourceDir));
  }

  /**
   * Construct a FileTemplateSource, using the default extension ".jamon".
   *
   * @param templateSourceDir the source directory
   */
  public FileTemplateSource(File templateSourceDir) {
    this(templateSourceDir, "jamon");
  }

  /**
   * Construct a FileTemplateSource, specifying a filename extension for templates. If the supplied
   * extension is null or empty, no extension is expected, otherwise the extension should
   * <emph>NOT</emph> include a leading ".".
   *
   * @param templateSourceDir the source directory
   * @param extension the filename extension for templates
   */
  public FileTemplateSource(File templateSourceDir, String extension) {
    this.templateSourceDir = templateSourceDir;
    this.extension = extension == null || extension.length() == 0
        ? ""
        : "." + extension;
  }

  @Override
  public long lastModified(String templatePath) {
    return getTemplateFile(templatePath).lastModified();
  }

  @Override
  public boolean available(String templatePath) {
    return getTemplateFile(templatePath).exists();
  }

  @Override
  public InputStream getStreamFor(String templatePath) throws IOException {
    return new FileInputStream(getTemplateFile(templatePath));
  }

  @Override
  public String getExternalIdentifier(String templatePath) {
    return getTemplateFile(templatePath).getAbsolutePath();
  }

  private File getTemplateFile(String templatePath) {
    return new File(templateSourceDir, templatePathToFilePath(templatePath) + extension);
  }

  private static String templatePathToFilePath(String path) {
    if (File.separatorChar == '/') {
      return path;
    }
    StringTokenizer tokenizer = new StringTokenizer(path, "/");
    StringBuilder convertedPath = new StringBuilder(path.length());
    while (tokenizer.hasMoreTokens()) {
      convertedPath.append(tokenizer.nextToken());
      if (tokenizer.hasMoreTokens()) {
        convertedPath.append(File.separator);
      }
    }
    return convertedPath.toString();
  }

  @Override
  public TemplateLocation getTemplateLocation(String templatePath) {
    return new TemplateFileLocation(getExternalIdentifier(templatePath));
  }

  @Override
  public void loadProperties(String path, Properties properties) throws IOException {
    File propertiesFile = new File(templateSourceDir, templatePathToFilePath(path
      + "/jamon.properties"));
    if (propertiesFile.canRead()) {
      FileInputStream fileInputStream = new FileInputStream(propertiesFile);
      try {
        properties.load(fileInputStream);
      }
      finally {
        fileInputStream.close();
      }
    }
  }

  private final File templateSourceDir;
  private final String extension;
}
