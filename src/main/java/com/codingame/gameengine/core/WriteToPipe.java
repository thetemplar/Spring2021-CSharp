package com.codingame.gameengine.core;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class WriteToPipe extends Thread
{
    PipedOutputStream pos = new PipedOutputStream();
    PipedInputStream pis = new PipedInputStream();

    enum InitPhase
    {
        NOT_INIT,
        INITIAL,
        RUNNING
    }
    InitPhase playerInit = InitPhase.NOT_INIT;

    public WriteToPipe() {
    }

    static class ReadCommand
    {
        public com.codingame.gameengine.core.OutputCommand cmd;
        public int lineCount;

        public ReadCommand(OutputCommand cmd, int lineCount)
        {
            this.cmd = cmd;
            this.lineCount = lineCount;
        }

        static public ReadCommand parse(String line) {
            Pattern HEADER_PATTERN = Pattern.compile("\\[\\[(?<cmd>.+)\\] ?(?<lineCount>[0-9]+)\\]");
            Matcher m = HEADER_PATTERN.matcher(line);
            if (!m.matches()) {
                throw new RuntimeException("Error in data sent to pipe-thread");
            } else {
                OutputCommand cmd = OutputCommand.valueOf(m.group("cmd"));
                int lineCount = Integer.parseInt(m.group("lineCount"));

                return new ReadCommand(cmd, lineCount);
            }
        }
    }

    public void run()
    {
        System.out.println("run()");
        String cmd = "WAIT";
        try
        {
            DataOutputStream out = new DataOutputStream(pos);
            DataInputStream inStream = new DataInputStream(pis);
            out.write("[[INIT]0]\r\n".getBytes());
            out.write("2\n".getBytes());
            out.write("[[GET_GAME_INFO]0]\n".getBytes());

            boolean running = true;

            Scanner in = new Scanner(inStream);
            while(running) {
                String s2 = in.nextLine();
                System.out.println(s2);

                ReadCommand iCmd = ReadCommand.parse(s2);

                if (iCmd.cmd.equals(OutputCommand.NEXT_PLAYER_INPUT))
                {
                    if(playerInit == InitPhase.NOT_INIT) {
                        playerInit = InitPhase.INITIAL;
                    }
                    if(playerInit == InitPhase.RUNNING)
                    {
                        if (iCmd.lineCount > 37)
                        {
                            for(int i = 0; i < 38; i++)
                            {
                                s2 = in.nextLine();
                            }
                        }
                        if (iCmd.lineCount > 0)
                        {
                            cmd = BotLoop(in);
                        }
                        else
                        {
                            cmd = "WAIT";
                        }
                    }
                    else {
                        for(int i = 0; i < iCmd.lineCount; i++)
                        {
                            s2 = in.nextLine();
                            System.out.println(s2);
                        }
                    }
                }
                else if (iCmd.cmd.equals(OutputCommand.NEXT_PLAYER_INFO))
                {
                    System.out.println(" > [[SET_PLAYER_OUTPUT]1] ");
                    System.out.println(" > " + cmd);
                    out.write("[[SET_PLAYER_OUTPUT]1]\r\n".getBytes());
                    out.write((cmd + "\r\n").getBytes());
                    playerInit = InitPhase.RUNNING;


                    if(playerInit == InitPhase.RUNNING) {
                        System.out.println(" > [[GET_GAME_INFO]0]");
                        out.write("[[GET_GAME_INFO]0]\n".getBytes());
                        for (int i = 0; i < iCmd.lineCount; i++) {
                            s2 = in.nextLine();
                            System.out.println(s2);
                        }
                    }
                }
                else if (iCmd.cmd.equals(OutputCommand.SCORES))
                {
                    System.out.println(" > FINISHED!!!");
                    running = false;
                    for (int i = 0; i < iCmd.lineCount; i++) {
                        s2 = in.nextLine();
                        System.out.println(s2);
                    }
                }
                else
                {
                    for(int i = 0; i < iCmd.lineCount; i++)
                    {
                        s2 = in.nextLine();
                        System.out.println(s2);
                    }
                }
            }
            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    String BotLoop(Scanner in)
    {
        int day = in.nextInt(); // the game lasts 24 days: 0-23
        int nutrients = in.nextInt(); // the base score you gain from the next COMPLETE action
        int sun = in.nextInt(); // your sun points
        int score = in.nextInt(); // your current score
        int oppSun = in.nextInt(); // opponent's sun points
        int oppScore = in.nextInt(); // opponent's score
        boolean oppIsWaiting = in.nextInt() != 0; // whether your opponent is asleep until the next day
        int numberOfTrees = in.nextInt(); // the current amount of trees
        for (int i = 0; i < numberOfTrees; i++) {
            int cellIndex = in.nextInt(); // location of this tree
            int size = in.nextInt(); // size of this tree: 0-3
            boolean isMine = in.nextInt() != 0; // 1 if this is your tree
            boolean isDormant = in.nextInt() != 0; // 1 if this tree is dormant
        }
        int numberOfPossibleActions = in.nextInt(); // all legal actions
        if (in.hasNextLine()) {
            in.nextLine();
        }
        ArrayList<String> possibleActions = new ArrayList<>();
        for (int i = 0; i < numberOfPossibleActions; i++) {
            possibleActions.add(in.nextLine()); // try printing something from here to start with
        }

        //DO MAGIC
        
        return possibleActions.get(possibleActions.size() - 1);
    }
}
