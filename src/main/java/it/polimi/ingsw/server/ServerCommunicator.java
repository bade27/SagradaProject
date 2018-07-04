package it.polimi.ingsw.server;

public interface ServerCommunicator {

    public void setAdapter(ServerModelAdapter sma);
    public void setMatchHandler(MatchHandler match);
    public void close();

}
