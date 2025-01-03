package io.github.zeroaicy.aide.scm;

import org.eclipse.jgit.transport.URIish;

public class UnsupportedCredentialItemException extends RuntimeException {
	
    public UnsupportedCredentialItemException(URIish uRIish, String str) {
        super(uRIish.setPass((String) null) + ": " + str);
    }
}

