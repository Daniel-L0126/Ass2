import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RDTSocket extends UnreliableSocket {
    private final String serverHostname;
    private int seq;
    private ipPort host;
    private ArrayList<Byte> receivedData;
    private int windowSize;
    private Byte[][] bufferedData;

    public RDTSocket(String serverHostname, int port, double lossProbability, int windowSize) throws SocketException {
        super(port, lossProbability);
        this.serverHostname = serverHostname;
        this.windowSize = windowSize;
    }
    protected class ipPort{
        InetAddress ip;
        int port;
        public ipPort(InetAddress ip, int port){
            this.ip = ip;
            this.port = port;
        }
    }
    
    public ipPort accept() throws Exception {    
        byte[] receiveData = new byte[20];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        int type = ByteBuffer.wrap(Arrays.copyOfRange(receiveData, 0, 3)).getInt();
        if(type == 0) {
            this.seq = ByteBuffer.wrap(Arrays.copyOfRange(receiveData, 4,7)).getInt();
            PacketHeader ack = new PacketHeader(3, seq, 0, 0);
            send(ack.parseByte());
            this.host = new ipPort(receivePacket.getAddress(), receivePacket.getPort());
            return host;
        }
        return null;
    }

    public void connect() throws Exception { 
        Random r = new Random();
        this.seq = r.nextInt(100);
        PacketHeader ph = new PacketHeader(0, r.nextInt(100), 0, 0);
        send(ph.parseByte());
        recv();
    }

    public void send(byte[] data) throws Exception { 
        int packetAmount = (data.length % 1452);
        byte[] packet = new byte[1472];
        for(int i = 0; i < packetAmount; i++){
            seq++;
            PacketHeader ph = new PacketHeader(2, seq, 1472, PacketHeader.compute_checksum(data));
            byte[] header = ph.parseByte();
            for(int j = (0 + i * 1452); j < (1472 + i * 1452); j++){
                int index = 0;
                if(index < 20) {
                    packet[index] = header[index];
                } else {
                    packet[index] = data[index - 20];
                }
                index++;
                
                sendto(packet, host.ip, host.port);
            }
        }
    }

    public void recv() throws Exception { 
        byte[] dataWithHeader = recvfrom();
        byte[] dataWithoutHeader = Arrays.copyOfRange(dataWithHeader, 20, dataWithHeader.length - 1);
        long checksum = ByteBuffer.wrap(Arrays.copyOfRange(dataWithHeader, 12, 19)).getLong();
        if(PacketHeader.verify_packet(dataWithoutHeader, checksum)) {
            
        }
    }
}
