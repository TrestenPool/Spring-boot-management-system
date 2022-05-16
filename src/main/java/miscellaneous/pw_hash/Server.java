package miscellaneous.pw_hash;

import java.util.ArrayList;

public class Server {
    private static Server instance = null;

    private ArrayList<Credentials> users;

    private Server() {
        users = new ArrayList<Credentials>();
    }

    public void registerUser(String userName, String hashedPassword) {
        // we do NOT store the hashedPassword
        // instead generated a random salt for the user, hash the hashed pw,
        // and store the username, salt, and salted hashed pw
        String newSalt = "this is not random enough " + users.size();
        String saltedHashedPassword = HashUtils.getCryptoHash(hashedPassword + newSalt, "SHA-256");
        Credentials newCredentials = new Credentials(userName, newSalt, saltedHashedPassword);

        System.out.println("Server is storing " + userName + " as:");
        System.out.println(newCredentials);

        users.add(newCredentials);
    }

    public void tryLogin(String userName, String hashedPassword) {
        System.out.println("User " + userName + " trying to log in with pw hash of " + hashedPassword);
        try {
            // find user's credentials in our user store
            Credentials foundCredentials = null;
            for(Credentials creds : users) {
                if(creds.getUserName().equals(userName)) {
                    foundCredentials = creds;
                    break;
                }
            }
            if(foundCredentials == null)
                throw new RuntimeException("User " + userName + " not found in credentials store");

            // calc a saltedhashedpw from the hashedPassword arg
            String tempHash = hashedPassword + foundCredentials.getSalt();
            String checkStr = HashUtils.getCryptoHash(tempHash, "SHA-256");

            System.out.println("Calculated salted hashed pw is " + checkStr);

            if(!foundCredentials.getSaltedHashPassword().equals(checkStr))
                throw new RuntimeException("Salted hashed pws DO NOT match! Expecting " + foundCredentials.getSaltedHashPassword());

            System.out.println("SUCCESS! " + userName + " can login!");

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Server getInstance() {
        if(instance == null) {
            instance = new Server();
        }
        return instance;
    }
}
