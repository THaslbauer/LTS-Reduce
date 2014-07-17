package kongruenz.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Utf8IO {
	
	public static final String tau = "τ";
	
	public static void demo(){
		// read standard input as UTF-8
		String lts = Utf8IO.readStdin();
		// read file
		String lts2 = Utf8IO.read(new File("test.json"));
		
		// print LTS to stdout
		Utf8IO.writeStdout(lts);
		// and save to file
		Utf8IO.write(new File("test2.json"), lts2);		
	}

	
	/**
	 * Reads the stream to a string, UTF-8 encoded. Does not close the stream.
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String read(InputStream input) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		StringBuilder builder = new StringBuilder();
		String s;
		while ((s = in.readLine()) != null/* && s.length() != 0*/) { // read until end
			builder.append(s);
			builder.append("\n");
		}
		return builder.toString();
	}
	
	/**
	 * Reads a file to a string, UTF-8 encoded. 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static String read(File f) {
		try(FileInputStream fin = new FileInputStream(f)){
			return read(fin);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads everything from standard input as UTF-8, and returns it as a String.
	 * @return
	 */
	public static String readStdin() {
		try{
			return read(System.in);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	
	
	/**
	 * Writes the string to standard output. As System.out.println(), but in UTF-8
	 * @param text
	 */
	public static void writeStdout(String text){
		write(System.out, text+"\n");
	}
	
	/**
	 * Writes the string to error output. As System.err.println(), but in UTF-8
	 * @param text
	 */
	public static void writeStderr(String text){
		write(System.err, text+"\n");
	}
	
	/**
	 * Writes a string as UTF-8 to an output stream. Does not close the stream.
	 * @param out
	 * @param text
	 */
	public static void write(OutputStream out, String text){
		try (PrintWriter w = new PrintWriter(new OutputStreamWriter(out, "UTF-8"))){
			w.print(text);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Writes a string as UTF-8 to a file. 
	 * @param f
	 * @param text
	 */
	public static void write(File f, String text){
		try (FileOutputStream fs = new FileOutputStream(f)){
			write(fs, text);
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}
}
