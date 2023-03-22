public class UnreliableSocket{
    int ipAddress, port;

    // This method bind the assigned ip address and port to a socket
    public boolean bind(int ipAddress, int port){
        this.ipAddress = ipAddress; 
        this.port = port;
        return true;
    }

    public boolean recvfrom(){
        
        return true;
    }
}
