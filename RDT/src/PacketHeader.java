public class PacketHeader {
    int type;
    int seq_num;
    int length; 
    int checksum;

    public PacketHeader(int type, int seq_num, int length, int checksum){
        this.type = type;
        this.seq_num = seq_num;
        this.length = length;
        this.checksum = checksum;
    }
}
