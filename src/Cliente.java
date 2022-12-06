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
        // falta criar um desmultiplexer
        Connection c = new Connection(s);

        Scanner input = new Scanner(System.in);

        String username = null;
        //String password = null;

        /*
        // send requests
        c.send(username.getBytes());   //p1
        c.send(password.getBytes());   //p2

        // get replies
        byte[] b1 = c.receive();
        byte[] b2 = c.receive();
        System.out.println("Server Reply: " + new String(b1));
        System.out.println("Server Reply: " + new String(b2));
        */


        while (username == null) {
            System.out.println();
            System.out.println("#################################################################################");
            System.out.println();
            System.out.print("---- TROTINETE MANAGER V.1 ----\n"
                    + "\n"
                    + "1) Iniciar sessão\n"
                    + "2) Registar nova conta\n"
                    + "\n"
                    + "Escreva o valor corresponde à opção desejada: ");

            String option = input.nextLine();

            if (option.equals("1")) {
                System.out.println();
                System.out.println("#################################################################################");
                System.out.println();
                System.out.print("---- INICIAR SESSÃO ----\n"
                        + "\n"
                        + "Introduza o seu nome de utilizador: ");

                String write_username = input.nextLine();

                System.out.print("Introduza a sua palavra-passe: ");

                String write_password = input.nextLine();


                c.send(write_username.getBytes());
                c.send(write_password.getBytes());

                byte[] b1 = c.receive();
                byte[] b2 = c.receive();

                System.out.println();
                System.out.println("Sessão iniciada");
                System.out.println("Server Reply - Username: " + new String(b1));
                System.out.println("Server Reply - Password: " + new String(b2));
                System.out.println();

                /*
                m.send(0, email, password.getBytes());
                String response = new String(m.receive(0));
                if (!response.startsWith("Erro")) {
                    username = email;
                }

                System.out.println("\n" + response + "\n");

                */

            } else if (option.equals("2")) {
                System.out.println();
                System.out.println("#################################################################################");
                System.out.println();
                System.out.print("---- REGISTAR NOVA CONTA ----\n"
                        + "\n"
                        + "Introduza o seu nome de utilizador: ");

                String write_username = input.nextLine();
                System.out.print("Introduza a sua palavra-passe: ");
                String write_password = input.nextLine();

                c.send(write_username.getBytes());
                c.send(write_password.getBytes());

                byte[] b1 = c.receive();
                byte[] b2 = c.receive();

                System.out.println();
                System.out.println("Adicionada nova conta");
                System.out.println("Server Reply - Username: " + new String(b1));
                System.out.println("Server Reply - Password: " + new String(b2));
                System.out.println();

                /*
                m.send(1, email, password.getBytes());
                String response = new String(m.receive(1));
                if (!response.startsWith("Erro")) {
                    username = email;
                }

                System.out.println("\n" + response + "\n");

                */



            }
        }

        c.close();

    }

}


