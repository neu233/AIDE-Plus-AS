
package io.github.zeroaicy.aide.utils.jks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class MakeJksKeyStore {



// 用于初始化
	JksKeyStore mJksKeyStore;
	
	/*
	public static void main(String[] args) throws Exception {
		PrivateKey privateKey = null;
		X509Certificate certificate;

		String keystorePath = "/storage/emulated/0/Download/.MT2/keys/soushu.pk8";
		//支持 .pk8 与 .x509.pem签名文件
		String keyNamePrefix;
		if (keystorePath.endsWith(".x509.pem")) {
			keyNamePrefix = keystorePath.substring(0, keystorePath.length() - 9);
		} else {
			keyNamePrefix = keystorePath.substring(0, keystorePath.length() - 4);
		}

		InputStream cert = new FileInputStream(new File(keyNamePrefix + ".x509.pem"));
		certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(cert);
		cert.close();

		InputStream key = new FileInputStream(new File(keyNamePrefix + ".pk8"));
		
		
		//PKCS8Key pkcs8 = new PKCS8Key();
		//pkcs8.decode(key);
		//privateKey = pkcs8;
		cert.close();
//			privateKey = stringToPrivateKey(new InputStreamReader(key), "android".toCharArray());

		JksKeyStore jksKeyStore = new JksKeyStore();
		char[] toCharArray = "xy22642633645".toCharArray();
		jksKeyStore.setKeyEntry("soushu", privateKey, toCharArray, new Certificate[]{certificate});
		jksKeyStore.store(new FileOutputStream("/storage/emulated/0/Download/.MT2/keys/测试.jks"), toCharArray);
		
	}
	public static void main3(String[] args) throws Exception {
		// 别名
		String aliasName = "123456";
		// 别名密码
		char[] aliasNamePassword = "123456".toCharArray();

		JksKeyStoreInfo dn = new JksKeyStoreInfo();

		// CN 此证书公用名称
		String CN = "CommonName";
		dn.setCommonName(CN);

		// O 组织名称
		dn.setOrganization("Organization");
		// OU 组织单位名称
		dn.setOrganizationalUnit("OrganizationalUnit");

		// L 城市或区域名称
		dn.setCityOrLocality("Locality");

		// ST 省/市/自治区名称
		dn.setStateOrProvince("State");

		// C 国家代码
		String countryCode = "CN";
		dn.setCountry(countryCode);
		int certValidityYears = 100;

		String storePath = "/storage/emulated/0/Download/AppCenter/test-1.jks";
		new File(storePath).delete();
		createKeystoreAndKey(storePath, aliasNamePassword, aliasName, certValidityYears, dn);


		System.out.println("JKS file generated successfully.");
	}
	//*/

	public static KeyStore loadKeyStore(String str, char[] cArr) {
        try {
            JksKeyStore jksKeyStore = new JksKeyStore();
            FileInputStream fileInputStream = new FileInputStream(str);
            jksKeyStore.load(fileInputStream, cArr);
            fileInputStream.close();
            return jksKeyStore;
        }
		catch (Exception e) {
            try {
                KeyStore keyStore = KeyStore.getInstance("bks");
                FileInputStream fileInputStream2 = new FileInputStream(str);
                keyStore.load(fileInputStream2, cArr);
                fileInputStream2.close();
                return keyStore;
            }
			catch (Exception e2) {
                throw new RuntimeException("Failed to load keystore: " + e2.getMessage(), e2);
            }
		}
    }

	public static KeyStore createKeyStore(String storePath, char[] storePass) throws Exception {
        KeyStore keyStore = storePath.toLowerCase().endsWith(".bks") ? JksKeyStore.getBksKeyStore() : JksKeyStore.getJksKeyStore();
        keyStore.load(null, storePass);
        return keyStore;
    }



	public static void writeKeyStore(KeyStore keyStore, String str, char[] cArr) {
        File file = new File(str);
        try {
            if (!file.exists()) {
                FileOutputStream fileOutputStream = new FileOutputStream(str);
                keyStore.store(fileOutputStream, cArr);
                fileOutputStream.close();
                return;
            }

            File createTempFile = File.createTempFile(file.getName(), null, file.getParentFile());
            FileOutputStream fileOutputStream2 = new FileOutputStream(createTempFile);
            keyStore.store(fileOutputStream2, cArr);
            fileOutputStream2.flush();
            fileOutputStream2.close();
            renameTo(createTempFile, file);
        }
		catch (Exception e) {
            try {
                PrintWriter printWriter = new PrintWriter(new FileWriter(File.createTempFile("zipsigner-error", ".log", file.getParentFile())));
                e.printStackTrace(printWriter);
                printWriter.flush();
                printWriter.close();
            }
			catch (Exception e2) {
            }
        }
    }

	public static void renameTo(File file, File file2) throws IOException {
        copyFile(file, file2, true);
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }

	static void copyFile(File file, File file2, boolean z) throws IOException {
        if (file2.exists() && file2.isDirectory()) throw new IOException("Destination '" + file2 + "' exists but is a directory");
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            byte[] bArr = new byte[4096];
            while (true) {
                int read = fileInputStream.read(bArr);
                if (-1 == read) break;
                else fileOutputStream.write(bArr, 0, read);
            }
            fileOutputStream.close();
            if (file.length() != file2.length()) throw new IOException("Failed to copy full contents from '" + file + "' to '" + file2 + "'");
            if (z) file2.setLastModified(file.lastModified());
        }
		finally {
            try {
                fileInputStream.close();
            }
			catch (IOException e2) {
            }
        }
    }




	public static void createKeystoreAndKey(String storePath, char[] password,
											String keyName, int certValidityYears, JksKeyStoreInfo info) {
        createKeystoreAndKey(storePath, password, "RSA", 2048, keyName, password, "SHA256withRSA", certValidityYears,
							 info);
    }


	/**
	 * certSignatureAlgorithm 证书签名算法
	 */
    public static KeySet createKeystoreAndKey(String storePath, char[] storePass,
											  String keyAlgorithm, int keySize, String keyName, char[] keyPass,
											  String certSignatureAlgorithm, int certValidityYears, JksKeyStoreInfo info) {
        try {


            KeyStore privateKS = createKeyStore(storePath, storePass);
            KeySet keySet = createKey(keyAlgorithm, keySize, keyName, certSignatureAlgorithm, certValidityYears,
									  info);



            privateKS.setKeyEntry(keyName, keySet.getPrivateKey(),
								  keyPass,
								  new Certificate[]{keySet.getPublicKey()});

            File sfile = new File(storePath);

            if (sfile.exists()) {
				
                throw new IOException("File already exists: " + storePath);
            }
            writeKeyStore(privateKS, storePath, storePass);

            return keySet;
        }
		catch (RuntimeException x) {
            throw x;
        }
		catch ( Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        }
    }

    /** Create a new key and store it in an existing keystore.
     *
     */
    public static KeySet createKey(String storePath, char[] storePass,
								   String keyAlgorithm, int keySize, String keyName, char[] keyPass,
								   String certSignatureAlgorithm, int certValidityYears,
								   JksKeyStoreInfo MakeJksKeyStore) {
        try {

            KeyStore privateKS = loadKeyStore(storePath, storePass);

            KeySet keySet = createKey(keyAlgorithm, keySize, keyName, certSignatureAlgorithm, certValidityYears,
									  MakeJksKeyStore);


            privateKS.setKeyEntry(keyName, keySet.getPrivateKey(),
								  keyPass,
								  new Certificate[]{keySet.getPublicKey()});

            writeKeyStore(privateKS, storePath, storePass);

            return keySet;

        }
		catch (RuntimeException x) {
            throw x;
        }
		catch ( Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        }
    }

	/**
	 * certValidityYears 证书有效期年份
	 */
	@SuppressWarnings({"deprecation"})
    public static KeySet createKey(String keyAlgorithm, int keySize, String keyName,
								   String certSignatureAlgorithm, int certValidityYears, JksKeyStoreInfo info) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyAlgorithm);
            keyPairGenerator.initialize(keySize);
            KeyPair KPair = keyPairGenerator.generateKeyPair();

            X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();

            X509Principal principal = info.getPrincipal();

            // generate a postitive serial number
            BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextInt());
            while (serialNumber.compareTo(BigInteger.ZERO) < 0) {
                serialNumber = BigInteger.valueOf(new SecureRandom().nextInt());
            }
            v3CertGen.setSerialNumber(serialNumber);
            v3CertGen.setIssuerDN(principal);
            v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 30L));
            v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60L * 60L * 24L * 366L * (long)certValidityYears)));
            v3CertGen.setSubjectDN(principal);

            v3CertGen.setPublicKey(KPair.getPublic());
            v3CertGen.setSignatureAlgorithm(certSignatureAlgorithm);

            X509Certificate PKCertificate = v3CertGen.generate(KPair.getPrivate(), "BC");

            KeySet keySet = new KeySet();
            keySet.setName(keyName);
            keySet.setPrivateKey(KPair.getPrivate());
            keySet.setPublicKey(PKCertificate);
            return keySet;
        }
		catch (Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        }
    }
}
