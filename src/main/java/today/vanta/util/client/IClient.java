package today.vanta.util.client;

public interface IClient {
    String CLIENT_NAME = "Drift";
    String CLIENT_VERSION = "alpha";
    String CLIENT_FULL_TITLE = CLIENT_NAME + " - " + CLIENT_VERSION;

    String USERNAME = System.getProperty("user.name");
}
