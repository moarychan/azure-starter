
package name.lizhe.splitter;

public class MySpeakerService implements SpeakerService {

    @Override
    public void sayHello(String name) {
        System.out.println("Hello " + name);
    }
}