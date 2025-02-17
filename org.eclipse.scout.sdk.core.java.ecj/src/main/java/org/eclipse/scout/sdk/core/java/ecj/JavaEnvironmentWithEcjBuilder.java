/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.java.ecj;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.scout.sdk.core.java.ISourceFolders;
import org.eclipse.scout.sdk.core.java.ecj.JavaEnvironmentFactories.EmptyJavaEnvironmentFactory;
import org.eclipse.scout.sdk.core.java.ecj.JavaEnvironmentFactories.RunningJavaEnvironmentFactory;
import org.eclipse.scout.sdk.core.java.model.api.IJavaEnvironment;
import org.eclipse.scout.sdk.core.java.model.spi.ClasspathSpi;
import org.eclipse.scout.sdk.core.util.CoreUtils;
import org.eclipse.scout.sdk.core.util.Ensure;

/**
 * <h3>{@link JavaEnvironmentWithEcjBuilder}</h3> Used to create an {@link IJavaEnvironment} based on ECJ. This is the
 * main entry point to work with the Scout SDK Java model API using the "Eclipse Compiler for Java" (ECJ). See
 * {@link JavaEnvironmentFactories} for factories that create specific {@link IJavaEnvironment} setups.
 * <p>
 * For a sample usage see {@link org.eclipse.scout.sdk.core.model.ecj}.
 *
 * @since 5.2.0
 * @see EmptyJavaEnvironmentFactory
 * @see RunningJavaEnvironmentFactory
 */
public class JavaEnvironmentWithEcjBuilder<T extends JavaEnvironmentWithEcjBuilder<T>> {
  private final Path m_curDir = Paths.get("").toAbsolutePath().normalize();
  private final Map<String, Pattern> m_sourceExcludes = new HashMap<>();
  private final Map<String, Pattern> m_binaryExcludes = new HashMap<>();
  private final List<ClasspathEntry> m_paths = new ArrayList<>();

  private Path m_javaHome;
  private boolean m_parseMethodBodies;
  private boolean m_includeRunningClasspath = true;
  private boolean m_includeSources = true;

  /**
   * @return A new JavaEnvironmentWithEcjBuilder instance.
   */
  public static JavaEnvironmentWithEcjBuilder<?> create() {
    return new JavaEnvironmentWithEcjBuilder<>();
  }

  /**
   * Include current running classpath, default is {@code true}.
   *
   * @return this
   */
  public T withRunningClasspath(boolean b) {
    m_includeRunningClasspath = b;
    return thisInstance();
  }

  @SuppressWarnings("unchecked")
  protected T thisInstance() {
    return (T) this;
  }

  /**
   * @return if the classpath of the running JRE should be included.
   */
  public boolean isIncludeRunningClasspath() {
    return m_includeRunningClasspath;
  }

  /**
   * Exclude classpath containing Scout SDK dependencies itself
   *
   * @return this
   */
  public T withoutScoutSdk() {
    excludeIfContains("wsdl4j");
    exclude(".*" + Pattern.quote(".scout.sdk.") + ".*target/.*\\.jar");
    exclude(".*ecj-.*\\.jar");
    return exclude(".*" + Pattern.quote(".scout.sdk.") + ".*target/classes");
  }

  /**
   * @return The Java home to use. If it is {@code null}, the running Java home will be used.
   */
  public Path javaHome() {
    return m_javaHome;
  }

  /**
   * Specifies the Java home to use.
   *
   * @param javaHome
   *          The JRE (not JDK!) home to use or {@code null} if the running Java home should be used.
   * @return this
   */
  public T withJavaHome(Path javaHome) {
    m_javaHome = javaHome;
    return thisInstance();
  }

  /**
   * Exclude all classpath that match the specified regular expression.
   *
   * @param regex
   *          file path pattern with '/' as delimiter. Must not be {@code null}.
   * @return this
   */
  public T exclude(String regex) {
    var pat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    m_sourceExcludes.put(regex, pat);
    m_binaryExcludes.put(regex, pat);
    return thisInstance();
  }

  /**
   * Exclude all classpath that contain the specified {@link String}.
   * 
   * @param string
   *          All paths (having '/' as delimiter) containing this {@link String} are excluded from the classpath.
   * @return this
   */
  public T excludeIfContains(String string) {
    return exclude(".*" + Pattern.quote(string) + ".*");
  }

  /**
   * Include all classpath that match the specified regular expression.
   * <p>
   * Implementation note: By default all paths are included. Therefore, this method just moves items from the list of
   * excludes (see {@link #exclude(String)}).
   *
   * @param regex
   *          The regular expression to include.
   * @return this
   */
  public T include(String regex) {
    m_sourceExcludes.remove(regex);
    m_binaryExcludes.remove(regex);
    return thisInstance();
  }

  /**
   * Specifies if method bodies should be compiled and validated by the resulting {@link IJavaEnvironment}. Default is
   * {@code false}.
   *
   * @param parseBodies
   *          {@code true} if the bodies should be compiled and validated.
   * @return this.
   */
  public T withParseMethodBodies(boolean parseBodies) {
    m_parseMethodBodies = parseBodies;
    return thisInstance();
  }

  /**
   * @return {@code true} if the content of methods should be parsed. {@code false} if method bodies should be ignored.
   *         Ignoring method bodies is faster. The default is {@code false}.
   */
  public boolean isParseMethodBodies() {
    return m_parseMethodBodies;
  }

  /**
   * Specifies if source attachments should be searched and included in the classpath. Default is {@code true}.
   *
   * @param includeSources
   *          {@code true} if the source code for each classpath entry should be searched and included. {@code false}
   *          otherwise.
   * @return this
   */
  public T withSourcesIncluded(boolean includeSources) {
    m_includeSources = includeSources;
    return thisInstance();
  }

  /**
   * @return {@code true} if the source is included. {@code false} otherwise.
   */
  public boolean isSourceIncluded() {
    return m_includeSources;
  }

  /**
   * Exclude source classpath that match the specified regular expression.
   *
   * @param regex
   *          file path pattern with '/' as delimiter. Must not be {@code null}.
   * @return this
   */
  public T withoutSources(String regex) {
    m_sourceExcludes.put(regex, Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
    return thisInstance();
  }

  /**
   * Include the specified relative source folder.
   *
   * @param sourceFolder
   *          a path relative to the current working directory (see {@link #currentDirectory()}) pointing to a folder
   *          containing the java files. Must not be {@code null}.
   * @return this
   */
  public T withSourceFolder(String sourceFolder) {
    return withSourceFolder(sourceFolder, null);
  }

  /**
   * Include the specified relative source folder.
   *
   * @param sourceFolder
   *          a path relative to the current working directory (see {@link #currentDirectory()}) pointing to a folder
   *          containing the java files. Must not be {@code null}.
   * @param encoding
   *          The {@link Charset} to use when loading the content of the java files. May be {@code null}.
   * @return this
   */
  public T withSourceFolder(String sourceFolder, Charset encoding) {
    if (sourceFolder != null) {
      appendSourcePath(m_curDir.resolve(sourceFolder), encoding, m_paths);
    }
    return thisInstance();
  }

  /**
   * Include the specified relative binary folder.
   *
   * @param classesFolder
   *          a path relative to the current working directory (see {@link #currentDirectory()}) pointing to a folder
   *          containing the class files. Must not be {@code null}.
   * @return this
   */
  public T withClassesFolder(String classesFolder) {
    if (classesFolder != null) {
      appendBinaryPath(m_curDir.resolve(classesFolder), m_paths);
    }
    return thisInstance();
  }

  /**
   * Include the specified absolute source path.
   *
   * @param sourcePath
   *          an absolute source path. Must not be {@code null}. Can point to a directory, zip file or jar file
   *          containing the java files.
   * @return this
   */
  public T withAbsoluteSourcePath(String sourcePath) {
    return withAbsoluteSourcePath(sourcePath, null);
  }

  /**
   * Include the specified absolute source path.
   *
   * @param sourcePath
   *          an absolute source path. Must not be {@code null}. Can point to a directory, zip file or jar file
   *          containing the java files.
   * @param encoding
   *          The {@link Charset} to use when loading the content of the java files. May be {@code null}.
   * @return this
   */
  public T withAbsoluteSourcePath(String sourcePath, Charset encoding) {
    if (sourcePath != null) {
      appendSourcePath(Paths.get(sourcePath), encoding, m_paths);
    }
    return thisInstance();
  }

  /**
   * Include the specified absolute binary path.
   *
   * @param binaryPath
   *          an absolute binary path. Must not be {@code null}. Can point to a directory, zip file, jar file or jmod
   *          file containing the classes.
   * @return this
   */
  public T withAbsoluteBinaryPath(String binaryPath) {
    if (binaryPath != null) {
      appendBinaryPath(Paths.get(binaryPath), m_paths);
    }
    return thisInstance();
  }

  /**
   * @return The current working directory.
   */
  public Path currentDirectory() {
    return m_curDir;
  }

  protected void collectRunningClassPath(Collection<ClasspathEntry> collector, Collection<Path> sourceAttachmentFor) {
    JreInfo.runningUserClassPath(javaHome())
        .forEach(classpathItem -> filterAndAppendBinaryPath(classpathItem, sourceAttachmentFor, collector));
  }

  /**
   * Check exclude filters and append path to collector using {@link #appendBinaryPath(Path, Collection)}
   */
  protected void filterAndAppendBinaryPath(Path f, Collection<Path> sourceAttachmentForCollector, Collection<ClasspathEntry> collector) {
    if (isExcluded(f, m_binaryExcludes.values())) {
      return;
    }
    sourceAttachmentForCollector.add(f);
    appendBinaryPath(f, collector);
  }

  /**
   * Check exclude filters and append path to collector using {@link #appendSourcePath(Path, Charset, Collection)}
   */
  protected void filterAndAppendSourcePath(Path f, Collection<ClasspathEntry> collector) {
    if (isExcluded(f, m_sourceExcludes.values())) {
      return;
    }
    appendSourcePath(f, null, collector);
  }

  protected static boolean isExcluded(Path f, Collection<Pattern> exclusions) {
    if (f == null) {
      return true;
    }
    if (exclusions.isEmpty()) {
      return false;
    }

    CharSequence s = f.toString().replace(File.separatorChar, '/');
    return exclusions.stream().anyMatch(p -> p.matcher(s).matches());
  }

  /**
   * Append binary path to collector. Only append if the path exists.
   */
  protected static void appendBinaryPath(Path f, Collection<ClasspathEntry> collector) {
    appendPath(f, false, null, collector);
  }

  /**
   * Append source path to collector. Only append if the path exists.
   */
  protected static void appendSourcePath(Path f, Charset encoding, Collection<ClasspathEntry> collector) {
    appendPath(f, true, encoding, collector);
  }

  protected static void appendPath(Path f, boolean isSource, Charset encoding, Collection<ClasspathEntry> collector) {
    if (f == null || !Files.isReadable(f)) {
      return;
    }
    var charsetName = Optional.ofNullable(encoding).map(Charset::name).orElse(null);
    collector.add(new ClasspathEntry(f, isSource ? ClasspathSpi.MODE_SOURCE : ClasspathSpi.MODE_BINARY, charsetName));
  }

  @SuppressWarnings("findbugs:NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  protected void appendSourceAttachments(Iterable<Path> sourceAttachmentsFor, Collection<ClasspathEntry> collector) {
    if (!isSourceIncluded()) {
      return;
    }

    for (var path : sourceAttachmentsFor) {
      if (path.endsWith("target/classes")) {
        filterAndAppendSourcePath(path.getParent().getParent().resolve(ISourceFolders.MAIN_JAVA_SOURCE_FOLDER), collector);
        filterAndAppendSourcePath(path.getParent().getParent().resolve(ISourceFolders.GENERATED_ANNOTATIONS_SOURCE_FOLDER), collector);
        filterAndAppendSourcePath(path.getParent().getParent().resolve(ISourceFolders.GENERATED_WS_IMPORT_SOURCE_FOLDER), collector);
      }
      else if (path.endsWith("target/test-classes")) {
        filterAndAppendSourcePath(path.getParent().getParent().resolve(ISourceFolders.TEST_JAVA_SOURCE_FOLDER), collector);
      }
      else {
        var extension = CoreUtils.extensionOf(path);
        if ("jar".equals(extension) || "zip".equals(extension)) {
          var fileName = path.getFileName().toString(); // no lower case here! Otherwise, case-sensitive filesystem may not find the file anymore!
          fileName = fileName.substring(0, fileName.length() - 4) + "-sources" + fileName.substring(fileName.length() - 4);
          filterAndAppendSourcePath(path.getParent().resolve(fileName), collector);
        }
      }
    }
  }

  protected static Collection<ClasspathEntry> sort(Collection<ClasspathEntry> allEntries) {
    var numBuckets = 4;
    Map<Integer, List<ClasspathEntry>> buckets = new HashMap<>(numBuckets);
    for (var entry : allEntries) {
      buckets.computeIfAbsent(bucketOf(entry), ArrayList::new).add(entry);
    }
    Collection<ClasspathEntry> grouped = new ArrayList<>(allEntries.size());
    for (var i = 0; i < numBuckets; i++) {
      addBucket(i, grouped, buckets);
    }
    return grouped;
  }

  protected static void addBucket(int index, Collection<ClasspathEntry> grouped, Map<Integer, List<ClasspathEntry>> buckets) {
    var bucketContent = buckets.get(index);
    if (bucketContent == null) {
      return;
    }
    grouped.addAll(bucketContent);
  }

  protected static Integer bucketOf(ClasspathEntry entry) {
    var result = 0;
    if (!Files.isDirectory(entry.path())) {
      result++;
    }
    if (entry.mode() == ClasspathSpi.MODE_BINARY) {
      result += 2;
    }
    return result;
  }

  protected JavaEnvironmentWithEcj build() {
    Collection<ClasspathEntry> allEntries = new ArrayList<>(m_paths);
    if (isIncludeRunningClasspath()) {
      Collection<Path> sourceAttachmentFor = new LinkedHashSet<>();
      collectRunningClassPath(allEntries, sourceAttachmentFor); // current classpath
      appendSourceAttachments(sourceAttachmentFor, allEntries); // find source attachments for the running classpath entries
    }

    var opts = EcjAstCompiler.createDefaultOptions();
    opts.ignoreMethodBodies = !isParseMethodBodies();
    return build(javaHome(), sort(allEntries), opts);
  }

  @SuppressWarnings("MethodMayBeStatic")
  protected JavaEnvironmentWithEcj build(Path javaHome, Collection<? extends ClasspathEntry> classpath, CompilerOptions options) {
    return new JavaEnvironmentWithEcj(javaHome, classpath, options);
  }

  /**
   * Calls the specified {@link Function} passing a {@link IJavaEnvironment} using a classpath as specified by this
   * builder.
   *
   * @param task
   *          The {@link Function} to call. Must not be {@code null}.
   * @return The return value of the specified {@link Function}.
   */
  public <R> R call(Function<IJavaEnvironment, R> task) {
    try (var env = build()) {
      return Ensure.notNull(task).apply(env.wrap());
    }
  }

  /**
   * Executes the specified {@link Consumer} passing a {@link IJavaEnvironment} using a classpath as specified by this
   * builder.
   *
   * @param task
   *          The {@link Consumer} to execute. Must not be {@code null}.
   */
  public void accept(Consumer<IJavaEnvironment> task) {
    call(env -> {
      Ensure.notNull(task).accept(env);
      return null;
    });
  }
}
