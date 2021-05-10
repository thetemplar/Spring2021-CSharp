import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.simulate.GameResult;

public class Spring2021 {
    static String[] DEFAULT_AI = new String[] {
            "C:\\Users\\Kristian\\AppData\\Local\\Programs\\Python\\Python38-32\\python.exe", "C:\\Users\\Kristian\\IdeaProjects\\SpringChallenge2021\\config\\Boss.py"
    };
    static String[] STARTER_AI = new String[] {
            "C:\\Users\\Kristian\\AppData\\Local\\Programs\\Python\\Python38-32\\python.exe", "C:\\Users\\Kristian\\IdeaProjects\\SpringChallenge2021\\starterAIs\\starter.py"
    };
    static String[] BOSS_WOOD2 = new String[] {
        "python3", "config/level1/Boss.py"
    };
    static String[] BOSS_WOOD1 = new String[] {
        "python3", "config/level2/Boss.py"
    };

    static Integer port = -1;
    public static void main(String[] args) throws IOException, InterruptedException {
        //String sPort = args[0];
        //port = Integer.parseInt(sPort);
        //System.out.println("port: " + Integer.parseInt(sPort));

        launchGame();
    }

    public static void launchGame() throws IOException, InterruptedException {

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.setLeagueLevel(1);
        Properties gameParameters = new Properties();
        gameRunner.setGameParameters(gameParameters);

        gameRunner.addAgent(
            //new String[] {
            //        "C:\\Users\\Kristian\\AppData\\Local\\Programs\\Python\\Python38-32\\python.exe", "C:\\Users\\Kristian\\IdeaProjects\\SpringChallenge2021\\config\\socketbot.py", " " + port
            //},
                DEFAULT_AI,
            "Tororo",
            "https://static.codingame.com/servlet/fileservlet?id=61910307869345"
        );
        
        gameRunner.addAgent(
                DEFAULT_AI,
            "Ghilbib",
            "https://static.codingame.com/servlet/fileservlet?id=61910289640958"
        );

        java.util.Random rand = new java.util.Random();
        int n = rand.nextInt(50000);
        gameRunner.setSeed(7308340236775320085L + n);

        //gameRunner.start(8888);
        GameResult res = gameRunner.simulate();

        java.util.List<String> v = res.errors.get("0");
        for (String s: v) {
            if(s != null)
                System.out.println(" > " + s);
        }
/*
        System.out.println("summaries > ");
        for (String s: res.summaries) {
            System.out.println(" > " + s);
        }
*/
        System.out.println("Res0: " + res.scores.get(0) + " - Res1: " + res.scores.get(1));
    }
}
