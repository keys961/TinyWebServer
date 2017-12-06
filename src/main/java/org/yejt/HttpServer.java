package org.yejt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer implements Runnable
{
    private boolean condition = true;

    private ServerSocket serverSocket;

    private static final int PORT = 2210;

    private ExecutorService threadPool;

    private void init() throws IOException
    {
        System.out.println("Starting server...");
        serverSocket = new ServerSocket(PORT);// bind port to 2210
        System.out.println("Starting to build the thread pool...");
        threadPool = Executors.newCachedThreadPool();
        System.out.println("Starting server succeed! Input 0 to exit.");
    }

    public void setCondition(boolean condition)
    {
        this.condition = condition;

    }

    public void run()
    {
        try
        {
            init();
        }
        catch (IOException e)
        {
            System.err.println("Creating server socket error!");
            return;
        }

        while(condition)
        {
            try
            {
                Runnable r = new ServeRunner(serverSocket.accept());
                threadPool.submit(r);
            }
            catch (IOException e)
            {
                System.err.println("Accept exception occurred!");
                return;
            }
        }
    }
}
