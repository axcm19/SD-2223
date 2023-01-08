import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import java.util.List;

public class Recompensa {

    private Mapa.Posicao posInicial;
    private Mapa.Posicao posFinal;
    private Integer valor;

    static final Integer DISTANCIA = 2;   // static final ?

    public Recompensa() {
        this.posInicial = new Mapa.Posicao();
        this.posFinal = new Mapa.Posicao();
        this.valor = 0;
    }

    public Recompensa(Mapa.Posicao posInicial, Mapa.Posicao posFinal, Integer valor) {
        this.posInicial = posInicial;
        this.posFinal = posFinal;
        this.valor = valor;
    }

    public Mapa.Posicao getPosInicial() {
        return posInicial;
    }

    public void setPosInicial(Mapa.Posicao posInicial) {
        this.posInicial = posInicial;
    }

    public Mapa.Posicao getPosFinal() {
        return posFinal;
    }

    public void setPosFinal(Mapa.Posicao posFinal) {
        this.posFinal = posFinal;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    /*
        Metodos
     */

    /**
     * metodo que calcula o valor da recompensa - usa formula inventada
     * @param inicio
     * @param fim
     */
    public Integer formulaValor(Mapa.Posicao inicio, Mapa.Posicao fim) {
        int distancia = calculaDistancia(inicio, fim);
        Integer valor;

        // formula inventada
        if(distancia > 10) valor = 100;
        else valor = 50;

        return valor;
    }

    /**
     * metodo calcula distancia manhattan entre 2 pontos
     * @param A
     * @param B
     * @return
     */
    public Integer calculaDistancia(Mapa.Posicao A, Mapa.Posicao B) {

        // distancia Manhattan
        int distance = Math.abs(B.getCoord_x() - A.getCoord_x()) + Math.abs(B.getCoord_y() - A.getCoord_y());

        return distance;
    }



    public Recompensa geraRecompensa(Mapa.Posicao A, Mapa.Posicao B) {
        // PARA O LOCAL A:
        // iteracao pela lista de trotinetes -> ver as livres
        // das livres -> uma a uma ver se tem trotinetes dentro do raio
        // esta no raio : distancia de manhaten da coord da Trotinetes com pos A e depois B

        // PARA O LOCAL B:
        // gerar Pos random e ver se tem alguma dentro do raio

        Integer valor = formulaValor(A,B);
        Recompensa recompensa = new Recompensa(A,B,valor);

        return recompensa;
    }

    /**
     * metodo que devolve a Pos de um possivel local A para a recompensa
     * @param livres   nao esquecer que eh so as trotinetes LIVRES!
     * @return
     */
    public Mapa.Posicao geraLocalA(List<Trotinete> livres) {
        int i = 0, j = 1;
        for(; i < livres.size(); i++) {
            for(; j < livres.size(); j++) {
                if(calculaDistancia(livres.get(i).getPos(),livres.get(j).getPos()) < DISTANCIA) break;
            }
        }
        return livres.get(i).getPos();
    }

    /**
     * metodo q devolve a Pos de um possivel local B para recompensa
     * @param trotinetes
     * @return
     */
    public Mapa.Posicao geraLocalB(List<Trotinete> trotinetes) {
        Mapa.Posicao rand = null;

        while(true) {
            Boolean flag = false; // flag para ver se encontrou um local B
            Random random = new Random();
            rand = new Mapa.Posicao(random.nextInt(20), random.nextInt(20));

            for(Trotinete t : trotinetes) {
                if(calculaDistancia(rand,t.getPos()) < DISTANCIA) {
                    flag = true; // se tiver trotinetes dentro do raio da break e cria novo rand
                    break;
                }
            }

            if(!flag) break;
        }

        return rand;
    }



    @Override
    public String toString() {
        return "Recompensa{" +
                "posInicial=" + "(" + this.posInicial.coord_x + "," +this.posInicial.coord_y + ")" +
                ", posFinal=" + "(" + this.posFinal.coord_x + "," +this.posFinal.coord_y + ")" +
                ", valor=" + this.valor +
                "}";
    }


}

