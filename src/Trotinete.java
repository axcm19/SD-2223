public class Trotinete {

    int id;     // identificador de uma trotinete
    boolean ocupada;    // true = livre ; false = ocupada
    Mapa.Posicao pos;

    public  Trotinete(){
        this.id = 0;
        this.ocupada = true;
        this.pos = new Mapa.Posicao();
    }

    public Trotinete(int id, boolean estado, int c_X, int c_Y){
        this.id = id;
        this.ocupada = estado;
        this.pos = new Mapa.Posicao(c_X, c_Y);
    }

    public Trotinete(Trotinete t){
        this.id = t.id;
        this.ocupada = t.ocupada;
        this.pos = t.pos;
    }

    //faltam métodos


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    public Mapa.Posicao getPos() {
        return pos;
    }

    public void setPos(Mapa.Posicao pos) {
        this.pos = pos;
    }

}

