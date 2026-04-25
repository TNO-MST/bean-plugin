package nl.tno.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tno.beangenerator.BeanGenerator;
import nl.tno.beangenerator.BeanGeneratorProperties;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

/**
 * @author bergtwvd
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MavenBeanPlugin extends AbstractMojo {

  @Parameter(
      property = "inputDir",
      required = true,
      defaultValue = "${project.basedir}/src/main/resources/foms")
  private File inputDir;

  @Parameter(
      property = "outputDir",
      required = true,
      defaultValue = "${project.build.directory}/generated-sources/beans")
  private File outputDir;

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Parameter(property = "groupId", required = true, defaultValue = "nl.tno")
  private String groupId;

  @Parameter(property = "packageNames")
  private Map<String, String> packageNames;

  @Parameter(property = "verbose", defaultValue = "false")
  private boolean verbose;

  @Override
  public void execute() throws MojoExecutionException {
    try {
      getLog().info("Generating sources from: " + inputDir);

      if (!outputDir.exists()) {
        outputDir.mkdirs();
      }

      // SCHEMA processing logic

      BeanGeneratorProperties properties = new BeanGeneratorProperties();
      
      // Set the package name mapping
      if (!this.packageNames.isEmpty()) {
        // reverse key and value
        Map<String, String> map = new HashMap<>();
        for (var a : packageNames.entrySet()) {
          map.put(a.getValue(), a.getKey());
        }
        properties.setDefaultPackageNames(map);
      }

      // Ser the Java group ID
      properties.setGroupId(groupId);

      // Create the Bean Generator for generating the source code
      BeanGenerator bg =
          new BeanGenerator(properties) {
            @Override
            protected void beforeOutput() throws Exception {}

            @Override
            protected void outputClass(
                String fqOmtName, String packageName, String className, StringBuilder sourceCode)
                throws Exception {
              this.createFile1(
                  outputDir.getAbsolutePath()
                      + DIR_SEPARATOR
                      + packageName.replace(PKG_SEPARATOR, DIR_SEPARATOR)
                      + DIR_SEPARATOR
                      + className
                      + JAVA_FILE_SUFFIX,
                  sourceCode);
            }

            @Override
            protected void outputPackage(
                String packageName, String infoName, StringBuilder sourceCode) throws Exception {
              this.createFile1(
                  outputDir.getAbsolutePath()
                      + DIR_SEPARATOR
                      + packageName.replace(PKG_SEPARATOR, DIR_SEPARATOR)
                      + DIR_SEPARATOR
                      + infoName
                      + JAVA_FILE_SUFFIX,
                  sourceCode);
            }

            protected void createFile1(String fileName, StringBuilder sb) throws IOException {
              if (verbose) getLog().info("Creating file: " + fileName);
              String sourceCode = sb.toString();
              File outputFile = new File(fileName);
              outputFile.getParentFile().mkdirs();
              outputFile.createNewFile();
              try (PrintWriter pw = new PrintWriter(outputFile)) {
                pw.append(sourceCode);
              }
            }
          };

      URL[] modules = getFiles(inputDir);

      // Generate the Java Beans
      bg.expand(modules, null, null);

      // Add generated sources to compilation
      project.addCompileSourceRoot(outputDir.getAbsolutePath());

      getLog().info("Generated sources for:");
      for (URL mod : modules) getLog().info("Module: " + mod.getPath());
    } catch (Exception e) {
      throw new MojoExecutionException("Error generating sources", e);
    }
  }

  private URL[] getFiles(File dir) throws IOException {
    List<URL> list = new ArrayList<>();
    Path dirPath = Paths.get(dir.toURI());

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
      for (Path entry : stream) {
        if (Files.isRegularFile(entry)) {
          list.add(entry.toUri().toURL());
        }
      }
    }

    return list.toArray(URL[]::new);
  }
}
