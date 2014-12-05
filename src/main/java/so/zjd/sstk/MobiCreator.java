package so.zjd.sstk;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import so.zjd.sstk.util.HttpHelper;
import so.zjd.sstk.util.RegexUtils;

public class MobiCreator {

	private static final Logger LOGGER = LoggerFactory.getLogger(MobiCreator.class);
	private static final char[] IMG_START_TAG = new char[] { '<', 'i', 'm', 'g' };
	private static final char[] IMG_END_TAG = new char[] { '/', '>' };

	private static List<String> supportedImgFormats = new ArrayList<String>();

	static {
		supportedImgFormats.add(".JPEG");
		supportedImgFormats.add(".JPG");
		supportedImgFormats.add(".GIF");
		supportedImgFormats.add(".PNG");
		supportedImgFormats.add(".BMP");
	}
	private PageEntry page;
	private int index = 0;

	public MobiCreator(PageEntry page) {
		this.page = page;
	}

	public void create() throws InterruptedException {
		processImages(page);
	}

	protected void processImages(PageEntry page) {
		StringBuilder processed = new StringBuilder();
		StringBuilder imgElement = new StringBuilder();
		StringBuilder content = page.getContent();
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if (index < 4 && c == IMG_START_TAG[index]) {
				index++;
			} else {
				index = 0;
			}
			if (index == 4) {
				processed.delete(processed.length() - 3, processed.length());
				imgElement.append("<img");
				index = 0;
				while (true) {
					c = content.charAt(++i);
					imgElement.append(c);
					if (index < 2 && c == IMG_END_TAG[index]) {
						index++;
					} else {
						index = 0;
					}
					if (index == 2) {
						index = 0;
						break;
					}
				}
				processed.append(downloadImage(imgElement.toString()));
				imgElement.delete(0, imgElement.length());
			} else {
				processed.append(c);
			}
			page.setContent(processed);
		}
	}

	private String downloadImage(String imgElement) {
		// LOGGER.debug("img element:"+imgElement.toString());
		final String url = RegexUtils.findAll("(?<=src=\").*?(?=\")", imgElement, false).get(0);
		final String fileName = getFileName(url);
		final String result = RegexUtils.replaceAll("(?<=src=\").*?(?=\")", imgElement, "images/" + fileName, false);
		try (OutputStream os = new FileOutputStream(page.getImgDir() + fileName)) {
			// LOGGER.debug("image save path:" + page.getImgDir() +
			// fileName);
			HttpHelper.download(url, GlobalConfig.DOWNLOAD_TIMEOUT, os);
			LOGGER.debug("downloaded url:" + url + ",replaced img tag:" + result);
		} catch (Exception e) {
			LOGGER.error("download image error:" + url);
		}
		return result;
	}

	private String getFileName(String url) {
		int index = url.lastIndexOf("/");
		if (index != -1) {
			return url.substring(index + 1);
		}
		return "";
	}
}
