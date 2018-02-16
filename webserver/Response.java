
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

/*
  HTTP Response = Status-Line
    *(( general-header | response-header | entity-header ) CRLF)
    CRLF
    [ message-body ]
    Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
*/

public class Response {

  
  private static final int BUFFER_SIZE = 64 * 1024; // also, maximum file size
  public static final String STATS_URI = "/STATS";

  Request request;
  OutputStream output;

  public Response(OutputStream output) {
    this.output = output;
  }

  public void setRequest(Request request) {
    this.request = request;
  }

  public void sendStaticResource() throws IOException {

    // Andrew Whitaker: display a special "stats" page
    if (request.getUri().contains(STATS_URI)) {
	displayStats(output);
	return;
    }
	  
    byte[] bytes = new byte[BUFFER_SIZE];
    FileInputStream fis = null;
    try {

      // Andrew Whitaker: use default file name for '/' URI
      File file = null;
      if (request.getUri().equals("/")) 
	file = new File (HttpServer.WEB_ROOT,HttpServer.DEFAULT_PAGE);
      else file = new File(HttpServer.WEB_ROOT, request.getUri());

      if (file.exists()) {
        fis = new FileInputStream(file);
        int ch = fis.read(bytes, 0, BUFFER_SIZE);
	int totalSize = 0;

	while (ch!=-1) {
	    totalSize += ch;
	    ch = fis.read(bytes,totalSize,BUFFER_SIZE - totalSize);
	}

	// Andrew Whitaker: write out a proper HTTP header
	String header = "HTTP/1.1 200 OK\r\n" +
          "Content-Type: text/html\r\n" +
	  "Content-Length: " + totalSize  + "\r\n\r\n";

	output.write(header.getBytes());
	output.write(bytes, 0, totalSize);
      }
        
      else {
        // file not found
        String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
          "Content-Type: text/html\r\n" +
          "Content-Length: 23\r\n" +
          "\r\n" +
          "<h1>File Not Found</h1>";
        output.write(errorMessage.getBytes());
      }
    }
    catch (Exception e) {
      // thrown if cannot instantiate a File object
      System.out.println(e.toString() );
    }
    finally {
      if (fis!=null)
        fis.close();
    }
  }

  // Andrew Whitaker: display a stats page
  private void displayStats(OutputStream out) throws IOException {
      StringBuilder body = new StringBuilder();
      body.append("<html><head><title>Statistics page </title></head>");
      body.append("<body> <h1> Web server statistics </h1>");
      // TODO: other stuff here!
      body.append("</body></html>");

      byte [] bodyBytes = body.toString().getBytes();

      String header = "HTTP/1.1 200 OK\r\n" +
          "Content-Type: text/html\r\n" +
	  "Content-Length: " + bodyBytes.length  + "\r\n\r\n";

      output.write(header.getBytes());
      output.write(bodyBytes);
  }					     
}