package org.yejt;

import java.io.BufferedInputStream;
import java.util.Scanner;

public class Main
{

    public static void main(String[] args)
    {
        int condition;
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        HttpServer server = new HttpServer();
        Thread httpServer = new Thread(server);
        httpServer.start();
        while(true)
        {
            condition = in.nextInt();
            if(condition == 0)
            {
                server.setCondition(false);
                try
                {
                    httpServer.interrupt();
                    break;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Server is off.");
        System.exit(0);
    }
}
