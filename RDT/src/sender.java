import java.net.SocketException;

public class sender {

    public static void main(String[] args) throws Exception {
        RDTSocket receiver = new RDTSocket("localhost", 4040, 0, 5);
        receiver.connect();
    }
}
