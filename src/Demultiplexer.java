import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {

    private final Connection conn;
    private final ReentrantLock lock_demu = new ReentrantLock();
    private final Map<Integer, Entry> map = new HashMap<>();
    private IOException exception = null;

    private class Entry {
        int waiters = 0;
        Queue<byte[]> queue = new ArrayDeque<>();
        Condition cond = lock_demu.newCondition();

        public Entry() {}
    }

    public Demultiplexer(Connection conn) {
        this.conn = conn;
    }

    public void start() {
        new Thread(() -> {
            try  {
                while(true){
                    Frame frame = conn.receive();
                    lock_demu.lock();
                    try{
                        Entry e = map.get(frame.tag);
                        if (e == null) {
                            e = new Entry();
                            map.put(frame.tag, e);
                        }
                        e.queue.add(frame.data);
                        e.cond.signal();
                    }
                    finally {
                        lock_demu.unlock();
                    }
                }
            } catch (IOException e) {
                exception = e;
            }
        }).start();
    }

    public void send(Frame frame) throws IOException {
        conn.send(frame);
    }

    public void send(int tag, String username, byte[] data) throws IOException {
        conn.send(tag, username, data);
    }

    public byte[] receive(int tag) throws IOException, InterruptedException {
        lock_demu.lock();
        Entry e;
        try{
            e = map.get(tag);
            if (e == null) {
                e = new Entry();
                map.put(tag, e);
            }
            e.waiters++;
            while(true) {
                if (!e.queue.isEmpty()) {
                    e.waiters--;
                    byte[] resposta = e.queue.poll();
                    if(e.waiters == 0 && e.queue.isEmpty()){
                        map.remove(tag);
                    }
                    return resposta;
                }
                if (exception != null) {
                    throw exception;
                }
                e.cond.await();
            }
        }
        finally {
            lock_demu.unlock();
        }
    }

    public void close() throws IOException {
        conn.close();
    }

}
