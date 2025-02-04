package io.reactiverse.es4x.cli;

import io.reactiverse.es4x.commands.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.reactiverse.es4x.cli.Helper.*;

public class PM {

  private static void printUsage() {
    System.err.println("Usage: es4x [COMMAND] [OPTIONS] [arg...]");
    System.err.println();
    System.err.println("Commands:");
    System.err.println(pad(Project.NAME + "/app", 16) + Project.SUMMARY);
    System.err.println(pad(Install.NAME, 16) + Install.SUMMARY);
    System.err.println(pad(SecurityPolicy.NAME, 16) + SecurityPolicy.SUMMARY);
    System.err.println(pad(Versions.NAME, 16) + Versions.SUMMARY);
    System.err.println();
    System.err.println("Current VM:");
    System.out.println("Name:   " + System.getProperty("java.vm.name") + " - " + System.getProperty("java.version"));
    System.out.println("Vendor: " + System.getProperty("java.vendor.version", "-"));
    System.err.println();
    System.err.println("Run 'es4x COMMAND --help' for more information on a command.");
  }

  private static void verifyRuntime(boolean fatal) {
    final GraalVMVersion vmVersion = new GraalVMVersion();
    if (vmVersion.isGraalVM()) {
      // graalvm version should be aligned with the dependencies
      // used on the application, otherwise it introduces some
      // unwanted side effects
      try (InputStream is = PM.class.getClassLoader().getResourceAsStream("META-INF/es4x-commands/VERSIONS.properties")) {
        if (is != null) {
          final Properties versions = new Properties();
          versions.load(is);
          String wanted = versions.getProperty("graalvm");
          if (!vmVersion.isGreaterOrEqual(wanted)) {
            if (fatal) {
              fatal(String.format("Runtime GraalVM version mismatch { wanted: [%s], provided: [%s] }%sFor installation help see: https://www.graalvm.org/docs/getting-started-with-graalvm/", wanted, vmVersion.toString(), System.lineSeparator()));
            } else {
              warn(String.format("Runtime GraalVM version mismatch { wanted: [%s], provided: [%s] }%sFor installation help see: https://www.graalvm.org/docs/getting-started-with-graalvm/", wanted, vmVersion.toString(), System.lineSeparator()));
            }
          }
        }
      } catch (IOException e) {
        fatal(e.getMessage());
      }
    }
  }

  public static void main(String[] args) {
    if (args == null || args.length == 0) {
      // default action is help
      args = new String[] { "--help" };
    }

    String command = args[0];
    // strip the command out of the arguments
    String[] cmdArgs = new String[args.length - 1];
    System.arraycopy(args, 1, cmdArgs, 0, cmdArgs.length);

    switch (command) {
      case Project.NAME:
        verifyRuntime(true);
        new Project(cmdArgs).run();
        System.exit(0);
        return;
      case Install.NAME:
        verifyRuntime(true);
        new Install(cmdArgs).run();
        System.exit(0);
        return;
      case SecurityPolicy.NAME:
        verifyRuntime(true);
        new SecurityPolicy(cmdArgs).run();
        System.exit(0);
        return;
      case Versions.NAME:
        verifyRuntime(true);
        new Versions(cmdArgs).run();
        System.exit(0);
        return;
      case "-h":
      case "--help":
        verifyRuntime(false);
        printUsage();
        System.exit(0);
        return;
      default:
        verifyRuntime(false);
        printUsage();
        System.exit(2);
    }
  }
}
