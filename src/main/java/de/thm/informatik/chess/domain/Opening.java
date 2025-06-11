package de.thm.informatik.chess.domain;

import java.util.List;

import com.github.bhlangonijr.chesslib.move.Move;

public class Opening {
    private String ecoCode;
    private String name;
    private List<String> moves;

    public Opening(String ecoCode, String name, List<String> moves){
        this.ecoCode = ecoCode;
        this.name = name;
        this.moves = moves;
    }

    public boolean matches(List<Move> playedMoves){
        if(playedMoves.size() < moves.size()){
            return false;
        }

        for(int i = 0; i < moves.size(); i++){
            Move move = playedMoves.get(i);
            String from = move.getFrom().name().toLowerCase();
            String to = move.getTo().name().toLowerCase();
            String promo = move.getPromotion() != null ? move.getPromotion().getSanSymbol().toLowerCase() : "";
            String moveUci = from + to + promo;

            if(!moveUci.equalsIgnoreCase(moves.get(i))){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString(){
        return String.join(" - ", ecoCode, name);
    }

    public String getEcoCode(){
        return ecoCode;
    }

    public String getName(){
        return name;
    }

    public List<String> getMoves(){
        return moves;
    }
}
