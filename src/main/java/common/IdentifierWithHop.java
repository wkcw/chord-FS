package common;

import net.grpc.chord.Identifier;

public class IdentifierWithHop {

    public Identifier identifier;
    public int hop;

    public IdentifierWithHop(Identifier identifier, int hop){
        this.identifier = identifier;
        this.hop = hop;
    }




}
