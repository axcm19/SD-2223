import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
