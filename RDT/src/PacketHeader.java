public class PacketHeader {
    public int type;
    public int seq_num;
    public int length; 
    public int checksum;

    public PacketHeader(int type, int seq_num, int length, int checksum){
        this.type = type;
        this.seq_num = seq_num;
        this.length = length;
        this.checksum = checksum;
    }
}
