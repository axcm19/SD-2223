import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {

    private Connection conn = null;
    private final Lock lock_demu = new ReentrantLock();
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
        });
    }

    public void send(Frame frame) throws IOException {
        conn.send(frame);
    }

    public void send(int tag, String username, byte[] data) throws IOException {
        conn.send(tag, username, data);
    }

    public byte[] receive(int tag) throws IOException, InterruptedException {
        lock_demu.lock();
        try{
            Entry e = map.get(tag);
            if (e == null) {
                e = new Entry();
                map.put(tag, e);
            }
            while(e.queue.isEmpty() && exception != null){
                e.cond.await();
            }
            if(!e.queue.isEmpty()){
                return e.queue.poll();
            }
            else{
                throw exception;
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
