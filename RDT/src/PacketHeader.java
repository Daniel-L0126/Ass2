import java.util.zip.CRC32;

public class PacketHeader {
    public int type;
    public int seq_num;
    public int length; 
    public long checksum;

    public PacketHeader(int type, int seq_num, int length, int checksum){
        this.type = type;
        this.seq_num = seq_num;
        this.length = length;
        this.checksum = checksum;
    }
        
    public void compute_checksum(byte[] data) {
        CRC32 crc32 = new CRC32();
        
        crc32.update(data);
        
        // Get the CRC32 checksum value as a long
        checksum = crc32.getValue();

    }

}

