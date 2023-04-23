package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import domain.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class ReadService {
    private static final String[] IGNORE_CASES = { "import", "package", "/*"};

    public static List<Service> findServices(Path dir) throws IOException {
        String[] array = {"node_modules"};
        List<Service> services = new ArrayList<>();
        List<String> filesToIgnore = new ArrayList<>(List.of(array));

        // GET EACH MICRO SERVICE
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file: stream) {
                File f = new File(String.valueOf(file));
                if (f.isDirectory() && !filesToIgnore.contains(f.getName()) && f.getName().charAt(0) != '.') {
                    // GET INFO ABOUT A MICRO SERVICE
                    DirectoryStream<Path> streamInSrc = Files.newDirectoryStream(file, Files::isDirectory);
                    for (Path path : streamInSrc) {
                        if (path.getFileName().toString().equals("src")) {
                            Service service = new Service();
                            // in the src folder, now read the test folder and main folder
                            File testDirectory = new File(String.valueOf(path) + "/test/java");
                            List<File> filesFromTest = new ArrayList<>();

                            readPackageToFiles(testDirectory, filesFromTest);
                            List<TestClass> testClasses = new ArrayList<>();
                            for (File fileInTest : filesFromTest) {
                                List<TestCase> testCases = new ArrayList<>();

                                TestClass testClass = new TestClass(fileInTest.getName().split("\\.")[0], fileInTest.getPath(), testCases);
                                testClass.setTestCases(findTestCases(testClass));
                                testClasses.add(testClass);
                            }
                            System.out.println("Found Service: " + f.getName().toUpperCase());
                            service.setName(f.getName());
                            service.setPath(f.getPath());
                            service.setTestClasses(testClasses);
                            services.add(service);
                        }
                    }
                }
            }


        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }

        return services;
    }

    // Find all test cases in one class.
    private static List<TestCase> findTestCases(TestClass testClass) {
        List<TestCase> testCases = new ArrayList<>();
        AtomicBoolean isNextLineTestCase = new AtomicBoolean(false);

        AtomicReference<String> name = new AtomicReference<>("");

        try (Stream<String> stream = Files.lines(Paths.get(testClass.getPath()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> {
                if (s.split(" ").length > 0 && !Arrays.asList(IGNORE_CASES).contains(s.split(" ")[0])) {
                    if (isNextLineTestCase.get()) {
                        // New test is found, add the previous to the list.
                        if (!name.get().equals("")) {
                            // add the testCase to the testClass
                            TestCase testCase = new TestCase(name.get());
                            testCases.add(testCase);
                        }

                        // getting the name of the test case
                        String[] split = s.split(" ");
                        for (String word : split) {
                            if (word.contains("()")) {
                                name.set(word.substring(0, word.length() - 2));
                            }
                        }
                    }
                    isNextLineTestCase.set(s.contains("@Test"));
                }
            });

            TestCase addTestCase = new TestCase(name.get());
            if (!testCases.contains(addTestCase)) {
                testCases.add(addTestCase);
            }
        } catch (IOException e) {
            //handle exception
        }

        return testCases;
    }

    // Going to read all files of a folder (recursively calls itself until it reaches a file instead of folder)
    private static void readPackageToFiles(File dir, List<File> files) throws IOException {
        File[] filesFromDir = dir.listFiles();
        if (filesFromDir != null) {
            for (File file : filesFromDir) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    readPackageToFiles(file, files);
                }
            }
        }
    }














    // Transform a Service object to a file. (JSON)
    public static void writeServicesToIndexFile(Path dir, List<Service> services) throws IOException {
        FileWriter jsonFileWriter = new FileWriter(dir + "/src/main/index-plugin.json");
        Gson gson = new Gson();
        gson.toJson(services, jsonFileWriter);
        jsonFileWriter.close();
    }

    // Checks if the given file is a testfile.
    private static Boolean isTestFile(Path file) throws IOException {
        String string = Files.readString(file);
        return string.contains("@SpringBootTest");
    }

    // Is going to find all Channels or Handlers in a TestCase
    private static ExecutionTrace getExecutionTraceForClass(TestClass testClass) throws IOException {
        List<Channel> channels = new ArrayList<>();
        List<Handler> handlers = new ArrayList<>();

        AtomicBoolean isNextLineAHandler = new AtomicBoolean(false);
        AtomicBoolean isNextLineAChannel = new AtomicBoolean(false);

        AtomicReference<String> name = new AtomicReference<>("");
        try (Stream<String> stream = Files.lines(Paths.get(testClass.getPath()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> {
//
//                if (isNextLineAChannel.get()) {
//                    for (String className : CHANNEL_CLASS_NAMES) {
//                        if (s.contains(className)) {
//                            String[] array = s.split(" ");
//                            String nameInClass = array[array.length - 1].substring(0, array[array.length - 1].length() - 1);
//                            channels.add(new Channel(name.get(), nameInClass));
//                        }
//                        isNextLineAChannel.set(false);
//                    }
//                }
//
//                if (isNextLineAHandler.get()) {
//                    for (String className : HANDLER_CLASS_NAMES) {
//                        if (s.contains(className)) {
//                            String[] array = s.split(" ");
//                            String nameInClass = array[array.length - 1].substring(0, array[array.length - 1].length() - 1);
//                            handlers.add(new Handler(name.get(), nameInClass));
//                        }
//                        isNextLineAHandler.set(false);
//                    }
//                }


                if (s.contains("@SpyBean")) {
                    name.set(s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\"")));
                    isNextLineAHandler.set(true);
                }

                if (s.contains("@Qualifier")) {
                    name.set(s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\"")));
                    isNextLineAChannel.set(true);
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new ExecutionTrace(channels, handlers);
    }

    public static Service[] jsonToServiceArray(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inJson = Service.class.getResourceAsStream(json);
        Service[] services = objectMapper.readValue(inJson, Service[].class);
        for (Service service : services) {
            System.out.println(service.getName());
        }

        return services;
    }
}
