package miscellaneous.pw_hash;

public class Demo1 {

    // we can store some extra info specific to bob to concatenate onto his password (a salt)
    // if bob's salt is unique-enough, then the concatenation of password + salt will generate a hash digest
    // that no one else has
    public static void main(String [] args) {
        String userName = "bob";
        String password = "123456";
        String passwordHash = HashUtils.getCryptoHash(password, "SHA-256");

        System.out.println("Password is " + password);
        System.out.println("Hash of password size: " + passwordHash.length() + " , printed as string: " + passwordHash);
    }
}
