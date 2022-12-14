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

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        String username = null;

        // AUTENTICAÇÃO
        while(username == null){
            System.out.print("--- PRO-SCOOTER v.1 ---\n"
                    + "\n"
                    + "Escolha uma das opções\n"
                    + "1) Autenticar conta.\n"
                    + "2) Registar nova conta.\n"
                    + "\n"
                    + "Opção ---> ");

            String opcao_selecionada = input.readLine();
            if(opcao_selecionada.equals("1")){
                System.out.print("--- INICIAR SESSÃO ---\n"
                        + "\n"
                        + "Escreva o seu username ---> ");

                String name = input.readLine();
                System.out.print("Escreva a sua password ---> ");
                String pass = input.readLine();

                // thread com tag 0 é de autenticação
                // passamos a password como byte[]
                m.send(0, name, pass.getBytes());
                String resposta = new String(m.receive(0));

                if(!resposta.startsWith("Erro")){
                    username = name;
                }
                System.out.println("\n" + resposta + "\n");

            }

            else if(opcao_selecionada.equals("2")) {
                System.out.print("--- REGISTAR NOVA CONTA ---\n"
                        + "\n"
                        + "Escreva o seu username ---> ");
                String name = input.readLine();
                System.out.print("Escreva a sua password ---> ");
                String pass = input.readLine();

                // thread com tag 1 é de registo
                // passamos a password como byte[]
                m.send(1, name, pass.getBytes());

                String response = new String(m.receive(1));
                if(!response.startsWith("Erro")) {
                    username = name;
                }
                System.out.println("\n" + response + "\n");
            }
        }


        m.close();

    }

}


