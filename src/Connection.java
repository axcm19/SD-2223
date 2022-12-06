/*
Classe que implementa a conexão que é estabelecida entre cliente e servidor.
Por enquanto só serve para um unico cliente.
 */

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;


public class Connection implements AutoCloseable {

        private final Socket socket;
        private final DataInputStream is;
        private final DataOutputStream os;
        private ReentrantLock sendLock = new ReentrantLock();
        private ReentrantLock receiveLock = new ReentrantLock();

        public Connection(Socket socket) throws IOException {
            this.socket = socket;
            this.is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }

        public void send(byte[] data) throws IOException {
            try{
                this.sendLock.lock();
                this.os.writeInt(data.length); // precisa de saber o tamanho Int do array de bytes
                this.os.write(data);
                this.os.flush();
            }
            finally {
                this.sendLock.unlock();
            }
        }

        public byte[] receive() throws IOException {
            // a  leitura de dados de um socket é como uma escrita
            // não se deve usar readLock neste contexto
            // tem de ter um lock exclusivo pois nao pode ser executado concorrentemente

            try {
                this.receiveLock.lock();
                int size = this.is.readInt();
                byte[] data = new byte[size];
                this.is.readFully(data); // só retorna quando conseguir ler a mensagem completa
                return data;
            }
            finally {
                this.receiveLock.unlock();
            }
        }

        public void close() throws IOException {
            this.socket.close();
        }

}
