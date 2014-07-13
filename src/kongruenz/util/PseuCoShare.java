package kongruenz.util;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Submits files to the pseuCo.com-Sharing-API.
 * 
 * @author Felix Freiberger
 * 
 */
public class PseuCoShare {
	String apiUrl;

	/**
	 * Initializes the object with a custom API endpoint.
	 * 
	 * @param apiUrl
	 *            The base URL. Must end with a slash.
	 */
	private PseuCoShare(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	/**
	 * Initializes the object with the default API endpoint.
	 */
	public PseuCoShare() {
		this("http://pseuco.com/api/");
	}

	/**
	 * Opens a URL in the local Browser.
	 * 
	 * @param uri
	 *            The URL to open.
	 * @throws Exception
	 */
	private void openInBrowser(URI uri) throws Exception {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
			throw new Exception("Cannot open the Browser.", e);
		}
	}

	/**
	 * Submits a file to the sharing server.
	 * 
	 * @param file
	 *            The file, as a JSON object.
	 * @param temporary
	 *            Whether the sharing link should expire after a few minutes.
	 *            Please use this if just want to open the link once.
	 * @return A URL which show the shared file when opened.
	 * @throws Exception
	 */
	private URI submitFile(JsonObject file, boolean temporary) throws Exception {
		try {
			URLConnection connection = new URL(apiUrl + "paste/add")
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded);charset=UTF-8");

			OutputStream output = connection.getOutputStream();

			JsonObject data = Json.createObjectBuilder().add("file", file)
					.add("temporary", temporary).build();
			// the "temporary"-flag is important:
			// it saves disk space on the server, and allows more API calls per
			// minute from your IP address

			output.write(data.toString().getBytes(Charset.forName("UTF-8")));

			InputStream response = connection.getInputStream();
			JsonObject responseData = Json.createReader(response).readObject();

			URI shareLink = new URI(responseData.getString("url"));

			return shareLink;
		} catch (Exception e) {
			throw new Exception("Could not paste the file.", e);
		}
	}

	/**
	 * Submits a file to the sharing server.
	 * 
	 * @param fileType
	 *            The type of the file, e.g. "pseuco", "ccs" or "lts".
	 * @param fileName
	 *            The file name. Must not be null, but may be empty.
	 * @param fileContent
	 *            The actual file content. Should be a valid file of the
	 *            fileType specified.
	 * @param temporary
	 *            Whether the sharing link should expire after a few minutes.
	 *            Please use this if just want to open the link once.
	 * @return
	 * @throws Exception
	 */
	private URI submitFile(String fileType, String fileName,
			JsonObject fileContent, boolean temporary) throws Exception {
		JsonObject file = Json.createObjectBuilder().add("type", fileType)
				.add("name", fileName).add("content", fileContent).build();

		return submitFile(file, temporary);
	}

	/**
	 * Opens an LTS object in pseuCo.com.
	 * 
	 * @param lts
	 *            The LTS object.
	 * @throws Exception
	 */
	public void submitAndOpenLts(JsonObject lts) throws Exception {
		openInBrowser(submitFile("lts", "", lts, true));
	}
}
