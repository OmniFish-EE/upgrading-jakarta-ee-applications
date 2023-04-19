# Example applications to transform Jakarta EE 8 app to Jakarta EE 10

In Jakarta EE 9, because of legal reasons, packages of all APIs were renamed to have `jakarta.` prefix instead of `javax.` prefix. Applications that depend on Jakarta EE 8 or Java EE, nead to be updated to run on runtimes that support Jakarta EE 9 or newer version. Developers may still face challenges related to this breaking change even if their application already uses Jakarta EE 9 or newer, if they want to use a library that still doesn't support this change.

Applications in this repository provide examples of how to deal with the challenges related to renaming of Jakarta EE API packages, as well as with some other challenges of migrating to Jakarta EE 10.

* [javax-jakarta-transform-whole-war](javax-jakarta-transform-whole-war) - transform the final WAR artifact to be compatible with Jakarta EE 9+
* [javax-jakarta-transform-individual-deps](javax-jakarta-transform-individual-deps) - an example how to transform dependencies during the build of your application


## Renaming package prefixes from `javax.` to `jakarta.` (upgrading from Jakarta EE 8 to 9 or newer)

Challenges:

* Application source code depends on Java EE APIs (with `javax.` prefix)
* Application resources (XML descriptors, service locator files) reference Java EE API elements (with `javax.` prefix)
* Application dependencies depend on Java EE APIs (with `javax.` prefix)

## Upgrading to Jakarta EE 10

Challenges:

Some obsolete APIs were dropped in favor of newer APIs that already exist in Jakarta EE 8 or older. It's pretty straightforward to migrate from the dropped APIs to the alternative ones though.

When migrating from Jakarta EE 8:

* replace annotation `javax.faces.bean.ApplicationScoped` with `javax.enterprise.context.ApplicationScoped`
* replace annotation `javax.faces.bean.ViewScoped` with `javax.faces.view.ViewScoped` and implement `java.io.Serializable` in the related class
* replace annotation `javax.faces.bean.SessionScoped` with `javax.enterprise.context.SessionScoped` and implement `java.io.Serializable` in the related class
* replace annotation `javax.faces.bean.NoneScoped` with `javax.enterprise.context.Dependent`
* replace annotation `javax.faces.bean.RequestScoped` with `javax.enterprise.context.RequestScoped`
* replace annotation `javax.faces.bean.ManagedBean` on a class with `javax.inject.Name`. if there's no scope annotation, add `javax.enterprise.context.RequestScoped`. If `@ManagedBean(eager=true)`, use the `@javax.enterprise.context.ApplicationScoped` scope and add an observer method like `public void init(@Observes @Initialized(ApplicationScoped.class) Object event) {}` to initialize the bean at applicaiton startup eagerly

Then continue the migration from Jakarta EE 8 to Jakarta EE 10.

When migrating from Jakarta EE 9, do the same changes but with the `jakarta.` prefix in package names instead of `javax.` prefixes and the result should be compatible with Jakarta EE 10.

## Techniques to address the challenges related to renaming package prefixes

### Deploy an existing Java EE application (WAR, EAR, etc.)

You may be in a situation that you have a packaged application that runs on a Java EE runtime and oyu want to deploy it on a Jakarta EE 9+ runtime. Or you want to quickly migrate your project to build it as usual but deploy on a Jakarta EE 9+ runtime.

The simplest way to do that is to use the [Eclipse Transformer](https://github.com/eclipse/transformer#readme) tool and transform a WAR, EAR, ... from using Java EE `javax.` API packages to using Jakarta EE 9+ `jakarta.` API packages. Eclipse Transformer scans the application and transforms Java bytecode and resources in the application to refer to Jakarta EE 9+ packages. All nested JAR files are transformed as well. The transformed package will depend on Jakarta EE 9 APIs as if it was originally built for Jakarta EE 9. Such an application can also be deployed with a Jakarta EE 10 runtime like Eclipse GlassFish 7 as long as it doesn't use any obsolete functionality that was dropped in Jakarta EE 10.

The sample application [javax-jakarta-transform-whole-war](javax-jakarta-transform-whole-war) provides an example of how to configure the Eclipse Transformer with a maven project to transform the final WAR artifact as well as to transform the exploded WAR directory. With this configuration:

* You can deploy the final WAR file to a Jakarta EE 9+ runtime with usual mechanisms
* IDEs that deploy an exploded directory, will also be able to deploy to a Jakarta EE 9+ runtime
* The WAR artifact deployed to a Maven repository is compatible with Jakarta EE 9+. You don't need to modify it again to deploy it to a Jakarta EE 9+ runtime, if you want to deploy the artifact downloaded from the Maven repository later.

### Convert the source code of your Java EE application

If you want to migrate an existing project to use Jakarta EE 9+, there are several ways to do so:

* Manual search and replace `javax.` references to `jakarta.`. 
* Automated conversion using an IDE
* Automated conversion using a command line tool like OpenRewrite or Eclipse transformer

#### Manual search and replace

You can just serch `javax.` and replace with `jakarta.` Any decent text editor or Java IDE would help you.

Remeber that some packages that start with `javax.` shouldn't be renamed. They are not part of Jakarta EE and should be used with the `javax.` prefix. They are either included in the Java SDK (e.g. `javax.sql.DataSource`) or in a specification outside of Java EE and Jakarta EE (e.g. `javax.cache` in JCache)

#### Automated conversion using an IDE

At this point, IntelliJ Idea provides a [migration tool](https://www.jetbrains.com/idea/guide/tutorials/migrating-javax-jakarta/use-migration-tool/) to help you convert your projects to Jakarta EE 9+ APIs.

### Convert the dependencies of your Jakarta EE application

If you want to update your source code for Jakarta EE 9+, you also need to address the dependencies that depend on Java EE APIs. You have 3 basic options:

1. For dependencies that already support Jakarta EE 9+, upgrade the dependency
2. Dependencies that don't support Jakarta EE 9+ are transformed during build
3. Dependencies that don't support Jakarta EE 9+ are transformed before the build (e.g. in a separate project or maven module) and your application depends directly on the transformed version

#### Option 1: Upgrade dependencies to a version compatible with Jakarta EE 9+

Some libraries provide a version that is compatible with Jakarta EE 9+. They often provide both a usual maven artefact that is compatible with Java EE and another maven artefact with the jakarta classifier. For example:

* Primefaces 12 Jakarta Maven dependency:

```
<dependency>
  <groupId>org.primefaces</groupId>
  <artifactId>primefaces</artifactId>
  <version>12.0.0</version>
  <classifier>jakarta</classifier>
</dependency>
```

#### Option 2: Transform dependencies during the build of your application

The sample application [javax-jakarta-transform-individual-deps](javax-jakarta-transform-individual-deps) provides an example how to use the option 2 in a Maven project. It transforms Java EE dependencies during application build and adds these dependencies to the final WAR artifact and exploded WAR directory. Note that this example project uses the Eclipse Transformer to transform individual JAR files. However, each if these dependencies may pull in some more transitive dependencies, which also miht depend on Java EE APIs. Therefore the sample application also runs the Eclipse Transformer on the final WAR artifact and exploded directory, so that it's ensured that all the JAR files in the WAR application use Jakarta EE 9+.

#### Option 3: Transform dependencies during the build of your application

If Option 2 isn't enough and you need to have a Jakarta EE 9+ version of your dependency in your project, then you can use the Option 3.

1. Create a new project (Maven module)
2. This new project should depend on this dependency
3. Your application should depend on the artifact from this new project instead of on the original Java EE artifact



