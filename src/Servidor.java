import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.time.*;

public class Servidor {

    final static int WORKERS_PER_CONNECTION = 3;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);

        ReentrantLock login_lock = new ReentrantLock();
        HashSet<String> clientes_logados = new HashSet<>(); // set para que não haja clinetes repetidos

        final Contas lista_contas;
        final Mapa locais;

        // criação da lista de contas
        lista_contas = new Contas();

        // iniciação do mapa é feita aqui
        locais = new Mapa();
        locais.iniciaMapa();

        // algumas contas já adicionadas
        lista_contas.addAccount("afonso_m","pass_afonso");
        lista_contas.addAccount("vicente_c","pass_vicente");
        lista_contas.addAccount("jose_f","pass_jose");
        lista_contas.addAccount("pedro_p","pass_pedro");

        //--------------------------------------------------------------------------------------

        while(true) {
            Socket s = ss.accept();
            Connection c = new Connection(s);

            Runnable worker = () -> {
                try (c) {
                    while(true){

                        boolean login_yes = false;
                        Frame frame = c.receive();

                        //--------------------------------------------------------------------------------------

                        // thread com tag 0 é de autenticação
                        if(frame.tag == 0){
                            System.out.println("Tentativa de autenticação por cliente");
                            String name = frame.username;

                            // password recebida pelo servidor como um byte array
                            String pass = new String(frame.data);

                            String pass_guardada;
                            lista_contas.lock_Contas.readLock().lock();

                            try{
                                pass_guardada = lista_contas.getPassword(name);
                            } finally {
                                lista_contas.lock_Contas.readLock().unlock();
                            }
                            if (pass_guardada != null) {
                                if (pass_guardada.equals(pass)) {
                                    c.send(0, "", "Sessão iniciada com sucesso!".getBytes());
                                    login_yes = true;
                                    login_lock.lock();
                                    try { clientes_logados.add(frame.username); }
                                    finally { login_lock.unlock(); }
                                }
                                else{
                                    c.send(0, "", "Erro - password incorreta.".getBytes());
                                }
                            }
                            else{
                                c.send(0, "", "Erro - conta inexistente.".getBytes());
                            }
                        }

                        //--------------------------------------------------------------------------------------

                        // thread com tag 1 é de registo
                        else if(frame.tag == 1){
                            System.out.println("Tentativa de registo por cliente");
                            String name = frame.username;

                            // password recebida pelo servidor como um byte array
                            String pass = new String(frame.data);

                            lista_contas.lock_Contas.readLock().lock();

                            try{
                                if(lista_contas.conta_existe(name)){
                                    c.send(1, "", "Erro - username já pertence a uma conta.".getBytes());
                                }
                                else{
                                    lista_contas.addAccount(name, pass);
                                    c.send(frame.tag, "", "Registo concluído".getBytes());
                                    login_yes = true;
                                    login_lock.lock();
                                    try{
                                        clientes_logados.add(frame.username);
                                    } finally {
                                        login_lock.unlock();
                                    }
                                }
                            } finally {
                                lista_contas.lock_Contas.writeLock().unlock();
                            }

                        }

                        //--------------------------------------------------------------------------------------

                        // thread com tag 2 é para listar as trotinetes livres dados uma posição e uma distância
                        else if (frame.tag == 2) {
                            System.out.println("Listar trotinetes livres");

                            // retirar a informação contina na frame
                            String[] info = new String(frame.data).split(" ");
                            Mapa.Posicao pos = new Mapa.Posicao(Integer.parseInt(info[0]), Integer.parseInt(info[1]));
                            int distancia_procura  = Integer.parseInt(info[2]);

                            String trotinetes_livres = "";

                            locais.lock_mapa.readLock().lock();
                            try {
                                trotinetes_livres = locais.lista_trotinetes(pos, distancia_procura);
                                c.send(frame.tag, "", String.valueOf(trotinetes_livres).getBytes());
                            }
                            finally {
                                locais.lock_mapa.readLock().unlock();
                            }
                            System.out.println("Listagem devolvida com sucesso");
                        }

                        //--------------------------------------------------------------------------------------

                        // thread com tag 3 é para reservar uma trotinete dada uma posicao
                        else if (frame.tag == 3) {
                            System.out.println("Reservar uma trotinete");

                            // retirar a informação contina na frame
                            String[] info = new String(frame.data).split(" ");
                            int Id = Integer.parseInt(info[0]);

                            int reserva = -1;
                            String ret;
                            locais.lock_geral.lock();
                            try {
                                reserva = locais.reservaTrotinete(Id);
                                if(reserva >= 0) {
                                    ret = "Reserva de trotinete feita com sucesso. Codigo da reserva " +reserva;
                                    c.send(frame.tag, "", ret.getBytes());
                                }
                                else {
                                    ret = "Reserva não efetuada";
                                    c.send(frame.tag, "", ret.getBytes());
                                }
                            }
                            finally {
                                locais.lock_geral.unlock();
                            }
                            System.out.println("Tentativa de reserva devolvida com sucesso");
                        }

                        //--------------------------------------------------------------------------------------

                        // thread com tag 6 é para deslocar uma trotinete dado o id da trotinete e a posicao
                        else if (frame.tag == 6) {
                            System.out.println("Deslocar uma trotinete");

                            // retirar a informação contina na frame
                            String[] info = new String(frame.data).split(" ");
                            int codigo = Integer.parseInt(info[0]);
                            if(locais.verificaReserva(codigo)==1){
                                Mapa.Posicao pos = new Mapa.Posicao(Integer.parseInt(info[1]), Integer.parseInt(info[2]));

                                int ret = -1;
                                String ret_S;

                                locais.lock_geral.lock();
                                try {
                                    ret = locais.desloca(codigo, pos);
                                    if (ret >= 0) {
                                        ret_S = "Deslocaçao de trotinete feita com sucesso";
                                        c.send(frame.tag, "", String.valueOf(ret_S).getBytes());
                                    } else {
                                        ret_S = "Deslocamento não efetuada";
                                        c.send(frame.tag, "", String.valueOf(ret_S).getBytes());
                                    }
                                } finally {
                                    locais.lock_geral.unlock();
                                }
                                System.out.println("Tentativa de deslocamento devolvida com sucesso");
                            }
                            else{
                                String ret_S;
                                ret_S = "Erro: codigo invalido";
                                c.send(frame.tag, "", String.valueOf(ret_S).getBytes());
                                System.out.println("Tentativa de deslocamento devolvida com sucesso");
                            }
                        }

                        //--------------------------------------------------------------------------------------

                        // thread com tag 4 é para listar as recompensas naquele preciso momento
                        else if (frame.tag == 4) {
                            System.out.println("Listar recompensas");

                            // retirar a informação contina na frame
                            String info = new String(frame.data);

                            String recompensas_atuais = "";

                            locais.lock_mapa.readLock().lock();
                            try {
                                if(info.equals("Listar recompensas")) {
                                    recompensas_atuais = locais.lista_recompensas();
                                    c.send(frame.tag, "", recompensas_atuais.getBytes());
                                }
                                else{
                                    recompensas_atuais = "Erro inesperado";
                                    c.send(frame.tag, "", recompensas_atuais.getBytes());
                                }
                            }
                            finally {
                                locais.lock_mapa.readLock().unlock();
                            }
                            System.out.println("Listagem devolvida com sucesso");

                        }



                        //--------------------------------------------------------------------------------------

                        // thread com tag 100 é para sair do programa
                        else if (frame.tag == 100) {

                            login_lock.lock();
                            try {
                                clientes_logados.remove(frame.username);
                            }
                            finally {
                                login_lock.unlock();
                            }
                        }

                        //--------------------------------------------------------------------------------------


                        if(login_yes){

                        }


                    }
                } catch (Exception ignored) { }
            };

            for (int i = 0; i < WORKERS_PER_CONNECTION; ++i) {
                new Thread(worker).start();
            }
        }
    }

}
