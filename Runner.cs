using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Threading;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;
using System.Text.RegularExpressions;

namespace CodingNetwork
{
    class entry
    {
        public float size;
        public float owner;
        public float dormant;
    }
    class Bot
    {
        private NeuralNetwork neuralNetwork;
        ManualResetEvent oSignalEvent = new ManualResetEvent(false);
        public ManualResetEvent oSignalEventComplete = new ManualResetEvent(false);
        Process p;
        int port;
        string pop;
        bool retry = true;
        float lastScore = 0;

        public Bot(NeuralNetwork neuralNetwork, int port, string pop)
        {
            this.neuralNetwork = neuralNetwork;
            this.port = port;
            this.pop = pop;
        }

        public void Run()
        {
            Thread t = new Thread(RunT);
            t.Start();
        }
        
        public void RunT()
        { 
            Process p = new Process();
            // Redirect the output stream of the child process.
            p.StartInfo.UseShellExecute = false;
            p.StartInfo.FileName = @"C:\Users\Kristian\IdeaProjects\SpringChallenge2021\run.bat";
            p.StartInfo.Arguments = "" + port;

            p.EnableRaisingEvents = true;
            p.StartInfo.RedirectStandardError = true;
            p.StartInfo.RedirectStandardOutput = true;
            p.OutputDataReceived += P_OutputDataReceived;
            p.ErrorDataReceived += P_OutputDataReceived;
            p.StartInfo.StandardErrorEncoding = Encoding.UTF8;
            p.StartInfo.StandardOutputEncoding = Encoding.UTF8;


            p.Start();


            p.BeginOutputReadLine();
            p.BeginErrorReadLine();

            oSignalEvent.WaitOne();
            Console.WriteLine("");
            Console.WriteLine(pop);
            Thread.Sleep(100);
            IPHostEntry host = Dns.GetHostEntry("localhost");
            IPAddress ipAddress = host.AddressList[0];
            IPEndPoint remoteEP = new IPEndPoint(IPAddress.Loopback, port);

            Socket sender = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            neuralNetwork.fitness = 0;
            while (retry)
            {
                try
                {
                    // Connect to Remote EndPoint  
                    sender.Connect(remoteEP);
                    retry = false;
                    List<byte> buffer = new List<byte>();
                    while (true)
                    {
                        while (true)
                        {
                            byte[] b = new byte[1024];
                            int r = sender.Receive(b);
                            if(r > 0)
                            {
                                buffer.AddRange(b.Take(r));

                                if (buffer.Count(x => x == '\n') == 119)
                                    break;
                            }
                        }
                        string s = System.Text.Encoding.UTF8.GetString(buffer.ToArray()).Replace("None", "-1");
                        string[] ss = s.Split("\r\n");

                        float[] inputs = new float[ss.Length - 1];
                        entry[] gamemap = new entry[37];
                        for (int i = 0; i < inputs.Length; i++)
                        {
                            string sss = (string)ss[i];
                            inputs[i] = float.Parse(sss);
                            int f = (i - 8) / 3;
                            if (i > 7 && (i - 8) % 3 == 0)
                            {
                                gamemap[f] = new entry();
                                gamemap[f].size = inputs[i];
                            }
                            if (i > 7 && (i - 8) % 3 == 1)
                                gamemap[f].owner = inputs[i];
                            if (i > 7 && (i - 8) % 3 == 2)
                                gamemap[f].dormant = inputs[i];
                        }

                        lastScore = inputs[3];

                        float[] output = neuralNetwork.FeedForward(inputs);

                        Console.WriteLine("!! > " + output[0] + "  " + output[1]);
                        if (output[0] > 0 && output[0] < 1)
                        {
                            sender.Send(Encoding.ASCII.GetBytes("WAIT"));
                            Console.WriteLine("WAIT");
                            neuralNetwork.fitness -= 0.01f;
                        }
                        else if (output[0] < 0 && output[0] > -1 && (int)Math.Round(output[1]) >= 0 && (int)Math.Round(output[1]) <= 34 && gamemap[(int)Math.Round(output[1])].owner != -100)
                        {
                            sender.Send(Encoding.ASCII.GetBytes("COMPLETE " + Math.Round(output[1])));
                            Console.WriteLine("COMPLETE " + Math.Round(output[1]));
                        }
                        else
                        {
                            sender.Send(Encoding.ASCII.GetBytes("XX " + Math.Round(output[1])));
                            Console.WriteLine("XX " + Math.Round(output[1]));

                            neuralNetwork.fitness -= 10f;
                        }
                        neuralNetwork.fitness += 0.1f;
                        buffer = new List<byte>();
                    }


                }
                catch (Exception ex)
                {
                    ex = ex;
                    Console.WriteLine("!!" + ex.Message);
                }
            }
        }


        private void P_OutputDataReceived(object sender, DataReceivedEventArgs e)
        {
            if (e.Data == null)
                return;

            //throw new NotImplementedException();
            if (e.Data.StartsWith(" > "))
                Console.WriteLine(" -> " + e.Data);

            if(e.Data.StartsWith("Res0"))
            {
                Console.WriteLine(" -> " + e.Data);
                var m = Regex.Match(e.Data, @"Res0: ([^ ]*) - Res1: ([^ ]*)");
                //int val1 = int.Parse(m.Groups[1].Value);
                //if (val1 > 18) val1 += 40; 
                
                neuralNetwork.fitness += lastScore;
                oSignalEventComplete.Set();
                retry = false;
                Remove();
            }

            if(e.Data.StartsWith("WARNING: All illegal access operations will be denied in a future release"))
                oSignalEvent.Set();

        }

        public void Remove()
        {
            if (p != null)
                p.Kill();
        }
    }

    class Runner
    {
        int[] layers = new int[3] { 119, 40, 2 };
        string[] activation = new string[2] { "leakyrelu", "leakyrelu" };


        public float MutationChance = 0.05f;
        public float MutationStrength = 0.5f;
        public int populationSize = 100;
        public int timeframe = 15;

        public int evo = 0;

        public float best = -100f;

        public List<NeuralNetwork> networks;
        private List<Bot> bots;
        ManualResetEvent oSignalEvent = new ManualResetEvent(false);


        public void Run()
        {
            if (populationSize % 2 != 0)
                populationSize++;

            InitNetworks();



            //Timer timer = new Timer(new TimerCallback(Evolute), null, 1000, 5000);
            while(true)
            {
                Evolute();
                oSignalEvent.WaitOne();
                oSignalEvent.Reset();
            }
        }

        public void InitNetworks()
        {
            networks = new List<NeuralNetwork>();

            NeuralNetwork initnet = new NeuralNetwork(layers, activation);
            for (int j = 0; j < 5; j++)
            {
                Console.WriteLine(" back prop " + j);
                for (int n = 0; n < 1; n++)
                {
                    float[] input = Enumerable.Repeat(-1f, 119).ToArray();

                    input[8 + j] = 3;
                    input[8 + j + 1] = 0;
                    input[8 + j + 2] = 0;


                    float[] output = { -1f, (float)j };

                    initnet.BackPropagate(input, output);
                }
            }

            for (int i = 0; i < populationSize; i++)
            {
                var net = initnet.copy(new NeuralNetwork(layers, activation));
                net.Mutate((int)(1 / MutationChance), MutationStrength);
                networks.Add(net);
            }
        }

        int portBase = 63434;
        int portAdd = 0;
        public void Evolute()
        {
            if (bots != null)
            {
                for (int i = 0; i < bots.Count; i++)
                {
                    bots[i].Remove();
                }

                SortNetworks();//this sorts networks and mutates them
            }

            bots = new List<Bot>();

            for (int i = 0; i < populationSize; i++)
            {

                Bot bot = new Bot(networks[i], portBase+portAdd, " ---------------- Evo: " + evo + " - Pop: " + i + " | best: " + best + " ----------------");
                portAdd++;
                if (portAdd > 1000)
                    portAdd = 0;
                bots.Add(bot);

                bot.RunT();

                Console.WriteLine(" ->> " + networks[i].fitness);
                if (networks[i].fitness > best)
                    best = networks[i].fitness;
            }

            oSignalEvent.Set();
        }

        public void SortNetworks()
        {
            evo++;
            networks.Sort(delegate (NeuralNetwork c1, NeuralNetwork c2) { return c1.fitness.CompareTo(c2.fitness); });
            networks[populationSize - 1].Save("Save.txt");//saves networks weights and biases to file, to preserve network performance
            for (int i = 0; i < populationSize / 2; i++)
            {
                networks[i] = networks[i + populationSize / 2].copy(new NeuralNetwork(layers, activation));
                networks[i].Mutate((int)(1 / MutationChance * Math.Max(1, 10 - evo)), MutationStrength * Math.Max(1, 10 - evo));
            }

            networks[0] = networks[populationSize - 1].copy(new NeuralNetwork(layers, activation));
            networks[0].Mutate((int)(1 / MutationChance*2), MutationStrength*2);

            networks[1] = networks[populationSize - 1].copy(new NeuralNetwork(layers, activation));
            networks[1].Mutate((int)(1 / MutationChance*3), MutationStrength*3);

            networks[2] = networks[populationSize - 1].copy(new NeuralNetwork(layers, activation));
            networks[2].Mutate((int)(1 / MutationChance * 4), MutationStrength * 4);

            networks[3] = networks[populationSize - 1].copy(new NeuralNetwork(layers, activation));
            networks[3].Mutate((int)(1 / MutationChance * 4), MutationStrength * 4);

            networks[4] = networks[populationSize - 1].copy(new NeuralNetwork(layers, activation));
            networks[4].Mutate((int)(1 / MutationChance * 4), MutationStrength * 4);
        }
    }
}
