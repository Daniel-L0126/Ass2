import java.net.*;

public class UnreliableSocket {
    public DatagramSocket socket;
    private double lossProbability;
    private double delayProbability;
    private int delayMillis = 200;
    private double corruptProbability;


    public UnreliableSocket(int port, double lossProbability) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.lossProbability = lossProbability;
        this.delayProbability = Math.random();
        this.corruptProbability = Math.random();
    }

    public void bind(int port) throws SocketException {
        this.socket.bind(new InetSocketAddress(port));
    }

    public void sendto(byte[] data, InetAddress address, int port) throws Exception {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        this.socket.send(packet);
    }

    public byte[] recvfrom() throws Exception {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(packet);
    
        // Simulate packet loss
        if (Math.random() < this.lossProbability) {
            return null;
        }
    
        // Simulate packet delay
        if (this.delayProbability > 0 && Math.random() < this.delayProbability) {
            Thread.sleep(this.delayMillis);
        }
    
        // Simulate packet corruption
        if (this.corruptProbability > 0 && Math.random() < this.corruptProbability) {
            int corruptIndex = (int) (Math.random() * (packet.getLength() - 20)) + 20;
            buffer[corruptIndex] ^= 0xFF;
        }
    
        return packet.getData();
    }
    

    public void close() {
        this.socket.close();
    }
}
