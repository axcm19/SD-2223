/*
Classe que implementa a conexão que é estabelecida entre cliente e servidor.
Por enquanto só serve para um unico cliente.
 */

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;


public class Connection implements AutoCloseable {

        //private final Socket socket;
        private final DataInputStream is;
        private final DataOutputStream os;
        private final ReentrantLock sendLock = new ReentrantLock();   // lock para operações de envio de frames
        private final ReentrantLock receiveLock = new ReentrantLock();    // lock para operações de receção de frames

        public Connection(Socket socket) throws IOException {
            //this.socket = socket;
            this.is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }

        public void send(Frame frame) throws IOException {
            try{
                sendLock.lock();
                this.os.writeInt(frame.tag); // lê a tag da frame
                this.os.writeUTF(frame.username);
                this.os.writeInt(frame.data.length); // precisa de saber o tamanho Int do array de bytes
                this.os.write(frame.data);
                this.os.flush();
            }
            finally {
                sendLock.unlock();
            }
        }

        public void send(int tag, String username, byte[] data) throws IOException {
            // vai criar uma nova frame e enviar
            this.send(new Frame(tag, username, data));
        }

        public Frame receive() throws IOException {
            // a  leitura de dados de um socket é como uma escrita
            // não se deve usar readLock neste contexto
            // tem de ter um lock exclusivo pois nao pode ser executado concorrentemente

            int tag;
            String username;    // ---> 3 componentes de uma frame
            byte[] data;

            try {
                receiveLock.lock();
                tag = this.is.readInt();    // lê a tag
                username = this.is.readUTF();    // lê o username
                int size = this.is.readInt();   // lê o tamanho dos dados que recebe

                data = new byte[size];  // cria um array de bytes de tamanho "size" onde vai colocar a mensagem

                this.is.readFully(data); // só retorna quando conseguir ler a mensagem completa
            }
            finally {
                receiveLock.unlock();
            }
            // finalmente constroi uma nova frame com os dados obtidos
            return new Frame(tag, username, data);
        }

        public void close() throws IOException {
            this.is.close();
            this.os.close();
        }

}
