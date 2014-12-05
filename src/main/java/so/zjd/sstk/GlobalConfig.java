package so.zjd.sstk;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import so.zjd.sstk.util.PathUtils;

public class GlobalConfig {
	public static final Logger LOGGER = LoggerFactory.getLogger(Service.class);
	public static final String WORK_DIR = PathUtils.getAppDir(GlobalConfig.class);
	public static final Properties CONFIGS = new Properties();
	public static final String BASE_TEMP_DIR = WORK_DIR + "\\temp";

	public static int SERVICE_THREADS = 0;
	public static int SERVICE_TIMEOUT = 0;
	public static int DOWNLOAD_TIMEOUT = 0;
	public static int DOWNLOAD_THREADS = 0;

	static {
		try {
			DOMConfigurator.configure(PathUtils.getRealPath("classpath:log4j.xml"));
			CONFIGS.load(new FileInputStream(PathUtils.getRealPath("classpath:sstk.properties")));

			SERVICE_THREADS = Integer.valueOf(CONFIGS.getProperty("sstk.service.threads"));
			SERVICE_TIMEOUT = Integer.valueOf(CONFIGS.getProperty("sstk.service.timeout"));
			DOWNLOAD_TIMEOUT = Integer.valueOf(CONFIGS.getProperty("sstk.download.timeout"));
			DOWNLOAD_THREADS = Integer.valueOf(CONFIGS.getProperty("sstk.download.threads"));

		} catch (Exception e) {
			LOGGER.error("static init error.", e);
		}
	}

	public static void init() {
		// do nothing
	}
}
