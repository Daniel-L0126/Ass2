import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RDTSocket extends UnreliableSocket {
    public final String serverHostname;
    public int seq;
    public ipPort host;
    public ArrayList<byte[]> receivedData;
    public int windowSize;
    public byte[][] receiveWindow;
    public int repeatedSeq = 0;
    public long zeroChecksum = 3523407757l;
    public byte[] lastSendPacket;
    public UnreliableSocket uSocket;

    public RDTSocket(String serverHostname, int port, double lossProbability, int windowSize) throws SocketException, UnknownHostException {
        super(port, lossProbability);
        this.receivedData = new ArrayList<>();
        this.serverHostname = serverHostname;
        this.windowSize = windowSize;
        this.receiveWindow = new byte[windowSize][];
        this.uSocket = new UnreliableSocket(port, lossProbability);
        uSocket.bind(port);
    }

    public class ipPort{
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
            PacketHeader ack = new PacketHeader(3, seq, 0, zeroChecksum);
            send(ack.parseByte());
            this.host = new ipPort(receivePacket.getAddress(), receivePacket.getPort());
            return host;
        }
        System.out.println("Connected.");
        return null;
    }

    public void connect() throws Exception { 
        Random r = new Random();
        this.seq = r.nextInt(100);
        PacketHeader ph = new PacketHeader(0, r.nextInt(100), 0, zeroChecksum);
        uSocket.sendto(ph.parseByte(), host.ip, host.port);
        System.out.println("Connected.");
        recv();
        }

    public void send(byte[] data) throws Exception { 
        byte[] packet = new byte[1472];
        seq++;
        PacketHeader ph = new PacketHeader(2, seq, 1472, PacketHeader.compute_checksum(data));
        byte[] header = ph.parseByte();
        for(int j = 0; j < 1472; j++){
            int index = 0;
            if(index < 20) {
                packet[index] = header[index];
            } else {
                packet[index] = data[index - 20];
            }
            index++;
        }
        this.lastSendPacket = packet;
        uSocket.sendto(packet, host.ip, host.port);
        
    }

    public int recv() throws Exception { 
        byte[] dataWithHeader = recvfrom();
        byte[] dataWithoutHeader = {0};
        if(dataWithHeader.length > 20) dataWithoutHeader = Arrays.copyOfRange(dataWithHeader, 20, dataWithHeader.length - 1);
        int ack = ByteBuffer.wrap(Arrays.copyOfRange(dataWithHeader, 4, 7)).getInt();
        int type = ByteBuffer.wrap(Arrays.copyOfRange(dataWithHeader, 0,3)).getInt();
        long checksum = ByteBuffer.wrap(Arrays.copyOfRange(dataWithHeader, 12, 19)).getLong();

        if(PacketHeader.verify_packet(dataWithoutHeader, checksum)) {
            if(type == 0){
                send(new PacketHeader(3, ack, 0, zeroChecksum).parseByte());
                this.seq = ack;
            } else if (type == 1) { 
                send(new PacketHeader(4, ack, 0, zeroChecksum).parseByte());
            } else if (type == 2) { 
                if (ack == seq) { 
                    seq++;
                    receivedData.add(dataWithoutHeader);
                    send(new PacketHeader(3, seq, 0, zeroChecksum).parseByte());
                }
                else if(ack <= seq + windowSize && ack > seq) {
                    int index = ack - seq - 1;
                    receiveWindow[index] = dataWithoutHeader;
                    send(new PacketHeader(3, seq, 0, zeroChecksum).parseByte());
                } 
            } else if (type == 3) {   
                if(ack == seq + 1) { 
                    return 1;
                } else if (ack == seq) {
                    repeatedSeq += 1;
                    if (repeatedSeq == 3) { 
                        repeatedSeq = 0;
                        return -1;
                    }
                }
            } else if (type == 4) { 
                close();
            }
        }
        return 0;
    }
}
