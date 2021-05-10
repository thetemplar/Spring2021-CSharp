package com.codingame.gameengine.core;

import com.codingame.gameengine.core.AbstractPlayer;
import com.codingame.gameengine.core.GameEngineModule;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.simulate.GameResult;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Types;

import java.io.*;
import java.lang.reflect.Type;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("MAIN START");

        InputStream in = System.in;
        PrintStream out = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // Do nothing.
            }
        }));
        System.setIn(new InputStream() {
            @Override
            public int read() throws IOException {
                throw new RuntimeException("Impossible to read from the referee");
            }
        });

        System.setProperty("game.mode", "multi");
        Injector injector = Guice.createInjector(new GameEngineModule());

        Type type = Types.newParameterizedType(GameManager.class, AbstractPlayer.class);
        GameManager<AbstractPlayer> gameManager = (GameManager<AbstractPlayer>) injector.getInstance(Key.get(type));

        PipedOutputStream pos = new PipedOutputStream();
        WriteToPipe automated = new WriteToPipe(pos);
        PipedInputStream pis = new PipedInputStream();
        pis.connect(automated.pos);
        automated.start();

        gameManager.start(pis, out);
    }
}
