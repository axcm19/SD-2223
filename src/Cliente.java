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

        // após registo ou autenticação

        boolean sair = false;
        while(!sair){
            System.out.print("--- PRO-SCOOTER v.1 ---\n"
                    + "\n"
                    + "Escolha uma das opções\n"
                    + "1) Ver trotinetes livres.\n"
                    + "2) Reservar trotinete.\n"
                    + "3) Minhas recompensas.\n"
                    + "4) Opções de notificações.\n"
                    + "\n"
                    + "0) SAIR.\n"
                    + "\n"
                    + "Opção ---> ");
            String opcao_selecionada = input.readLine();

            // sair do programa --> a tag é 100
            switch(opcao_selecionada) {
                case "0":
                    m.send(100, username, new byte[0]);
                    sair = true;
                    break;

                case "1":
                    while(true) {
                        System.out.print("--- Listar trotinetes livres ---\n"
                                + "\n"
                                + "Introduza em qual localização quer procurar: ");
                        System.out.print("\nCoordenada X ---> ");
                        String coor_x = input.readLine();
                        System.out.print("\nCoordenada Y ---> ");
                        String coor_y = input.readLine();

                        System.out.print("\n Introduza a distância de procura: ");
                        System.out.print("\nDistância ---> ");
                        String distancia_procura = input.readLine();

                        try {
                            Mapa.Posicao pos = new Mapa.Posicao();
                            pos.coord_x = Integer.parseInt(coor_x);
                            pos.coord_y = Integer.parseInt(coor_y);
                            m.send(2, username, String.format("%d %d %s", pos.coord_x, pos.coord_y, distancia_procura).getBytes());

                            String response = new String(m.receive(2));
                            System.out.println("\n" + response + "\n");

                            break;
                        }
                        catch (IllegalStateException e){
                            System.out.println("\nErro");
                        }
                    }

                case "2":
                    while(true){
                        System.out.print("--- Reservar uma trotinete ---\n"
                                + "\n"
                                + "Introduza em qual localização quer reservar: ");
                        System.out.print("\nCoordenada X ---> ");
                        String coor_x = input.readLine();
                        System.out.print("\nCoordenada Y ---> ");
                        String coor_y = input.readLine();
                        try {
                            Mapa.Posicao pos = new Mapa.Posicao();
                            pos.coord_x = Integer.parseInt(coor_x);
                            pos.coord_y = Integer.parseInt(coor_y);
                            m.send(3, username, String.format("%d %d", pos.coord_x, pos.coord_y).getBytes());

                            String response = new String(m.receive(3));
                            System.out.println("\n" + response + "\n");

                            break;
                        }
                        catch (IllegalStateException e){
                            System.out.println("\nErro");
                        }
                    }

            }
        }


        m.close();

    }

}


