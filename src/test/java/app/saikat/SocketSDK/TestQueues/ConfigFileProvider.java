package app.saikat.SocketSDK.TestQueues;

import java.io.File;

import app.saikat.Annotations.ConfigurationManagement.ConfigFile;
import app.saikat.Annotations.DIManagement.Provides;

public class ConfigFileProvider {

	@Provides
	@ConfigFile
	static File getConfigFile() {
		File testconfigFile = new File(System.getProperty("user.home") + "/test/socket_queue_test.conf");

		if (testconfigFile.exists()) {
			testconfigFile.delete();
		}

		return testconfigFile;
	}
}
