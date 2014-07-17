package ReaderTests;

import java.io.UnsupportedEncodingException;

import javax.json.JsonObject;

//import com.pseuco.project.Main;


import kongruenz.LTS;
import kongruenz.util.LTSReader;

public class ReaderTest {

	public static void main(String[] args) throws UnsupportedEncodingException {
		
		LTSReader Reader = new LTSReader(LTSReader.getString());
		LTS lts = Reader.generateLTSfromJSON();
		JsonObject LTS = lts.ToJson();
//		Main.openInBrowserDemo(LTS);
		
		//TODO: Implement openInBrowserDemo

	}

}
