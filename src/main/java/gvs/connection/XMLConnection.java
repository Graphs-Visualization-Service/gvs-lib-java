
package gvs.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection-Class for the GVS. If the Server is busy, the client will exit.
 * 
 * @author mkoller
 */
public class XMLConnection {

  private String serverAdress = "";
  private int serverPort = 0;
  private Socket socket = null;
  private PrintStream outStream = null;
  private BufferedReader inReader = null;

  private static final Logger logger = LoggerFactory
      .getLogger(XMLConnection.class);

  public XMLConnection(String pServerAdress, int pServerPort) {
    this.serverAdress = pServerAdress;
    this.serverPort = pServerPort;
  }

  /**
   * Connect to the GVS-Server and reserve the Service If the Server is busy,
   * the client wilb be terminated
   * 
   * @return answer from the Server.
   */
  public synchronized String connectToServer() {
    String str = "";
    try {
      logger.info("Connect to " + serverAdress + " " + serverPort);
      socket = new Socket(serverAdress, serverPort);
      outStream = new PrintStream(socket.getOutputStream(), true);
      inReader = new BufferedReader(
          new InputStreamReader(socket.getInputStream()));
      outStream.println("reserveGVS");
      outStream.flush();
      str = inReader.readLine();

      if (str.equals("FAILED")) {
        logger.error("Server busy!!!!!!!! System exit");
        outStream.close();
        inReader.close();
        socket.close();
        System.exit(0);
      } else if (str.equals("OK")) {
        logger.info("Server is free. Communication are established");
      }
    } catch (UnknownHostException e) {
      logger.error("Unknown Host. System exit", e);

    } catch (IOException e) {
      logger.error("No Server found", e);
    }
    return str;
  }

  /**
   * Sends the Xml-Document to the GVS-Server
   * 
   * @param pDocument
   */
  public synchronized void sendFile(Document pDocument) {
    XMLWriter writer;
    try {
      writer = new XMLWriter(outStream);
      writer.write(pDocument);
      writer.flush();
      logger.info("Send data");

      // Signal gor end data
      outStream.println(";");
      outStream.flush();
      logger.info("Finish send data");

    } catch (UnsupportedEncodingException e) {
      logger.error("Unsupported encoding", e);
    } catch (IOException e) {
      logger.error("No Server found", e);
    }

  }

  /**
   * Disconnect from Server. Must be called to transfer the datas properly
   *
   */
  public synchronized void disconnectFromServer() {
    outStream.println("releaseGVS");
    outStream.flush();
    try {
      logger.info("Close Connection");
      inReader.close();
      outStream.flush();
      outStream.close();
      socket.close();
    } catch (IOException e) {
      logger.error("Unable to disconnect from server", e);
    }
  }
}
