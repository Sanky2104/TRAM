
package backend;

import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class logindriver {

    public static String decrypt(String strToDecrypt) {
        try {
            String secret_key, salt_val;
            CSVReader reader = new CSVReader(new FileReader("back\\salt&private.csv"));
            String[] nextLine;
            nextLine = reader.readNext();
            secret_key = nextLine[0];
            salt_val = nextLine[1];
            /* Declare a byte array. */
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(secret_key.toCharArray(), salt_val.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            /* Retruns decrypted value. */
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (IOException | CsvValidationException | InvalidAlgorithmParameterException | InvalidKeyException
                | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException
                | NoSuchPaddingException e) {
            System.out.println("Error occured during decryption: " + e.toString());
        }
        return null;
    }

    public Document verifyDriver(String Username, String Password) {
        MongoClient mc = MongoClients.create(
                "mongodb+srv://sainidiv2003:Divyanshu%401@cluster0.ruxesjs.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mc.getDatabase("DRIVERS");
        MongoCollection<Document> col = db.getCollection("DRIVER_INFO");
        Bson filters = Filters.eq("UserName", Username);
        FindIterable<Document> fr = col.find(filters);
        Iterator<Document> it = fr.iterator();
        if (!it.hasNext()) {
            Document res = new Document("ResCode", 404);
            res.append("Msg", "User does not exist");
            return res;
        }
        Document userinfo = it.next();
        String Pass = userinfo.getString("Password");
        Pass = decrypt(Pass);

        if (Pass.equals(Password)) {
            Document res = new Document("ResCode", 200);
            res.append("Msg", "User Logged In");
            return res;
        } else {
            Document res = new Document("ResCode", 401);
            res.append("Msg", "Incorrect Password");
            return res;
        }

    }

    public static void main(String[] args) {
        logindriver obj = new logindriver();
        System.out.println(obj.verifyDriver("sh1234", "12345"));
    }

}
