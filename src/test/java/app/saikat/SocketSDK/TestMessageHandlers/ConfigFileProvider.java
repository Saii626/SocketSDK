package app.saikat.SocketSDK.TestMessageHandlers;

import java.io.File;

import app.saikat.Annotations.ConfigurationManagement.ConfigFile;
import app.saikat.Annotations.DIManagement.Provides;

public class ConfigFileProvider {

	@Provides
	@ConfigFile
	public static File getConfigFile() {
		return new File("testConfig.config");
	}
}