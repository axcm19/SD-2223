/*
Classe que serve para representar o mapa onde as trotinetes podem circular.
É representado como uma matriz NxN onde N é um numero inteiro.
O mapa tem uma lista de posições que são compostas por um par(x, y) onde x, y são inteiros.
 */

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Math.abs;

public class Mapa extends Recompensa{

    //------------------------------------------------------------------------------------------------------------------

    public static class Posicao {

        public int coord_x;
        public int coord_y;

        public  Posicao(){
            this.coord_x = 0;
            this.coord_y = 0;
        }

        public Posicao(int x,int y){
            this.coord_x = x;
            this.coord_y = y;
        }

        public Posicao(Posicao p){
            this.coord_x = p.coord_x;
            this.coord_y = p.coord_y;
        }
        //faltam métodos


        public int getCoord_x() {
            return coord_x;
        }

        public void setCoord_x(int coord_x) {
            this.coord_x = coord_x;
        }

        public int getCoord_y() {
            return coord_y;
        }

        public void setCoord_y(int coord_y) {
            this.coord_y = coord_y;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    // tamanho do mapa = N*N
    private int N = 20;

    public HashMap<Integer,Trotinete> reserva;
    private HashMap<Integer, Trotinete> trotinetes; // <ID, Trotinete>
    private HashMap<Integer, List<Posicao>> locais; // <Numero do sector, Lista de posiçoes do sector>    alterar o formato
    public ReentrantLock lock_geral = new ReentrantLock();
    public ReentrantReadWriteLock lock_mapa = new ReentrantReadWriteLock();

    //inicia o mapa    nao sei se esta certo
    public Mapa iniciaMapa(){
        Posicao trotinete = new Posicao();
        int x = -1;
        int y = -1;
        int i = 1;
        List<Posicao> posicoes = new ArrayList<>();
        this.locais = new HashMap<>();
        /*while(i <= nsector){
            while(x<tamanhosector){
                x = x+2;
                trotinete.coord_x = x;
                while(y<tamanhosector){
                    y = y+2;
                    trotinete.coord_y = y;
                    posicoes.add(trotinete);
                }
            }
            locais.put(i,posicoes);
            i++;
        }*/


        // inicializa trotinetes

        Trotinete t1 = new Trotinete(1, true, 5,3 );
        Trotinete t2 = new Trotinete(2, true, 1,2 );
        Trotinete t3 = new Trotinete(3, true, 2,19 );
        Trotinete t4 = new Trotinete(4, false, 15,9 );
        Trotinete t5 = new Trotinete(5, true, 16,7 );
        Trotinete t6 = new Trotinete(6, true, 4,20 );
        Trotinete t7 = new Trotinete(7, true, 0,2 );
        Trotinete t8 = new Trotinete(8, false, 0,1 );
        Trotinete t9 = new Trotinete(9, false, 1,0 );
        Trotinete t10 = new Trotinete(10, true, 4,20 );
        Trotinete t11 = new Trotinete(11, false, 5,8 );
        Trotinete t12 = new Trotinete(12, false, 3,10 );
        Trotinete t13 = new Trotinete(13, true, 4,2 );
        Trotinete t14 = new Trotinete(14, true, 3,5 );
        Trotinete t15 = new Trotinete(15, true, 2,6 );

        this.reserva = new HashMap<>();
        this.trotinetes = new HashMap<>();
        trotinetes.put(t1.id, t1);
        trotinetes.put(t2.id, t2);
        trotinetes.put(t3.id, t3);
        trotinetes.put(t4.id, t4);
        trotinetes.put(t5.id, t5);
        trotinetes.put(t6.id, t6);
        trotinetes.put(t7.id, t7);
        trotinetes.put(t8.id, t8);
        trotinetes.put(t9.id, t9);
        trotinetes.put(t10.id, t10);
        trotinetes.put(t11.id, t11);
        trotinetes.put(t12.id, t12);
        trotinetes.put(t13.id, t13);
        trotinetes.put(t14.id, t14);
        trotinetes.put(t15.id, t15);

        Mapa mapa = new Mapa();
        mapa.locais = this.locais;
        mapa.trotinetes = this.trotinetes;
        return mapa;
    }

    public Map<Integer, Trotinete> getTrotinetes(){
        Map<Integer, Trotinete> tro = new HashMap<>();
        for(int k : this.trotinetes.keySet()){
            tro.put(k, this.trotinetes.get(k));
        }
        return tro;
    }

    //calcula distancia entre dois pontos
    /*
    public int calculaDistancia(Posicao p1,Posicao p2){
        int dist = abs(p1.coord_x - p2.coord_x) + abs(p1.coord_y - p2.coord_y);
        return dist;
    }
    */

    public boolean comparaPosicoes(Posicao p1,Posicao p2){
        if(p1.coord_x == p2.coord_x)
            if(p1.coord_y == p2.coord_y) return true;
        return false;
    }

    //pegar posiçao do user e tirar do mapa
    //mudar para os hashmap
    public int reservaTrotinete(int Id){
        Random rand = new Random();
        Trotinete t = new Trotinete();
        for(int ID : trotinetes.keySet()){
            t = trotinetes.get(ID);
            if(Id == t.id){
                t.ocupada = false;
                int codigo = 0;
                boolean flag = true;
                while(flag) {
                    codigo = rand.nextInt(100);
                    if(reserva.containsKey(codigo)) ;
                    else {
                        reserva.put(codigo, t);
                        flag = false;
                    }
                }
                return codigo;
            }
        }
        return -1;
    }

    public int desloca(int codigo,Posicao p){
        Trotinete t = reserva.get(codigo);
        for (int ID : trotinetes.keySet()){
            Trotinete aux = trotinetes.get(ID);
            if(t.id == aux.id){
                reserva.remove(codigo);
                aux.ocupada = true;
                aux.pos = p;
                return 1;
            }
        }
        return -1;
    }

    public int verificaReserva(int codigo){
        if(reserva.containsKey(codigo)) return 1;
        else return -1;
    }

    //faz a listagem de todas as trotinetes livres
    public String lista_trotinetes(Posicao pos, int distancia_procura){
        String livres;
        StringBuilder sb = new StringBuilder();
        sb.append("\nTrotinetes livres: ");

        for(int ID : trotinetes.keySet()){
            Trotinete t = trotinetes.get(ID);
            Posicao t_pos = new Posicao(t.pos.coord_x, t.pos.coord_y);  // REDUNDANTE!! Nao é necessario criar nova posição mas cria-se na mesma
            int dist = calculaDistancia(pos, t_pos);

            if(dist <= distancia_procura && t.ocupada == true){
                sb.append("\nID: " + t.id + " ; " + "(" + t.pos.coord_x + ", " + t.pos.coord_y + ");");
            }
        }
        livres = String.valueOf(sb);
        return livres;
    }

    public String lista_recompensas(){
        List<Trotinete> trotinetes_lista = new ArrayList<>();
        List<Recompensa> recompensas_lista = new ArrayList<>();
        String recompensas  = "";
        StringBuilder sb = new StringBuilder();
        sb.append("\nLista Recompensas: \n");

        // cria lista de trotinetes a partir do Map
        for(int id_trot : this.trotinetes.keySet()){
            if(this.trotinetes.get(id_trot).ocupada) trotinetes_lista.add(this.trotinetes.get(id_trot)); // so trotinetes LIVRES
        }

        recompensas_lista = geraRecompensas(geraLocaisA(trotinetes_lista));

        for(Recompensa r : recompensas_lista)
            sb.append(r.toString());


        recompensas = String.valueOf(sb);

        return recompensas;
    }

    public List<Recompensa> geraRecompensas(List<Mapa.Posicao> A) {
        // PARA O LOCAL A:
        // iteracao pela lista de trotinetes -> ver as livres
        // das livres -> uma a uma ver se tem trotinetes dentro do raio
        // esta no raio : distancia de manhaten da coord da Trotinetes com pos A e depois B

        // PARA O LOCAL B:
        // gerar Pos random e ver se tem alguma dentro do raio

        List<Recompensa> recompensas = new ArrayList<>();

        for(Mapa.Posicao p : A) {

            Posicao B = geraLocalB((List<Trotinete>) trotinetes.values());
            Integer valor = formulaValor(p,B);

            Recompensa r = new Recompensa(p,B,valor);
            recompensas.add(r);
        }

        return recompensas;
    }



}
