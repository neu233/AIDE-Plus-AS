package io.github.zeroaicy.aide.utils.jks;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.X509Principal; 


/** 
 * Helper class for dealing with the distinguished name RDNs. 
 */ 
public class JksKeyStoreInfo extends LinkedHashMap<ASN1ObjectIdentifier,String> { 

	public JksKeyStoreInfo() { 
		put(BCStyle.C, null); 
		put(BCStyle.ST, null); 
		put(BCStyle.L, null); 
		put(BCStyle.STREET, null); 
		put(BCStyle.O, null); 
		put(BCStyle.OU, null); 
		put(BCStyle.CN, null); 
	} 

	public String put(ASN1ObjectIdentifier oid, String value) { 
		if (value != null && value.equals("")) value = null; 
		if (containsKey(oid)) super.put(oid, value); // preserve original ordering 
		else { 
			super.put(oid, value); 
			//            String cn = remove(BCStyle.CN); // CN will always be last. 
			//            put(BCStyle.CN,cn); 
		} 
		return value; 
	} 

	public void setCountry(String country) { 
		put(BCStyle.C, country); 
	} 

	public void setStateOrProvince(String state) { 
		put(BCStyle.ST, state); 
	} 
	
	// 城市或区域名称
	public void setCityOrLocality(String locality) { 
		put(BCStyle.L, locality); 
	} 

	public void setStreet(String street) { 
		put(BCStyle.STREET, street); 
	} 

	public void setOrganization(String organization) { 
		put(BCStyle.O, organization); 
	} 

	public void setOrganizationalUnit(String organizationalUnit) { 
		put(BCStyle.OU, organizationalUnit); 
	} 

	public void setCommonName(String commonName) { 
		put(BCStyle.CN, commonName); 
	} 

	@Override 
	public int size() { 
		int result = 0; 
		for (String value : values()) { 
			if (value != null) result += 1; 
		} 
		return result; 
	} 

	public X509Principal getPrincipal() { 
		Vector<ASN1ObjectIdentifier> oids = new Vector<ASN1ObjectIdentifier>(); 
		Vector<String> values = new Vector<String>(); 

		for (Entry<ASN1ObjectIdentifier,String> entry : entrySet()) {
			if (entry.getValue() != null && !entry.getValue().equals("")) { 
				oids.add(entry.getKey()); 
				values.add(entry.getValue()); 
			} 
		} 

		return new X509Principal(oids, values); 
	} 
}
