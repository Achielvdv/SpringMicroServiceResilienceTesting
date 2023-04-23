import domain.Globals;
import domain.Service;
import services.CommandService;
import services.ReadService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private static final String URL_NORMAL = Globals.URL;
    private static final String URL_RESILIENCE = Globals.RESILIENCE_URL;

    public static void main(String[] args) throws IOException {
//        Path dir = Paths.get(URL_NORMAL);
        Path dir = Paths.get(URL_RESILIENCE);


        System.out.println("\n=============================================");
        System.out.println(" SERVICE DISCOVERY PHASE & INITIAL TEST RUNS ");
        System.out.println("=============================================");

        // Discovery of the services and their tests
        List<Service> services = ReadService.findServices(dir);
        CommandService.runAllTests(services);

        System.out.println("\n=========================");
        System.out.println(" DISCOVER CHANNELS PHASE ");
        System.out.println("=========================");

        // Inject aspects and run the application to discover message channels
//        CommandService.discoverChannelsForAllServices(services);
//        CommandService.discoverStreamListenersForAllServices(services);
//        CommandService.discoverStreamListenersTest(services);


        System.out.println("\n===================================");
        System.out.println(" CAPTURE MESSAGE PHASE & TEST RUNS ");
        System.out.println("===================================");

        // generate aspects to intercept messages based on channels found in previous step
        //CommandService.generateMessageCapturingAspects(services);

        // Inject the generated aspects and execute tests
        CommandService.injectCaptureMessageAspectsAndRunTestsForAllServices(services);
    }
}
