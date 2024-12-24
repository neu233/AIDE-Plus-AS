package io.github.zeroaicy.aide.services;

import android.content.SharedPreferences;
import com.android.apksig.ApkSigner;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.utils.jks.JksKeyStore;
import io.github.zeroaicy.util.FileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApkSignerService {
	// 流将自动关闭
	private static PrivateKey readPrivateKeyFromFile(InputStream inputStream) throws NoSuchProviderException, NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		byte[] readAllBytes = FileUtil.readAllBytes(inputStream);

		return readPrivateKeyFromFile(readAllBytes);
	}
	private static PrivateKey readPrivateKeyFromFile(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {  
		// 使用Bouncy Castle的PKCS8EncodedKeySpec来解析私钥  
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);  
		KeyFactory kf = KeyFactory.getInstance("RSA", "BC"); // 或者 "EC", "DSA" 等，取决于你的私钥类型  
		return kf.generatePrivate(spec);  
	}  



	/**
	 * 优化对齐apk
	 */
	public static File zipalignApk(String zipalignLibPath, File unZipAlignSignerApkFile, File unSignedApkFile) throws Exception, Throwable {

		//填充参数
		List<String> args = new ArrayList<>();
		args.add(zipalignLibPath);
		args.add("-p");
		args.add("-v");
		args.add("4");
		//输入
		args.add(unZipAlignSignerApkFile.getAbsolutePath());
		//输出
		args.add(unSignedApkFile.getAbsolutePath());

		abcd.wf j62 = abcd.xf.j6(args, null, null, true, null, null);
		if (j62.DW() != 0) {
			throw new Exception(" zipalign Error: " + new String(j62.j6()));
		}
		//删除缓存
		unZipAlignSignerApkFile.delete();

		return unSignedApkFile;
	}



	public static void signerApk(int minSdkVersion, String keystorePath,String alias, String aliasPassword, String password, File unsignedApk, File signedApk) throws Throwable {
		PrivateKey privateKey;
		X509Certificate certificate;
		//自定义签名文件是存在
		if (keystorePath != null && new File(keystorePath).exists()) {
			//支持 .pk8 与 .x509.pem签名文件
			if (keystorePath.endsWith(".x509.pem") 
				|| keystorePath.endsWith(".pk8")) {
				String keyNamePrefix;
				if (keystorePath.endsWith(".x509.pem")) {
					keyNamePrefix = keystorePath.substring(0, keystorePath.length() - 9);
				} else {
					keyNamePrefix = keystorePath.substring(0, keystorePath.length() - 4);
				}

				InputStream cert = new FileInputStream(new File(keyNamePrefix + ".x509.pem"));
				certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(cert);
				cert.close();

				privateKey = readPrivateKeyFromFile(new FileInputStream(keyNamePrefix + ".pk8"));
				cert.close();
			} else {

				FileInputStream keystoreIs = new FileInputStream(keystorePath);
				// 支持jks，bks
				KeyStore jks = new JksKeyStore();
				jks.load(keystoreIs, password.toCharArray());

				privateKey = (PrivateKey) jks.getKey(alias, aliasPassword.toCharArray());
				certificate = (X509Certificate) jks.getCertificateChain(alias)[0];

				keystoreIs.close();
			}
		} else {
			//为设置签名文件使用内置签名文件
			String keyName = "testkey";
			Class<ApkSignerService> clazz = ApkSignerService.class;

			InputStream certInputStream = clazz.getResourceAsStream("/keys/" + keyName + ".x509.pem");
			certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(certInputStream);
			certInputStream.close();

			privateKey = readPrivateKeyFromFile(clazz.getResourceAsStream("/keys/" + keyName + ".pk8"));

		}

		if (signedApk.exists()) {
			signedApk.delete();
		}
		//签名
		ApkSignerService.signerApk(minSdkVersion, privateKey, certificate, unsignedApk, signedApk);

		//删除输入文件
		unsignedApk.delete();
	}

	private static void signerApk(int minSdkVersion, PrivateKey privateKey, X509Certificate certificate, File unsignedApk, File signedApk) throws Throwable {
		
		SharedPreferences defaultSp = ZeroAicySetting.getDefaultSp();
		boolean isApksignv1 = defaultSp.getBoolean("apksign_v1", true);
		boolean isApksignv2 = defaultSp.getBoolean("apksign_v2", true);
		boolean isApksignv3 = defaultSp.getBoolean("apksign_v3", true);

		ApkSigner.SignerConfig signerConfig = new ApkSigner.SignerConfig.Builder("ANDROID",  privateKey, Collections.singletonList(certificate))
			.build();
		ApkSigner.Builder builder = new ApkSigner.Builder(Collections.singletonList(signerConfig));
		builder.setCreatedBy("Android Gradle 8.4")
			.setMinSdkVersion(minSdkVersion)
			.setInputApk(unsignedApk)
			.setOutputApk(signedApk)
			.setV1SigningEnabled(isApksignv1)
			.setV2SigningEnabled(isApksignv2)
			.setV3SigningEnabled(isApksignv3)
			.build()
			.sign();
	}
}
