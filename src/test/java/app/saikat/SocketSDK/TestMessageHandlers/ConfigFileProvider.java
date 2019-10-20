package app.saikat.SocketSDK.TestMessageHandlers;

import java.io.File;

import app.saikat.ConfigurationManagement.interfaces.ConfigFile;
import app.saikat.DIManagement.Provides;

public class ConfigFileProvider {

    @Provides
    @ConfigFile
    public static File getConfigFile() {
        return new File("testConfig.config");
    }
}