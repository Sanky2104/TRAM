package backend;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bson.Document;
import org.eclipse.jetty.util.security.Password;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class register {
    // 1
    public static List<Document> Sort(List<Document> allUsers) {

        int n = allUsers.size();
        for (int i = 1; i < n; i++) {
            Document key = allUsers.get(i);
            int j = i - 1;

            while (j >= 0 && allUsers.get(j).get("UserName").toString().compareTo(key.getString("UserName")) > 0) {
                allUsers.set(j + 1, allUsers.get(j));
                j--;
            }

            allUsers.set(j + 1, key);
        }
        return allUsers;
    }

    // 2
    public static int Search(List<Document> allUsers, String Target) {

        int left = 0;
        int right = allUsers.size() - 1;

        while (left <= right) {
            int mid = (left + right) / 2;
            int comparison = Target.compareToIgnoreCase(allUsers.get(mid).getString("UserName"));

            if (comparison == 0) {
                return mid;
            } else if (comparison < 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return -1; // Element not found
    }

    // 3
    static String Encrypt(String Password) throws CsvValidationException, IOException, NoSuchAlgorithmException {

        String secret_key, salt_val;
        CSVReader reader = new CSVReader(new FileReader("back\\salt&private.csv"));
        String[] nextLine;
        nextLine = reader.readNext();
        secret_key = nextLine[0];
        salt_val = nextLine[1];

        try {
            /* Declare a byte array. */
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            /* Create factory for secret keys. */
            // Password-Based Key Derivation Function 2 (PBKDF2)
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(secret_key.toCharArray(), salt_val.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            /* Returns encrypted value. */
            return Base64.getEncoder().encodeToString(cipher.doFinal(Password.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException
                | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException
                | NoSuchPaddingException e) {
            System.out.println("Error occured during encryption: " + e.toString());
        }

        return null;
    }

    // 4
    public Document addUser(String Name, String UserName, String PhoneNo, String Password)
            throws CsvValidationException, NoSuchAlgorithmException, IOException {

        MongoClient mc = MongoClients.create(
                "mongodb+srv://sainidiv2003:Divyanshu%401@cluster0.ruxesjs.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mc.getDatabase("USERS"); // database
        MongoCollection<Document> col = db.getCollection("USERS_INFO"); // table name
        FindIterable<Document> itr = col.find(); // find gets all the entries within the tavble
        Iterator<Document> it = itr.iterator();
        List<Document> allUsers = new ArrayList<>();
        int i = 0;
        while (it.hasNext()) {
            allUsers.add(it.next());
            i++;
        }

        int ind = Search(allUsers, UserName);

        if (ind != -1) {
            Document res = new Document("ResCode", 409);
            res.append("Msg", "User already exists!");
            return res;
        }

        Document user = new Document("UserName", UserName);
        user.append("Name", Name);
        user.append("Phone_Number", PhoneNo);
        Password = Encrypt(Password);
        user.append("Password", Password);

        List<Document> insUser = new ArrayList<>(allUsers);
        insUser.add(user);
        insUser = Sort(insUser);

        col.deleteMany(new Document());
        if (col.insertMany(insUser) != null) {
            Document res = new Document("ResCode", 200);
            res.append("Msg", "User Created!");
            return res;
        }
        Document res = new Document("ResCode", 202);
        res.append("Msg", "User not created");

        return res;

    }

    public static void main(String[] args) throws CsvValidationException, NoSuchAlgorithmException, IOException {
        register obj = new register();
        obj.addUser("div", "ainidiv", "98214782347", "12345");

    }

}