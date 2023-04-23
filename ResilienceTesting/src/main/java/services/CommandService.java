package services;

import domain.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class CommandService {
    private static final String URL = Globals.URL;

    // General Use

    private static void killRunningApplication(String PID) throws IOException {
        ProcessBuilder taskKillBuilder = new ProcessBuilder(
                "cmd.exe", "/c", "taskkill /F /PID " + PID);
        taskKillBuilder.redirectErrorStream(true);
        Process p1 = taskKillBuilder.start();
        BufferedReader r1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
        String l1;
        do {
            l1 = r1.readLine();
        } while (l1 != null);
    }


    // Test Discovering Fase

    public static void buildService(Service service) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd " + URL + "/" + service.getName() + " && mvn clean install" );
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        do {
            line = r.readLine();
        } while (line != null);
    }

    public static TestResult runTest(String execTestCommand) throws IOException {
        var returnedTime = "";
        var buildSucces = false;

        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", execTestCommand);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
//            System.out.println(line);
            if (line == null) {
                break;
            }

            if (line.contains("Tests run: 1, Failures: 0, Errors: 0, Skipped: 0")) {
                buildSucces = true;
            }

            if (line.contains("Total time:")) {
                var trimmed = line.substring(18).trim();
                returnedTime = trimmed.split(" ")[0];
            }
        }

        return new TestResult(returnedTime, buildSucces);
    }

    public static void runTestCases(Service service) throws IOException {
       buildService(service);
       if (service.getTestClasses().size() != 0) {
           System.out.println("Executing tests..");
           for (TestClass testClass : service.getTestClasses()) {
               System.out.println("Test Class: " + testClass.getName());
               for (TestCase testCase : testClass.getTestCases()) {
                   String command = "cd " + URL + "/" + service.getName() + " && mvn -Dtest=" + testClass.getName() + "#" + testCase.getName() + " test";
                   System.out.println(" - Test: " + testCase.getName());
                   var testResult = runTest(command);
                   System.out.println("\t Test result: " + testResult.showOutcome());
                   testCase.setExecTimeInSeconds(testResult.getTime());
                   testCase.setSuccess(testResult.getSuccess());
               }
           }
       }
    }

    public static void runAllTests(List<Service> services) throws IOException {
        for (Service service : services) {
            runTestCases(service);
        }
    }


    // Aspect Injection

    public static void injectAspectIntoService(Service service, String aspectSourcePath) throws IOException {
        System.out.println(service.getName().toUpperCase() + ": injecting aspects to discover message channels...");
        File source = Paths.get(aspectSourcePath).toFile();
        File destination = Paths.get(service.getPath() + "\\src\\main\\java\\io\\pivotal\\" + service.getName()).toFile();

        copyDirectory(source, destination);
    }

    private static void removeAspectsFromService(Service service) {
        System.out.println(service.getName().toUpperCase() + ": Removing aspects...\n");
        File destination = Paths.get(service.getPath() + "\\src\\main\\java\\io\\pivotal\\" + service.getName() + "\\aspects").toFile(); // TODO: change to basepath of the service
        deleteDirectory(destination);
    }

    public static void captureMessages(List<Service> services) throws IOException {
        for (Service service : services) {
            Thread serviceThread = new Thread(() -> {
                try {
                    injectAspectIntoService(service, "src/main/resources/captureMessagesAspects");
                    startServiceForMessageCapturing(service);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            serviceThread.start();
        }
    }

    public static void injectCaptureMessageAspectsAndRunTestsForAllServices(List<Service> services) throws IOException {
        for (Service service : services) {
            injectCaptureMessageAspectsAndRunTests(service);
        }
    }

    public static void injectCaptureMessageAspectsAndRunTests(Service service) throws IOException {
            injectAspectIntoService(service, "src/main/resources/captureMessagesAspects-" + service.getName());
            runTestCases(service);
            removeAspectsFromService(service);
    }

    private static void startServiceForMessageCapturing(Service service) throws IOException, InterruptedException {
        System.out.println("Starting " + service.getName() + "...");
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd " + service.getPath() + " && " + "mvn spring-boot:run");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;

        while (true) {
            line = r.readLine();
            //System.out.println(line);
            if (line == null) {
                break;
            }

            if (line.contains("with PID")) {
                var splitted = line.split("PID");
                var PID = splitted[1].split(" ")[1];
                Thread stopExecutionThread = new Thread(() -> {
                    try {
                        TimeUnit.MINUTES.sleep(1);
                        killRunningApplication(PID);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                });

            }

            if (line.contains("Resilience Testing Capturing")) {
                System.out.println(service.getName() + ": " + line);
            }
        }
    }

    public static void discoverChannels(Service service) throws IOException {
        injectAspectIntoService(service, "src/main/resources/discoverChannelsAspects");

        System.out.println(service.getName().toUpperCase() + ": discovering channels...");
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd " + URL + "/" + service.getName() + " && " + "mvn spring-boot:run -e");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String PID = "";
        String line;
        while (true) {
            line = r.readLine();
            System.out.println(line);
            if (line == null) {
                break;
            }

            if (line.contains("with PID")) {
                var splitted = line.split("PID");
                PID = splitted[1].split(" ")[1];
            }

            if (line.contains("Resilience Testing Discovery")) {
                System.out.println("Found a channel!");
                if (line.contains("output")) {
                    var currentMessageChannels = service.getMessageChannels();
                    currentMessageChannels.add(new MessageChannel("output", line.split(" - ")[3], line.split(" - ")[2]));
                    service.setMessageChannels(currentMessageChannels);
                }

                if (line.contains("input")) {
                    var currentMessageChannels = service.getMessageChannels();
                    currentMessageChannels.add(new MessageChannel("input", line.split(" - ")[3], line.split(" - ")[2]));
                    service.setMessageChannels(currentMessageChannels);
                }
            }

            if (line.contains("has started...")) {
                killRunningApplication(PID);
            }
        }

        removeAspectsFromService(service);
    }

    public static void discoverStreamListeners(Service service) throws IOException {
      //  injectAspectIntoService(service, "src/main/resources/discoverStreamListenersAspects");

        System.out.println(service.getName().toUpperCase() + ": discovering StreamListeners...");
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd " + URL + "\\" + service.getName() + " && " + "mvn spring-boot:run");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String PID = "";
        String line;
        while (true) {
            line = r.readLine();
            System.out.println(line);
            if (line == null) {
                break;
            }

            if (line.contains("with PID")) {
                var splitted = line.split("PID");
                PID = splitted[1].split(" ")[1];
            }

            if (line.contains("Resilience Testing Discovery")) {
                System.out.println("Found a StreamListener!");
                var listeners = service.getListeners();
                listeners.add(new Listener(line.split(" - ")[3], line.split(" - ")[2]));
                service.setListeners(listeners);
            }

            if (line.contains("has started...")) {
                killRunningApplication(PID);
            }
        }
    }

    public static void discoverStreamListenersForAllServices(List<Service> services) throws IOException {
        for (Service service : services) {
            discoverStreamListeners(service);
        }
    }

    public static void discoverChannelsForAllServices(List<Service> services) throws IOException {
        for (Service service : services) {
            discoverChannels(service);
        }
    }

    public static void discoverStreamListenersTest(List<Service> services) throws IOException {
        for (Service service : services) {
                new Thread(() -> {
                    try {
                        discoverStreamListeners(service);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
        }
    }


    // Aspect Generation

    public static void generateMessageCapturingAspects(List<Service> services) throws IOException {
        for (Service service : services) {
            generateAllMessageCapturingAspectsForService(service);
            //generateSomeMessageCaptureAspectsForService(service);
            generateAspectConfig(service);
        }
    }

    public static void generateAllMessageCapturingAspectsForService(Service service) throws IOException {
        String path = "./src/main/resources/captureMessagesAspects-" + service.getName() + "/aspects";

        Path directory =  Paths.get(path);
        Files.createDirectories(directory);

        File captureMessageAspects = new File(path + "/CaptureAllMessagesAspect.java");
        writeTemplateCodeForAspect(captureMessageAspects);
        List<Pointcut> pointcuts = generatePointcutsForService(service, captureMessageAspects);
        for (Pointcut pointcut : pointcuts) {
            generateCaptureAllAspectForPointcut(captureMessageAspects, pointcut);
        }
        writeTemplateCodeForEndOfFile(captureMessageAspects);

    }

    public static void generateSomeMessageCaptureAspectsForService(Service service) throws IOException {
        String path = "./src/main/resources/captureMessagesAspects-" + service.getName() + "/aspects";
        Path directory =  Paths.get(path);
        Files.createDirectories(directory);

        File captureMessageAspects = new File(path + "/CaptureSomeMessagesAspect.java");
        writeTemplateCodeForAspect(captureMessageAspects);
        List<Pointcut> pointcuts = generatePointcutsForService(service, captureMessageAspects);
        for (Pointcut pointcut : pointcuts) {
            generateCaptureSomeAspectForPointcut(captureMessageAspects, pointcut);
        }
        writeTemplateCodeForEndOfFile(captureMessageAspects);
    }

    public static void generateAspectConfig(Service service) throws IOException {
        String path = "./src/main/resources/captureMessagesAspects-" + service.getName() + "/aspects";

        File file = new File(path + "/AspectConfig.java");
        Files.writeString(Path.of(file.getPath()), "package io.pivotal.loancheck.aspects;" + System.lineSeparator() + System.lineSeparator(), CREATE); // TODO change to service base path
        Files.writeString(Path.of(file.getPath()), "import org.springframework.context.annotation.ComponentScan;" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "import org.springframework.context.annotation.Configuration;" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "import org.springframework.context.annotation.EnableAspectJAutoProxy;" + System.lineSeparator() + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "@Configuration" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "@ComponentScan(basePackages= \"io.pivotal\")" + System.lineSeparator(), APPEND);  // TODO change to service base path
        Files.writeString(Path.of(file.getPath()), "@EnableAspectJAutoProxy" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "public class AspectConfig {" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "}", APPEND);
    }

    public static void generateCaptureAllAspectForPointcut(File file, Pointcut pointcut) throws IOException {
        Files.writeString(Path.of(file.getPath()), "\t @Around(value = \"" + pointcut.getName() + "()\")" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t public void catchMessageFor" + pointcut.getName().substring(0, pointcut.getName().length() - 1) + "(ProceedingJoinPoint pjp) throws Throwable {" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t \t System.out.println(\"ABORTING ALL\");" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t }" + System.lineSeparator() + System.lineSeparator(), APPEND);
    }

    public static void generateCaptureSomeAspectForPointcut(File file, Pointcut pointcut) throws IOException {
        Files.writeString(Path.of(file.getPath()), "\t @Around(value = \"" + pointcut.getName() + "()\")" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t public void catchMessageFor" + pointcut.getName().substring(0, pointcut.getName().length() - 1) + "(ProceedingJoinPoint pjp) throws Throwable {" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t \t counter++;" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t \t if (counter % 2 == 0) {" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t \t \t pjp.proceed();" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t \t \t System.out.println(\"PROCEED\");" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t \t } else {" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t \t \t System.out.println(\"ABORT\");" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t \t }" + System.lineSeparator(), APPEND);
        Files.writeString(Path.of(file.getPath()), "\t }" + System.lineSeparator() + System.lineSeparator(), APPEND);
    }

    public static List<Pointcut> generatePointcutsForService(Service service, File captureMessageAspects) throws IOException {
        List<MessageChannel> channels = service.getMessageChannels();
        List<Pointcut> pointcuts = new ArrayList<>();

        for (MessageChannel channel : channels) {
            Pointcut pointcut = new Pointcut(channel.getJoinpoint(), "capture" + channel.getName());
            Files.writeString(Path.of(captureMessageAspects.getPath()), "\t @Pointcut(value = \"" + pointcut.getJoinpoint() + "\")" + System.lineSeparator(), APPEND);
            Files.writeString(Path.of(captureMessageAspects.getPath()), "\t private void " + pointcut.getName() + "() {}" + System.lineSeparator() + System.lineSeparator(), APPEND);
            pointcuts.add(pointcut);
        }

        return pointcuts;
    }

    public static void writeTemplateCodeForAspect(File file) throws IOException {
        PrintStream ps = new PrintStream(file);
        ps.println("package io.pivotal.loancheck.aspects;"); // TODO: inject the basepackage from the service.
        ps.println("");
        ps.println("import org.aspectj.lang.ProceedingJoinPoint;");
        ps.println("import org.aspectj.lang.annotation.Around;");
        ps.println("import org.aspectj.lang.annotation.Aspect;");
        ps.println("import org.aspectj.lang.annotation.Pointcut;");
        ps.println("import org.springframework.stereotype.Component;");
        ps.println("");
        ps.println("@Component(\"around\")");
        ps.println("@Aspect");
        ps.println("public class CaptureSomeMessagesAspect {");
        ps.println("");
        ps.println("\t private int counter = 1;");
        ps.println("");
    }

    public static void writeTemplateCodeForEndOfFile(File file) throws IOException {
        Files.writeString(Path.of(file.getPath()), "}", APPEND);
    }

    // IO Operations

    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        for (String f : sourceDirectory.list()) {
            copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }

    private static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    private static void copyFile(File sourceFile, File destinationFile) throws IOException {
        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
