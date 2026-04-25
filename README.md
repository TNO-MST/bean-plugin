![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/TNO-MST/bean-plugin/build-publish-jar.yaml?branch=main&label=build&event=push)
[![Maven Central Version](https://img.shields.io/maven-central/v/nl.tno/bean-plugin)](https://central.sonatype.com/artifact/nl.tno/bean-plugin)
[![javadoc](https://javadoc.io/badge2/nl.tno/bean-plugin/javadoc.svg)](https://javadoc.io/doc/nl.tno/bean-plugin) 

# Maven Java Bean plugin

This project provides functionality to generate Java Beans from HLA FOM modules as part of the Maven build process.

For example, add the following plugin to your project's POM:

````
<build>
	<plugins>
		<plugin>
			<groupId>nl.tno</groupId>
			<artifactId>bean-plugin</artifactId>
			<version>1.0</version>
			<executions>
				<execution>
					<phase>generate-sources</phase>
					<goals>
						<goal>generate</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
````

The default configuration of the plugin is sufficient for most use cases:
- The default file location for the (input) HLA FOM modules is: `${project.basedir}/src/main/resources/foms`
- The default file location for the (output) Java Beans is: `${project.build.directory}/generated-sources/beans`

The generated Java Beans are organised in three subpackages per main package: `datatypes`, `objects`, and `interactions`. Each main package corresponds with a FOM module.

## Plugin configuration

| Parameter  | Type | Description | Default if not provided |
| ------------- | ------------- | --------- | -------- |
| `inputDir`  | STRING | Input directory with the FOM modules | `${project.basedir}/src/main/resources/fom` |
| `outputDir` | STRING | Output directory for the Java Beans | `${project.build.directory}/generated-sources/beans` |
| `groupId`   | STRING | Java Group Identifier | `nl.tno` |
| `packageNames` | MAP<STRING,STRING> | Map of key-value pairs, where key is packange name and value is a regex for the FOM module | Default MIM, RPR, and NETN mappings |
| `verbose` | BOOLEAN | Additional logging output | `false` |

## FOM file name to Java package mappings

FOM file names are mapped to Java package names. A default mapping for MIM, RPR, and NETN FOM modules is used if no mapping configuration is provided.

An example of a mapping configuration is shown below.

````
<build>
	<plugins>
		<plugin>
			<groupId>nl.tno</groupId>
			<artifactId>bean-plugin</artifactId>
			<version>1.0</version>
			<executions>
				...
			</executions>
			<configuration>
				<packageNames>
					<rpr>.*rpr.?fom.*</rpr>
					<mim>.*hlastandardmim.*</mim>
				</packageNames>
			</configuration>
		</plugin>
	</plugins>
</build>
````
This mapping specifies that a file name that matches with the regex:
- `.*rpr.?fom.*` is mapped to the `rpr` package name and
- `.*hlastandardmim.*` is mapped to the `mim` package name.
