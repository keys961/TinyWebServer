package org.yejt;

import java.io.*;
import java.net.Socket;

public class ServeRunner implements Runnable
{
    private static final String RESOURCE_PATH = new File("").getAbsolutePath()
            + File.separator + "src" + File.separator + "main" + File.separator + "resources";//resource root

    private static final String errorBody = "<html><body>404 Error</body></html>";//error body

    private Socket socket;

    private BufferedReader in;

    private OutputStream out;

    private byte[] contentBuffer = new byte[65536]; //buffer

    public ServeRunner(Socket socket)
    {
        this.socket = socket;
    }

    /**
     * Initialization
     * @throws IOException: Exception that occurred when initiating input reader
     */
    private void init() throws IOException
    {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = socket.getOutputStream();
    }

    /**
     * Close input/output stream and socket
     */
    private void destroy()
    {
        try
        {
            in.close();
            out.close();
            socket.close();
        }
        catch (IOException e)
        {
            //do nothing
        }
    }

    /**
     * Parsing GET request text and send response to the web browser
     * @param request: The request head text
     */
    private void doGet(String request)
    {
        String fileType;
        StringBuilder response = new StringBuilder();
        String[] lines = request.split("\r\n");
        String[] getElements = lines[0].split("\\s+");
        String path = "";
        if(getElements[1].contains(".html"))//html file
            fileType = "text/html";
        else if(getElements[1].contains(".txt"))//txt file
        {
            fileType = "text/plain";
            if(getElements[1].contains("/html")) //delete prefix /html
                getElements[1] = getElements[1].substring(5);
        }
        else //other files, such as image...
        {
            fileType = "image/jpeg";
            if(getElements[1].contains("/html"))
                getElements[1] = getElements[1].substring(5);
        }
        path = RESOURCE_PATH + getElements[1];//true absolute path
        File responseFile = new File(path);//create file object
        if(!responseFile.exists())//404 not found
        {
            response.append("HTTP/1.1 404 File Not Found\r\n");
            response.append("Server: Localhost\r\n");
            response.append("Content-Type: text/html\r\n");
            response.append("Content-Length: " + errorBody.length() + "\r\n\r\n");
            response.append(errorBody);
            contentBuffer = response.toString().getBytes(); //write to buffer...

            try
            {
                out.write(response.toString().getBytes());
                out.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.err.println("Output error");
            }
        }
        else
        {
            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Server: Localhost\r\n");
            response.append("Content-Length: " + responseFile.length() + "\r\n");
            response.append("Content-Type: " + fileType + "\r\n\r\n");
            byte[] header = response.toString().getBytes();
            System.arraycopy(header, 0, contentBuffer, 0, header.length); //copy head to the buffer
            try(FileInputStream fileInputStream = new FileInputStream(responseFile))
            {
                int res = fileInputStream.read(contentBuffer, header.length, fileInputStream.available());//append byte stream to the buffer
                out.write(contentBuffer);//write
                out.flush();//flush
            }
            catch (IOException e)
            {
                System.err.println("IO error!");
            }
        }
    }

    /**
     * Parsing POST request text and send response to the web browser
     * @param request: The request head text
     */
    private void doPost(String request)
    {
        final String USERNAME = "3150102210";
        final String PASSWORD = "2210";

        StringBuilder response = new StringBuilder();
        String responseData = "";
        String[] lines = request.split("\r\n");
        String[] postElements = lines[0].split("\\s+");

        if(postElements[1].equals("/html/dopost")) //dopost request 200
        {
            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Server: Localhost\r\n");
            //response.append("Content-Length: " + responseFile.length() + "\r\n");
            response.append("Content-Type: text/html\r\n");
            char[] postedData = new char[4096];
            int res = 0;
            try
            {
                res = in.read(postedData);//get posted data
            }
            catch (IOException e)
            {
                System.err.println("Read posted data error!");
                return;
            }

            String postedStr = new String(postedData);
            String username = postedStr.substring(0, postedStr.indexOf("&pass")).substring(6);
            String password = postedStr.substring(postedStr.indexOf("&pass") + 1).substring(5);
            password = password.substring(0, password.indexOf(0));
            if(username.equals(USERNAME) && password.equals(PASSWORD))
                responseData = "<html><head><title>Login Succeeded!</title></head><body>Login Succeeded!</body></html>";
            else
                responseData = "<html><head><title>Login Failed!</title></head><body>Login Failed!</body></html>";
            response.append("Content-Length: " + responseData.length() + "\r\n\r\n");
            response.append(responseData);
            System.arraycopy(response.toString().getBytes(), 0, contentBuffer, 0, response.toString().getBytes().length);

            try
            {
                out.write(contentBuffer);
                out.flush();
            }
            catch (IOException e)
            {
                System.err.println("IO error!");
            }
        }
        else
        {
            response.append("HTTP/1.1 404 Method Not Found\r\n");
            response.append("Server: Localhost\r\n");
            response.append("Content-Length: 0\r\n");
            response.append("Content-Type: text/html\r\n\r\n");
            System.arraycopy(response.toString().getBytes(), 0, contentBuffer, 0, response.toString().getBytes().length);
            try
            {
                out.write(contentBuffer);
                out.flush();
            }
            catch (IOException e)
            {
                System.err.println("IO error!");
            }
        }

    }

    public void run()
    {
        try
        {
            init();
        }
        catch (IOException e)
        {
            System.err.println("Socket init error!");
            return;
        }
        StringBuilder requestContent = new StringBuilder();
        String line;
        String request;
        try
        {
            while (!(line = in.readLine()).equals(""))
            {
                requestContent.append(line);
                requestContent.append("\r\n");
            }
        }
        catch (IOException e)
        {
            System.err.println("Socket init error!");
            return;
        }

        request = requestContent.toString();
        if(request.contains("GET"))
            doGet(request);
        else if(request.contains("POST"))
            doPost(request);

        destroy();
    }
}
