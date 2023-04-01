import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class PacketHeader {
    public int type;    // 0 ~ 3 byte in the header
    public int seq_num; // 4 ~ 7 byte in the header
    public int length;  // 8 ~ 11 byte in the header
    public long checksum;// 12 ~ 19 byte in the header

    public PacketHeader(int type, int seq_num, int length, long checksum){
        this.type = type;
        this.seq_num = seq_num;
        this.length = length;
        this.checksum = checksum;
    }

    public byte[] parseByte() {
        byte[] head = new byte[20];

        ByteBuffer bb = ByteBuffer.allocate(4);
        byte[] typeInByte = bb.putInt(type).array();
        byte[] seqInByte = bb.putInt(seq_num).array();
        byte[] lengInByte = bb.putInt(length).array();
        bb = ByteBuffer.allocate(8);
        byte[] checksumInByte = bb.putLong(checksum).array();

        for(int i = 0; i < 20; i++){
            if (i < 4) head[i] = typeInByte[i];
            else if (i >= 4 && i < 8) head[i] = seqInByte[i];
            else if (i >= 8 && i < 12) head[i] = lengInByte[i];
            else if (i >= 12 && i < 20) head[i] = checksumInByte[i];
        }

        return head;
    }
        
    public static long compute_checksum(byte[] data) {

        CRC32 crc32 = new CRC32();
        
        crc32.update(data);

        return crc32.getValue();
    }
    
    public static boolean verify_packet(byte[] data, long checksum) { 
        CRC32 crc32 = new CRC32();

        crc32.update(data);

        if(checksum == crc32.getValue()) {
            return true;
        }

        return false;
    }
}

