import java.net.Socket;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cliente {

    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12345);

        Demultiplexer m = new Demultiplexer(new Connection(s));
        m.start();

        Thread[] threads = {

                new Thread(() -> {
                    try  {
                        // send request
                        m.send(1, "USER_1","Ola".getBytes());
                        Thread.sleep(100);
                        // get reply
                        byte[] data = m.receive(1);
                        System.out.println("(1) Reply: " + new String(data));
                    }  catch (Exception ignored) {}
                }),

                new Thread(() -> {
                    try  {
                        // send request
                        m.send(3, "USER_2","Hello".getBytes());
                        Thread.sleep(100);
                        // get reply
                        byte[] data = m.receive(3);
                        System.out.println("(2) Reply: " + new String(data));
                    }  catch (Exception ignored) {}
                }),

                new Thread(() -> {
                    try  {
                        // One-way
                        m.send(0, "USER_3", ":-p".getBytes());
                    }  catch (Exception ignored) {}
                }),

                new Thread(() -> {
                    try  {
                        // Get stream of messages until empty msg
                        m.send(2, "USER_4", "ABCDE".getBytes());
                        for (;;) {
                            byte[] data = m.receive(2);
                            if (data.length == 0)
                                break;
                            System.out.println("(4) From stream: " + new String(data));
                        }
                    } catch (Exception ignored) {}
                }),

                new Thread(() -> {
                    try  {
                        // Get stream of messages until empty msg
                        m.send(4, "USER_5", "123".getBytes());
                        for (;;) {
                            byte[] data = m.receive(4);
                            if (data.length == 0)
                                break;
                            System.out.println("(5) From stream: " + new String(data));
                        }
                    } catch (Exception ignored) {}
                })

        };

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();
        m.close();

    }

}


