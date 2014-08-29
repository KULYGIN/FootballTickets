package HttpLayer;

import ServiceLayer.JSONBuilder;
import org.json.simple.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Date;

public class ClientSession implements Runnable {
    private Socket socket;
    private InputStream in = null;
    private OutputStream out = null;

    @Override
    public void run() {
        try {
            String header = readHeader();
            System.out.println(header + "\n"); 
            int matchID = getMatchIDFromHeader(header);
            System.out.println("matchID: " + matchID + "\n");
            int code = 404;
            JSONObject jsonObject = JSONBuilder.prepareAndGetJSON(matchID);
            if (jsonObject != null) {
                code = send(jsonObject);
            } else {
                send(null);
            }
            System.out.println("Result code: " + code + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        initialize();
    }

    private void initialize() throws IOException { 
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    private String readHeader() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String ln = null;
        while (true) {
            ln = reader.readLine();
            if (ln == null || ln.isEmpty()) {
                break;
            }
            builder.append(ln + System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    private int getMatchIDFromHeader(String header) {
        int from = header.indexOf(" ") + 1;
        int to = header.indexOf(" ", from);
        String matchIDString = header.substring(from+1, to);
        int paramIndex = matchIDString.indexOf("/");
        if (paramIndex != -1) {
            matchIDString = matchIDString.substring(0, paramIndex);
        }
        try {
            return Integer.valueOf(matchIDString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int send(JSONObject jsonObject) throws IOException {
        int code;
        if(jsonObject != null) {
            code = 200;
        }
        else {
            code = 404;
            return code;
        }
        String header = getHeader(code); // "UTF-8"
        PrintStream answer = new PrintStream(out, true, "windows-1251");
        answer.print(header);
        if (code == 200) {
            byte[] b = jsonObject.toJSONString().getBytes(Charset.forName("windows-1251"));
            out.write(b, 0, b.length);
        }
        return code;
    }

    private String getHeader(int code) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("HTTP/1.1 " + code + " " + getAnswer(code) + "\n");
        buffer.append("Date: " + new Date().toGMTString() + "\n");
        buffer.append("Accept-Ranges: none\n");
        buffer.append("\n");
        return buffer.toString();
    }

    private String getAnswer(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            default:
                return "Internal Server Error";
        }
    }
}