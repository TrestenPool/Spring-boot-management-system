package miscellaneous.pw_hash;

public class Demo3 {
    public static void main(String [] args) {
        // bob and sue both create accounts on our fake server
        Server.getInstance().registerUser("bob",
                HashUtils.getCryptoHash("123456", "SHA-256"));
        Server.getInstance().registerUser("sue",
                HashUtils.getCryptoHash("123456", "SHA-256"));

        // bob tries to log in
        Server.getInstance().tryLogin("bob",
                HashUtils.getCryptoHash("123456", "SHA-256"));
        // sue tries to log in with SAME pw hash
        Server.getInstance().tryLogin("sue",
                HashUtils.getCryptoHash("123456", "SHA-256"));
    }
}
