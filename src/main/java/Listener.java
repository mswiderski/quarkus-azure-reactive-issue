import org.eclipse.microprofile.reactive.messaging.Incoming;

public class Listener {

    @Incoming(value = "test")
    public void get(String message) {
        System.out.println("Message received " + message);
    }

}
