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

        System.setProperty("game.mode", "multi");
        System.setProperty("league.level", "1");
        System.setProperty("allow.config.override", "true");
        Injector injector = Guice.createInjector(new GameEngineModule());

        Type type = Types.newParameterizedType(GameManager.class, AbstractPlayer.class);
        GameManager<AbstractPlayer> gameManager = (GameManager<AbstractPlayer>) injector.getInstance(Key.get(type));


        WriteToPipe automated = new WriteToPipe();


        PipedOutputStream pos = new PipedOutputStream();
        PrintStream ps = new PrintStream(pos);


        PipedInputStream pis = new PipedInputStream();
        pis.connect(automated.pos);
        pos.connect(automated.pis);

        automated.start();
        //gameManager.start(System.in, System.out);
        gameManager.start(pis, ps);
    }
}
