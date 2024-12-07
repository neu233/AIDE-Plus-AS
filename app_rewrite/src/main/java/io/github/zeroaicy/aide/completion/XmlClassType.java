package io.github.zeroaicy.aide.completion;

import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.Type;

public class XmlClassType extends Type {
	
	public XmlClassType(FileSpace fileSpace, EntitySpace entitySpace, int semantic) {
		super(fileSpace, entitySpace, semantic);
	}
	public XmlClassType(FileSpace fileSpace, EntitySpace entitySpace) {
		super(fileSpace, entitySpace, -1);
	}

	@Override
	public String getFullyQualifiedNameString() {
		return super.getFullyQualifiedNameString();
	}
	
}
