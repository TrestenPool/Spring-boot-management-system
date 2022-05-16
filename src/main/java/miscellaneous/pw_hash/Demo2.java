package miscellaneous.pw_hash;

public class Demo2 {

    // this is ok, SHA-256 is a tough hashing algorithm (as of last year, no credible collisions found)
    // BUT: what if 2 different users have the same password???
    public static void main(String [] args) {
        String user1 = "bob";
        String password1 = "123456";
        // bob has his own randomly generated salt
        //String salt1 = "bobs salt";

        String passwordHash = HashUtils.getCryptoHash(password1 + "bob2105551212", "SHA-256");
        System.out.println("Bob's Password is " + password1);
        System.out.println("Bob's Hash of password size: " + passwordHash.length() + " , printed as string: " + passwordHash);

        passwordHash = HashUtils.getCryptoHash(password1 + "sue2104444444", "SHA-256");
        System.out.println("Sue's Password is " + password1);
        System.out.println("Sue's Hash of password size: " + passwordHash.length() + " , printed as string: " + passwordHash);

    }
}
