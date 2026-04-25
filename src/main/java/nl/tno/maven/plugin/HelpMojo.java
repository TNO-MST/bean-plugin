package nl.tno.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author bergtwvd
 */  

@Mojo(name = "help", requiresProject = false)
public class HelpMojo extends AbstractMojo {

    @Parameter(property = "detail", defaultValue = "false")
    private boolean detail;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Bean Plugin Help");
        getLog().info("----------------");

        getLog().info("Goals:");
        getLog().info("  generate    - generate Java Beans from HLA FOM modules");

        if (detail) {
            getLog().info("\nDetailed usage:");
            getLog().info("  -DinputDir=STRING                  - name of input directory with FOM modules");
            getLog().info("  -DoutputDir=STRING                 - name of output directory for Java Beans");
            getLog().info("  -DpackageNames=MAP<STRING,STRING>  - name mapping for NETN package name pattern");
            getLog().info("  -Dverbose=BOOLEAN                  - if true, additional logging output");
        }
    }
}

