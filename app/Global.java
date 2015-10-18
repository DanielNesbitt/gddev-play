import play.Application;
import play.GlobalSettings;

/**
 * @author Daniel Nesbitt
 */
public final class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        System.load("~/Desktop/api-key.properties");
    }

}
