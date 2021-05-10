package com.codingame.gameengine.core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedOutputStream;

class WriteToPipe extends Thread
{
    PipedOutputStream pos;

    public WriteToPipe(PipedOutputStream pos)
    {
        this.pos = pos;
    }

    public void run()
    {
        try
        {
            DataOutputStream out = new DataOutputStream(pos);
            out.write("[[INIT]0]\r\n".getBytes());
            out.write("2\n".getBytes());
            out.write("[[GET_GAME_INFO]0]\n".getBytes());
            Thread.sleep(50);

            while(true) {
                out.write("[[SET_PLAYER_OUTPUT]1]\n".getBytes());
                Thread.sleep(50);
                out.write("WAIT\n".getBytes());
                Thread.sleep(50);
                out.write("[[GET_GAME_INFO]0]\n".getBytes());
                Thread.sleep(50);
            }
            //out.close();
        }
        catch(IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
