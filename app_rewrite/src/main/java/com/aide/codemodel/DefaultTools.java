package com.aide.codemodel;
import com.aide.codemodel.api.abstraction.Tools;
import java.util.List;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.collections.MapOf;
import com.aide.codemodel.api.ClassType;
import com.aide.codemodel.api.Entity;
import com.aide.codemodel.api.collections.SetOf;
import com.aide.codemodel.api.Namespace;
import com.aide.codemodel.api.Member;
import com.aide.codemodel.api.Type;
import abcd.f2;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.callback.TemplateEvaluatorCallback;
import java.util.Set;
import com.aide.codemodel.api.abstraction.FormatOption;
import com.aide.codemodel.api.collections.HashtableOfInt;
import com.aide.codemodel.api.abstraction.Tools.a;
import com.aide.codemodel.api.collections.ListOfInt;
import com.aide.codemodel.api.Model;

public class DefaultTools implements Tools {
	protected Model model;
	public DefaultTools(Model model) {
		this.model = model;
	}
	protected int tp(FileEntry da, int i, int i2) {
		// j6
        String j6 = da.Mr(i, i2);
        int length = j6.length() - 1;
        while (length >= 0 && (Character.isLetter(j6.charAt(length)) || j6.charAt(length) == '.')) {
            length--;
        }
        return length + 2;
    }

	@Override
	public void BT(FileEntry fileEntry, int p, int p1, int p2, int p3, Type type, int[] p4, Type[] type1, int[] p5) {
		// TODO: Implement this method
	}

	@Override
	public void DW(FileEntry fileEntry, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public String EQ(Namespace namespace) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean FH(String string) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void Hw(FileEntry fileEntry, int p, int p1, boolean p2) {
		// TODO: Implement this method
	}

	@Override
	public void I(FileEntry fileEntry, int p, int p1, int p2, int p3) {
		// TODO: Implement this method
	}

	@Override
	public void J0(FileEntry fileEntry, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public void J8(SyntaxTree syntaxTree, f2 f2, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public String KD(Namespace namespace) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void Mr(FileEntry fileEntry, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public String Mz(SyntaxTree syntaxTree, MapOf<ClassType, Entity> mapOf, SetOf<Namespace> setOf) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void P8(FileEntry fileEntry, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public boolean QX(FileEntry fileEntry, int p, int p1) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void SI(FileEntry fileEntry, int p, int p1, int p2, int p3) {
		// TODO: Implement this method
	}

	@Override
	public void Sf(SyntaxTree syntaxTree, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public boolean U2(Member member, ListOfInt listOfInt, ListOfInt listOfInt1, ListOfInt listOfInt2, ListOfInt listOfInt3, ListOfInt listOfInt4) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void VH(FileEntry fileEntry) {
		// TODO: Implement this method
	}

	@Override
	public String Ws() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public HashtableOfInt<Tools.a> XL(SyntaxTree syntaxTree, f2 f2, int p, int p1, int p2) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void Zo(FileEntry fileEntry, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public void a8(FileEntry fileEntry, int p, int p1, int p2, Type type) {
		// TODO: Implement this method
	}

	@Override
	public String aM(SyntaxTree syntaxTree, int p, int p1, SetOf<? extends Type> setOf, SetOf<Entity> setOf1) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String ca(Namespace namespace) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String cb(SyntaxTree syntaxTree, int p, int p1, Type type) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void cn(FileEntry fileEntry, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public void dx(TemplateEvaluatorCallback templateEvaluatorCallback, FileEntry fileEntry, int p, int p1, String string, List<String> list) {
		// TODO: Implement this method
	}

	@Override
	public String ef(SyntaxTree syntaxTree, MapOf<ClassType, Entity> mapOf) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String ei(String string, String string1) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void er(SyntaxTree syntaxTree, FileEntry fileEntry, Language language, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public void g3(FileEntry fileEntry, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public void gW(FileEntry fileEntry, int p, int p1, Member member) {
		// TODO: Implement this method
	}

	@Override
	public void gn(FileEntry fileEntry, int p, int p1, String string, int p2, int p3, boolean p4) {
		// TODO: Implement this method
	}

	@Override
	public Set<? extends FormatOption> j3(SyntaxTree syntaxTree, int p) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public HashtableOfInt<Tools.a> j6(SyntaxTree syntaxTree, f2 f2, int p, int p1, int p2) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void lg(SyntaxTree syntaxTree, f2 f2, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public HashtableOfInt<Tools.a> nw(SyntaxTree syntaxTree, f2 f2, int p, int p1, int p2) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void rN(FileEntry fileEntry, FileEntry fileEntry1, int p, int p1, int p2, int p3) {
		// TODO: Implement this method
	}

	@Override
	public void ro(FileEntry fileEntry, int p, int p1, String string, int p2) {
		// TODO: Implement this method
	}

	@Override
	public void sG(FileEntry fileEntry, String string) {
		// TODO: Implement this method
	}

	@Override
	public void sh(FileEntry fileEntry, int p, int p1, int p2, int p3, Type type) {
		// TODO: Implement this method
	}

	@Override
	public void tp(FileEntry fileEntry, int p, int p1, int p2, int p3) {
		// TODO: Implement this method
	}

	@Override
	public void u7(FileEntry fileEntry, int p, int p1, int p2, int p3) {
		// TODO: Implement this method
	}

	@Override
	public int[] v5(SyntaxTree syntaxTree, f2 f2, int p, int p1) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String vJ(SyntaxTree syntaxTree, int p, int p1, SetOf<? extends Type> setOf) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void vy(FileEntry fileEntry, Member member, ListOfInt listOfInt, ListOfInt listOfInt1, ListOfInt listOfInt2, ListOfInt listOfInt3, ListOfInt listOfInt4) {
		// TODO: Implement this method
	}

	@Override
	public void we(FileEntry fileEntry, int p, int p1) {
		// TODO: Implement this method
	}

	@Override
	public void x9(FileEntry fileEntry, int p, int p1, int p2, int p3) {
		// TODO: Implement this method
	}

	@Override
	public void yS(SyntaxTree syntaxTree, int p, int p1, int p2, boolean p3) {
		// TODO: Implement this method
	}


}
