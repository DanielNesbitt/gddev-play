import play.Application;
import play.GlobalSettings;

import java.io.FileReader;
import java.io.IOException;

/**
 * @author Daniel Nesbitt
 */
public final class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        try {
            String homeFolder = System.getProperty("user.home");
            System.getProperties().load(new FileReader(homeFolder + "/Desktop/api-key.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
