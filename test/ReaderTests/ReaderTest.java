package ReaderTests;

import javax.json.JsonObject;

//import com.pseuco.project.Main;

import kongruenz.LTS;
import kongruenz.util.LTSReader;

public class ReaderTest {

	public static void main(String[] args) {
		
		LTSReader Reader = new LTSReader(args);
		LTS lts = Reader.generateLTSfromJSON();
		JsonObject LTS = lts.ToJson();
//		Main.openInBrowserDemo(LTS);

	}

}
