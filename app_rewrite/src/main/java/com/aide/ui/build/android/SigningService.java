package com.aide.ui.build.android;

import abcd.ed;
import android.app.Activity;
import androidx.annotation.Keep;
import com.aide.common.MessageBox;
import com.aide.common.ValueRunnable;
import com.aide.ui.AppPreferences;
import com.aide.ui.MainActivity;
import com.aide.ui.ServiceContainer;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.utils.jks.JksKeyStore;
import io.github.zeroaicy.aide.utils.jks.JksKeyStoreInfo;
import io.github.zeroaicy.aide.utils.jks.KeySet;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class SigningService {

	private static String TAG = "SigningService";
	
	@Keep
    public interface SigningRunnable {
		@Keep
		public abstract void j6(String storePath, String storePassword, String aliasName, String aliasPassword);
    }

    private static class a implements Runnable {
        final String keyStoreFilePath;
        final SigningService signingService;
        final SigningRunnable signingRunnable;

		private a(SigningService signingService, String str, SigningRunnable signingRunnable) {
            this.signingService = signingService;
            this.keyStoreFilePath = str;
            this.signingRunnable = signingRunnable;
        }
		
        @Override
        public void run() {
            try {
                this.signingService.v5(this.keyStoreFilePath, this.signingRunnable);
            }
			catch (Throwable th) {
                throw new Error(th);
            }
        }
    }

    public static class b implements ValueRunnable<String> {

        final String DW;
        final SigningRunnable FH;

        final SigningService Hw;
        final String storePath;

        private b(SigningService signingService, String storePath, String str2, SigningRunnable signingRunnable) {
            this.Hw = signingService;
            this.storePath = storePath;
            this.DW = str2;
            this.FH = signingRunnable;
        }

        public void DW(String str) {
            try {
                SigningService.j6(this.Hw, this.storePath, this.DW, str, this.FH);
            }
			catch (Throwable th) {
                throw new Error(th);
            }
        }

        public /* bridge */ void acceptValue(String obj) {
            DW(obj);
        }
    }
    public static class c implements ValueRunnable<String> {
        final String DW;
        final String FH;
        final SigningRunnable Hw;
        final String j6;
        final SigningService v5;
		
		private c(SigningService signingService, String str, String str2, String str3, SigningRunnable signingRunnable) {
            this.v5 = signingService;
            this.j6 = str;
            this.DW = str2;
            this.FH = str3;
            this.Hw = signingRunnable;
        }

        public void DW(String str) {
            try {
                SigningService.DW(this.v5, this.j6, this.DW, this.FH, str, this.Hw);
            }
			catch (Throwable th) {
                throw new Error(th);
            }
        }

        public /* bridge */ void acceptValue(String obj) {
            DW(obj);
        }
    }



    public SigningService() {

    }

    static void DW(SigningService signingService, String str, String str2, String str3, String str4, SigningRunnable signingRunnable) {
        signingService.FH(str, str2, str3, str4, signingRunnable);
    }

    private void FH(String keystorePath, String str2, String str3, String str4, SigningRunnable signingRunnable) {
        try {
			if (keystorePath.endsWith(".x509.pem") 
				|| keystorePath.endsWith(".pk8")) {
				signingRunnable.j6(keystorePath, str2, str3, str4);
				return;
			}
			JksKeyStore jksKeyStore = new JksKeyStore();
			jksKeyStore.load(new FileInputStream(keystorePath), str2.toCharArray());
			if (jksKeyStore.getKey(str3, str4.toCharArray()) != null) {
				signingRunnable.j6(keystorePath, str2, str3, str4);
				return;
			}
			throw new Exception("no alias");
		}
		catch (Throwable unused) {
			MessageBox.BT(ServiceContainer.getMainActivity(), "Build Error", "Invalid keystore credentials!");
		}
    }

    private void VH(String keystorePath, String str2, SigningRunnable signingRunnable) {
        try {
			if (keystorePath.endsWith(".x509.pem") 
				|| keystorePath.endsWith(".pk8")) {
				signingRunnable.j6(keystorePath, "", "", "");
				return;
			}
			JksKeyStore jksKeyStore = new JksKeyStore();
			jksKeyStore.load(new FileInputStream(keystorePath), str2.toCharArray());
			ArrayList<String> list = Collections.list(jksKeyStore.aliases());
			if (list.size() == 1) {
				gn(keystorePath, str2, list.get(0), signingRunnable);
			} else {
				MessageBox.VH(ServiceContainer.getMainActivity(), "Select keystore alias", list, new b(this, keystorePath, str2, signingRunnable));
			}
		}
		catch (Throwable unused) {
			MessageBox.BT(ServiceContainer.getMainActivity(), "Build Error", "Invalid keystore credentials!");
		}
    }

    private void gn(String str, String str2, String str3, SigningRunnable signingRunnable) {
        try {
            MainActivity mainActivity = ServiceContainer.getMainActivity();
            MessageBox.J8(mainActivity, (String) null, "Enter password for keystore alias '" + str3 + "':", new c(this, str, str2, str3, signingRunnable));
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    static void j6(SigningService signingService, String str, String str2, String str3, SigningRunnable signingRunnable) {
        signingService.gn(str, str2, str3, signingRunnable);
    }
	
	
	// -> makeJksKeyStore
	@Keep
	public boolean makeJksKeyStore(String storePath, String password, String aliasName, String aliasPassword, Date notBefore, Date notAfter, BigInteger serialNumber, String commonName, String organization, String organizationalUnit, String cityOrLocality, String stateOrProvince, String countryCode) {
        try {
			JksKeyStoreInfo info = new JksKeyStoreInfo();
			info.setCommonName(commonName);
			if (organization.length() > 0) {
				// 组织
                info.setOrganization(organization);
            }
            if (organizationalUnit.length() > 0) {
				// 组织单位
                info.setOrganizationalUnit(organizationalUnit);
			}

			if (cityOrLocality.length() > 0) {
				// 城市或地区
				info.setCityOrLocality(cityOrLocality);
            }
            if (stateOrProvince.length() > 0) {
				// 州或省
                info.setStateOrProvince(stateOrProvince);
			}
            if (countryCode.length() > 0) {
				// 国家代码
				info.setCountry(countryCode);
            }

			KeySet keySet = createKey(aliasName, notBefore, notAfter, 
									  serialNumber, info);



			JksKeyStore keyStore = storePath.toLowerCase().endsWith(".bks") ? JksKeyStore.getBksKeyStore() : JksKeyStore.getJksKeyStore();
			
			keyStore.load(null, null);
			keyStore.setKeyEntry(aliasName, keySet.getPrivateKey(),
								  aliasPassword.toCharArray(),
								  new Certificate[]{keySet.getPublicKey()});
			FileOutputStream fileOutputStream = new FileOutputStream(storePath);
			keyStore.store(fileOutputStream, password.toCharArray());
			fileOutputStream.flush();
            fileOutputStream.close();
			return true;


        }
		catch (Throwable th) {
			throw new Error(th);
        }

    }

	public static KeySet createKey(String keyName,
								   Date notBefore, Date notAfter, BigInteger serialNumber, JksKeyStoreInfo info) {
        try {
            // 密钥对
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

			// 密钥长度
            keyPairGenerator.initialize(2048);
			// 生成 密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

			// 根据配置生成证书
            X509Principal principal = info.getPrincipal();

			// 证书生成器
            X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();

			// 
            v3CertGen.setSerialNumber(serialNumber);
            // 证书信息
			v3CertGen.setIssuerDN(principal);
            v3CertGen.setSubjectDN(principal);
			// 证书创建日期
            v3CertGen.setNotBefore(notBefore);
            // 证书过期日期
            v3CertGen.setNotAfter(notAfter);
			// 设置公钥
            v3CertGen.setPublicKey(keyPair.getPublic());
			// 设置签名算法
            v3CertGen.setSignatureAlgorithm("SHA256withRSA");

			//生成证书
            X509Certificate x509Certificate = v3CertGen.generate(keyPair.getPrivate(), "BC");
			// 返回证书集合 KeySet
            KeySet keySet = new KeySet();
            keySet.setName(keyName);
            keySet.setPrivateKey(keyPair.getPrivate());
            keySet.setPublicKey(x509Certificate);
            return keySet;
        }
		catch (Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        }
    }

	@Keep
    public void Zo(String storePath, BuildGradle.SigningConfig signingConfig, SigningRunnable runnable) {

		if (signingConfig == null) {
			//Log.d(TAG, "signingConfig is null");
			if (storePath.endsWith(".x509.pem") 
				|| storePath.endsWith(".pk8")) {
				runnable.j6(storePath, "", "", "");
				return;
			}
			v5(storePath, runnable);
			return;
		}

		// Log.d(TAG, "signingConfig is ", signingConfig);
		// 读取 signingConfig
		try {
			storePath = signingConfig.getStoreFilePath();

			if (storePath.endsWith(".x509.pem") 
				|| storePath.endsWith(".pk8")) {
				runnable.j6(storePath, signingConfig.storePassword, signingConfig.keyAlias, signingConfig.keyPassword);
				return;
			}

			JksKeyStore jksKeyStore = new JksKeyStore();
			char[] toCharArray = signingConfig.storePassword.toCharArray();
			jksKeyStore.load(new FileInputStream(storePath), toCharArray);
			
			if (jksKeyStore.getKey(signingConfig.keyAlias, signingConfig.keyPassword.toCharArray()) != null) {
				runnable.j6(storePath, signingConfig.storePassword, signingConfig.keyAlias, signingConfig.keyPassword);
				return;
			}
			String message = "can not read keystore\n";
			message += "Failed to open signingConfig from build.gradle. Use alternative signing?\n";
			MessageBox.rN(ServiceContainer.getMainActivity(), "Build Error", message, new a(this, storePath, runnable), (Runnable) null);

		}
		catch (Throwable unused) {
			String message = "Failed to open signingConfig from build.gradle. Use alternative signing?\n";
			if (unused instanceof UnrecoverableKeyException) {
				if (unused.getMessage().startsWith("checksum mismatch")) {
					message = "签名文件配置错误\n" + message;
				}
			}
			MessageBox.rN(ServiceContainer.getMainActivity(), "Build Error", message, new a(this, storePath, runnable), (Runnable) null);
			return;
		}

    }

	@Keep
    public void u7(Activity activity) {
        try {
            MessageBox.showDialog(activity, new ed(FileSystem.getExternalStorageDirectory() + "/AppProjects/debug.keystore", "androiddebugkey", "android", new d(activity)));
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	static class d implements ed.f {
        final Activity activity;
        public d(Activity activity) {
            this.activity = activity;
        }

        public void j6(boolean z, String str) {
            try {
                if (z) {
                    AppPreferences.setUserKeystore(str);
                    Activity activity = this.activity;
                    MessageBox.ei(activity, "Create keystore", "Keystore file " + str + " has been created and set as default.", (Runnable) null);
                    return;
                }
                Activity activity2 = this.activity;
                MessageBox.BT(activity2, "Create keystore", "Failed to create keystore file " + str);
            }
			catch (Throwable th) {
                throw new Error(th);
            }
        }
    }
	
	
    public void v5(String keyStoreFilePath, SigningRunnable signingRunnable) {
        try {
			if (keyStoreFilePath == null || keyStoreFilePath.length() <= 0) {
				// 会AndroidProjectBuildService.dx() 索引越界
				synchronized( ServiceContainer.getProjectService().getMainAppWearApps() ){
					signingRunnable.j6("", "", "", "");
				}
				
				return;
			}
			
			if (!FileSystem.isFileAndNotZip(keyStoreFilePath)) {

				MainActivity mainActivity = ServiceContainer.getMainActivity();
				MessageBox.BT(mainActivity, "Build Error", "Keystore file " + keyStoreFilePath + " does not exist!");
				return;
			}
			try {
				JksKeyStore jksKeyStore = new JksKeyStore();
				jksKeyStore.load(new FileInputStream(keyStoreFilePath), "android".toCharArray());

				if (jksKeyStore.getKey("androiddebugkey", "android".toCharArray()) != null) {
					signingRunnable.j6(keyStoreFilePath, "android", "androiddebugkey", "android");
					return;
				}

				throw new Exception("no androiddebugkey");
			}
			catch (Exception unused) {
				if (ServiceContainer.getLicenseService().showFeaturePremium(ServiceContainer.getCurrentActivity(), "custom_keystore")) {
					VH(keyStoreFilePath, "", signingRunnable);
					return;
				}
				return;
			}


        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
}

