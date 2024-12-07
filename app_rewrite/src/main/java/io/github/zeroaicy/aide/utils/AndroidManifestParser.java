package io.github.zeroaicy.aide.utils;

import android.text.TextUtils;
import com.aide.ui.util.Configuration;
import io.github.zeroaicy.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.FileNotFoundException;

public class AndroidManifestParser extends Configuration<AndroidManifestParser> {

	public static AndroidManifestParser instance;

	public static synchronized AndroidManifestParser getInstance() {  
        if (instance == null) {  
            instance = new AndroidManifestParser();  
        }
        return instance;  
    }

	public static AndroidManifestParser get(File path) {
		return get(path.getAbsolutePath());
	}
	public static AndroidManifestParser get(String path) {
		//复用缓存
		return getInstance().getConfiguration(path);
	}


	public AndroidManifestParser() {
		this.mAndroidManifestXmlPath = null;
	}

	@Override
	public AndroidManifestParser makeConfiguration(String path) {
		try {
			return new AndroidManifestParser(path);
		}
		catch (Exception e) {
			Log.e("AndroidManifestParser", "创建AndroidManifestParser()", e);
		}
		return this;
	}


	public static String NAME = "android:name";
	public static String MAIN = "android.intent.action.MAIN";


    public Document document;
	public void close() {
		this.document = null;
	}
	/************************************************************************************/
    private String mAndroidManifestXmlPath;
	/**
	 * 返回文件
	 */
    public String getPath() {
        return mAndroidManifestXmlPath;
    }

    public AndroidManifestParser(String xmlPath) throws IOException, ParserConfigurationException, SAXException {
        this.mAndroidManifestXmlPath = xmlPath;
		init();
    }

	Element manifestElement;
	Element applicatonElement;

    private void init() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		this.document = builder.parse(new File(mAndroidManifestXmlPath));

		this.manifestElement = this.document.getDocumentElement();
		NamedNodeMap manifestAttributes = manifestElement.getAttributes();
		if (manifestAttributes != null) {
			this.packageName = getAttributeValue(manifestAttributes, "package");
			this.versionName = getAttributeValue(manifestAttributes, "android:versionName");
			try {
				versionCode = Long.parseLong(getAttributeValue(manifestAttributes, "android:versionCode"));
			}
			catch (Throwable e) {}
		}

        this.applicatonElement = (Element)getFristElementByTagName(this.manifestElement, "application");
		if (this.applicatonElement != null) {
			NamedNodeMap applicatonAttributes = this.applicatonElement.getAttributes();
			applicationLabel = getAttributeValue(applicatonAttributes, "android:label");
			applicationName = getAttributeValue(applicatonAttributes, "android:name");
			applicationIcon = getAttributeValue(applicatonAttributes, "android:icon");			
			String extractNativeLibsValue = getAttributeValue(applicatonAttributes, "android:extractNativeLibs");
			if ("false".equals(extractNativeLibsValue)) {
				this.extractNativeLibs = false;
			}
		} 

		Node usesSdkNode = getFristElementByTagName(manifestElement, "uses-sdk");
		if (usesSdkNode != null) {
			NamedNodeMap usesSdkAttributes = usesSdkNode.getAttributes();
			this.minSdkVersion = getAttributeValue(usesSdkAttributes, "android:minSdkVersion");
			this.targetSdkVersion = getAttributeValue(usesSdkAttributes, "android:targetSdkVersion");
		}
		//this.permissions = getPermissions(this.document);
    }


	/********************************manifest属性****************************************************/
	private String packageName;
	private long versionCode;
	private String versionName;
    public String getPackageName() {
		return packageName;
    }
    public long getVersionCode() {
        return versionCode;
    }
    public String getVersionName() {
        return this.versionName;
    }
	/********************************uses-sdk属性****************************************************/
	String minSdkVersion;
	String targetSdkVersion;
	public String getMinSdkVersion() {
		return minSdkVersion;
	}
	public int getMinSdkVersionInteger() {
		int parseInt;
		try {
			parseInt = Integer.parseInt(this.minSdkVersion);
		}
		catch (NumberFormatException e) {
			parseInt = -1;
		}
		return parseInt;
	}

	public String getTargetSdkVersion() {
		return targetSdkVersion;
	}

	/********************************application属性****************************************************/

	private String applicationLabel;
	private String applicationName;
	private String applicationIcon;
	private boolean extractNativeLibs = true;

    public String getApplicationLabel() {
        return this.applicationLabel;
    }
    public String getApplicationName() {
        return this.applicationName;
    }
    public String getApplicationIcon() {
        return this.applicationIcon;
    }
	public boolean getExtractNativeLibs() {
        return this.extractNativeLibs;
    }

	/********************************新封装工具****************************************************/

	public boolean equalsAndroidDebuggableAttributeValue(boolean debuggable) {
		if (applicatonElement == null) {
			return false;
		}
		NamedNodeMap attributes = applicatonElement.getAttributes();
		if (attributes == null) {
			return false;
		}
		Node namedItem = attributes.getNamedItem("android:debuggable");


		if (namedItem == null && !debuggable) {
			return true;
		}

		boolean equals = "true".equals(namedItem.getNodeValue());
		if (equals && debuggable) {
			return true;
		}
		return false;
	}

	// 相同时不会保存
	public static void settingAndroidDebuggable(String xmlPath, boolean debuggable) {

		AndroidManifestParser androidManifestParser = AndroidManifestParser.get(xmlPath);

		Element applicatonElement = androidManifestParser.applicatonElement;
		if (applicatonElement == null) {
			return;
		}
		NamedNodeMap attributes = applicatonElement.getAttributes();
		if (attributes == null) {
			return;
		}
		Node namedItem = attributes.getNamedItem("android:debuggable");

		if (namedItem == null) {
			// 默认值是false
			if (!debuggable) {
				return;
			}
			
			applicatonElement.setAttribute("android:debuggable", "true");
			try {
				Save(androidManifestParser.document, androidManifestParser.mAndroidManifestXmlPath);
				androidManifestParser.close();
			}
			catch (Exception e) {
			}
			return;
		}

		String nodeValue = namedItem.getNodeValue();

		if ("true".equals(nodeValue) 
			&& debuggable) {
			return;
		}

		namedItem.setNodeValue(String.valueOf(debuggable));
		try {
			Save(androidManifestParser.document, androidManifestParser.mAndroidManifestXmlPath);
			androidManifestParser.close();
		}
		catch (Exception e) {}
		System.out.println("退出6");
		
	}

	/********************************封装工具****************************************************/
	public static String getAttributeNodeValue(Element element, String attributeName) {
		Attr attributeNode = element.getAttributeNode(attributeName);
		if (attributeNode != null) {
			return attributeNode.getValue();
		}
		return null;
	}
	public static String getAttributeValue(NamedNodeMap attributes, String attributeName) {
		if (attributes == null) return null;

		Node namedItem = attributes.getNamedItem(attributeName);
		if (namedItem != null) {
			return namedItem.getNodeValue();
		}
		return null;
	}

	public static Node getFristElementByTagName(Element node, String name) {
		if (node == null) return null;

		NodeList elementsByTagName = node.getElementsByTagName(name);
		if (elementsByTagName == null || elementsByTagName.getLength() < 1) {
			return null;
		}
		return elementsByTagName.item(0);

	}

	/************************************************************************************/
    public String getFirstActivityName() {
        Node n = getFirstActivity();
        if (n == null)
            return null;
        return getNodeValue(n, NAME);
    }
	public static String getNodeValue(Node node, String attributeName) {
        if (node == null) return "";

        NamedNodeMap map = node.getAttributes();
        if (map == null)
            return "";

        Node nn = map.getNamedItem(attributeName);

        if (nn != null)
            return nn.getNodeValue();
        return "";
    }

	List<String> permissions ;
	public List<String> getPermissions() {
		return this.permissions;
	}

	public static List<String> getPermissions(Document document) {
        if (document == null) return null;

        NodeList permissionNodeList = document.getElementsByTagName("uses-permission");
        if (permissionNodeList == null) return null;

        List<String> result = new ArrayList<String>();
        for (int i = 0; i < permissionNodeList.getLength(); i += 1) {
            NamedNodeMap map = permissionNodeList.item(i).getAttributes();
            String attributeValue = getAttributeValue(map, "android:name");
			if (TextUtils.isEmpty(attributeValue)) {
				continue;
			}
			result.add(attributeValue);
        }
        return result;
    }

	/*******************修改API*******/


	/**
	 * 修改所有activity的android:exported
	 */
    public void exportActivity() {
        NodeList per = document.getElementsByTagName("activity");
        for (int i = 0; i < per.getLength(); i += 1) {
            ((Element) per.item(i)).setAttribute("android:exported", "true");
        }
    }
    public void exportActivity(boolean all) {
        NodeList per = document.getElementsByTagName("activity");
        for (int i = 0; i < per.getLength(); i += 1) {
            ((Element) per.item(i))
				.setAttribute("android:exported", "true");
        }
    }

    public List<String> getActivityName() {
        List<FActivityInfo> as = getActivity();
        List<String> names = new ArrayList<String>();
        for (int i = 0; i < as.size(); i += 1)
            names.add(as.get(i).name);
        return names;
    }


    public List<FActivityInfo> getActivity() {
        NodeList per = document.getElementsByTagName("activity");
        List<FActivityInfo> result = new ArrayList<FActivityInfo>();
        for (int i = 0; i < per.getLength(); i += 1) {
            FActivityInfo activityitem = new FActivityInfo();
            activityitem.name = getNodeValue(per.item(i), "android:name");
            activityitem.lable = getNodeValue(per.item(i), "android:name");
            //查找Main类
            NodeList childs = per.item(i).getChildNodes();
            for (int j = 0; j < childs.getLength(); j += 1) {
                Node an = childs.item(j);
                if (an == null)
                    continue;
                String localname = an.getNodeName();
                if (localname == null) continue;
                if (localname.equals("intent-filter")) {
                    NodeList childs2 = an.getChildNodes();
                    if (childs2 == null) continue;

                    for (int o = 0; o < childs.getLength(); o += 1) {
                        if (childs2.item(o) == null) continue;
                        if (childs2.item(o).getNodeName().equals("action")) {
                            String value = getNodeValue(childs2.item(o), "android:name");
                            if (value.equals(MAIN)) {
                                activityitem.isMain = true;
                            }
                        }
                    }
                }
            }
            result.add(activityitem);
        }
        return result;
    }

    public Node getFirstActivity() {
        NodeList per = document.getElementsByTagName("activity");
        List<FActivityInfo> info = getActivity();
        for (int i = 0; i < info.size(); i += 1) {
            if (info.get(i).isMain) {
                return per.item(i);
            }
        }

        return null;
    }

    public boolean setFirstActivity(int id) {
        Node FistActivity = null;
        Node node = getFirstActivity();
        if (node == null)
            return false;
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i += 1) {
            if (list.item(i).getNodeName().equals("intent-filter")) {
                if (list.item(i) == null) continue;
                NodeList action = list.item(i).getChildNodes();
                if (action != null) {
                    for (int p = 0; p < action.getLength(); p += 1) {
                        String name = getNodeValue(action.item(p), NAME);
                        if (name.equals("android.intent.action.MAIN")) {
                            FistActivity = list.item(i);
                            node.removeChild(list.item(i));
                            break;
                        }
                    }
                }

            }
        }
        if (FistActivity == null) {
            return false;
        }
        NodeList per = document.getElementsByTagName("activity");
        per.item(id).appendChild(FistActivity);
        return true;
    }

    public boolean RemovePermission(int id) {
		getPermissions().remove(id);
        return false;
    }

    public static void Save(Document document, String mAndroidManifestXmlPath) throws Exception {
		FileOutputStream fos = new FileOutputStream(mAndroidManifestXmlPath);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        callDomWriter(document, osw, "UTF-8");
    }

	/*
	 public static void format(Activity a, File manifestFile, boolean f){
	 f = ManifestActivity.isAnnotationPermission();
	 FileWriter fileWriter = null;

	 try{
	 List<Permission> list = PManifestUtils.load(manifestFile);
	 DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	 String content = ManifestActivity.ManifestUtils.save(list, db.parse(manifestFile), f);
	 fileWriter = new FileWriter(manifestFile);
	 fileWriter.write(content);

	 }
	 catch (Throwable e){
	 Utils.showExDialog(a, e);
	 }
	 finally{
	 try{
	 fileWriter.flush();
	 }
	 catch (Throwable e){}
	 try{
	 fileWriter.close();
	 }
	 catch (Throwable e){}
	 }
	 }
	 */

    public static Node getNode(Node n, String attr) {
        NamedNodeMap map = n.getAttributes();
        if (map == null)
            return null;
        Node nn = map.getNamedItem(attr);
        if (nn != null)
            return nn;
        return null;
    }




    public static void setNodeValue(Node n, String attr, String newname) {
        NamedNodeMap map = n.getAttributes();

        Node nn = map.getNamedItem(attr);
        if (nn != null)
            nn.setNodeValue(newname);
    }

    public static void callDomWriter(Document dom, Writer writer, String encoding) {
        try {

			Source source = new DOMSource(dom);
            Result res = new StreamResult(writer);
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.transform(source, res); 
		}
		catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
		catch (TransformerException e) {
            e.printStackTrace();
        }
    }
	public static class FActivityInfo {

		public String name;

		public String lable;

		public boolean isMain;

	}
}
