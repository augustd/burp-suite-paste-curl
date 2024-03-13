package burp;

import burp.api.montoya.logging.Logging;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;


/**
 * Paste cURL: A BurpSuite extension to allow pasting curl commands into a new tab in Repeater
 *
 * @author augustd
 */
public class BurpExtender implements BurpExtension {

    public static final String EXTENSION_NAME = "Paste cURL";
    private static BurpExtender instance;
    private MontoyaApi api;

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;

        // set extension name
        api.extension().setName(EXTENSION_NAME);

        api.userInterface().registerContextMenuItemsProvider(new MenuItemsProvider(api));

        instance = this;
    }

    public static BurpExtender getInstance() {
        return instance;
    }

    public MontoyaApi getApi() {
        return api;
    }

}
