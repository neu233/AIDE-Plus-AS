
package io.github.zeroaicy.aide.utils.jks;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class KeySet {
	String name;

    // certificate
    X509Certificate publicKey = null;

    // private key
    PrivateKey privateKey = null; 

    public KeySet() {}

    public KeySet(String name, X509Certificate publicKey, PrivateKey privateKey, byte[] sigBlockTemplate) {
        this.name = name;
        this.publicKey = publicKey;
        this.privateKey = privateKey;}

    public KeySet(String name, X509Certificate publicKey, PrivateKey privateKey, String signatureAlgorithm, byte[] sigBlockTemplate) {
        this.name = name;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
	}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public X509Certificate getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(X509Certificate publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}

