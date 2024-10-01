public class Frame {

    public final int tag;           // ---> a tag da frame para identificação da mesma
    public final String username;   // ---> o user que a enviou
    public final byte[] data;       // ---> a mensagem contida na frame

    public Frame(int tag, String username, byte[] data) {
        this.tag = tag;
        this.username = username;
        this.data = data;
    }
}
