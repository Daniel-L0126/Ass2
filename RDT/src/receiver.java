public class receiver {
    public static void main(String[] args) throws Exception {
        RDTSocket receiver = new RDTSocket("localhost", 20, 0, 5);
        receiver.accept();
    }
}
