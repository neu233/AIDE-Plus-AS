
package io.github.zeroaicy.aide.utils.jks;

import io.github.zeroaicy.util.Log;
import java.security.KeyStore;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class JksKeyStore extends KeyStore{
	private static final BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
	private static boolean inited = false;
	static {
		initBouncyCastleProvider();
		inited = true;
	}

	public static void initBouncyCastleProvider(){
		if (inited) return;
		Log.d("JksKeyStore", "替换bouncycastle版本");
		// 先移除，后添加
		Security.removeProvider(bouncyCastleProvider.getName());
		
		Security.addProvider(bouncyCastleProvider);
	}

    public JksKeyStore(){
        super(new JKS(), bouncyCastleProvider, "jks");
    }

    private JksKeyStore(String type){
        super(new JKS(), bouncyCastleProvider, type);
		
    }
	
	public static JksKeyStore getJksKeyStore(){
		return new JksKeyStore();
	}
	
	public static JksKeyStore getBksKeyStore(){
		return new JksKeyStore("bks");
	}
	
	
	/*
	static void test(){


		System.out.println(" " + bouncyCastleProvider.getName());

		System.out.println("-------列出加密服务提供者-----");
		Provider[] providers = Security.getProviders();
		System.out.println(providers.length);

		for (Provider provider : providers){
			System.out.println("class " + provider.getClass() +  " Provider:" + provider.getName() + " - version:" + provider.getVersion());
			System.out.println(provider.getInfo());
		}
		System.out.println("");
		System.out.println("-------列出系统支持的消息摘要算法：");
		for (String s : Security.getAlgorithms("MessageDigest")){
			System.out.println(s);
		}
		System.out.println("-------列出系统支持的生成公钥和私钥对的算法：");
		for (String s : Security.getAlgorithms("KeyPairGenerator")){
			System.out.println(s);
		}
	}//*/

}

