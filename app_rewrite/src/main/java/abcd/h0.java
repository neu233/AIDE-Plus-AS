/**
 * @Author ZeroAicy
 * @AIDE AIDE+
 */

//
// Decompiled by Jadx - 814ms
//
package abcd;

import androidx.annotation.Keep;
import com.aide.codemodel.AIDEModel;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeSpace;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.codemodel.api.abstraction.CodeAnalyzer;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.callback.HighlighterCallback;
import com.aide.codemodel.api.collections.FunctionOfIntLong;
import com.aide.codemodel.api.collections.SetOfInt;
import com.aide.codemodel.language.java.EclipseJavaCodeAnalyzer2;
import io.github.zeroaicy.util.IOUtils;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

/**
 * 高亮填充工具
 */
public class h0 {

    private final SyntaxTreeSpace syntaxTreeSpace;

	// 语义高亮版本
    private FunctionOfIntLong semanticHighlighterVersionMap;

	// 词法高亮版本
    private FunctionOfIntLong lexerHighlighterVersionMap;

    private final AIDEModel aideModel;

	FileSpace fileSpace;

	HighlighterCallback highlighterCallback;
    @Keep
    public h0( AIDEModel aideModel ) {
		this.aideModel = aideModel;

		this.fileSpace = aideModel.fileSpace;
		this.syntaxTreeSpace = aideModel.syntaxTreeSpace;
		this.highlighterCallback = aideModel.highlighterCallback;

		this.semanticHighlighterVersionMap = new FunctionOfIntLong();
		this.lexerHighlighterVersionMap = new FunctionOfIntLong();
    }

	// 高亮 根据SyntaxTree
    private void fillSemanticHighlighter( SyntaxTree syntaxTree ) {

		// 编辑器 渲染时采用倒序查询
		// 哪个优先添加用哪个，奇怪, 不是倒序查询吗
		CodeAnalyzer codeAnalyzer = syntaxTree.getLanguage().getCodeAnalyzer();
		if ( codeAnalyzer instanceof EclipseJavaCodeAnalyzer2 ) {
			EclipseJavaCodeAnalyzer2 eclipseJavaCodeAnalyzer2 = (EclipseJavaCodeAnalyzer2)codeAnalyzer;
			// 回调EclipseJavaCodeAnalyzer2填充语义高亮
			eclipseJavaCodeAnalyzer2.fillSemanticHighlighter(syntaxTree.getFile());
		}
		
		// 根据 SyntaxTree(AIDE语义) 填充高亮
		fillSemanticHighlighter(syntaxTree, syntaxTree.getRootNode());
		
	}
	
	private void fillSemanticHighlighter( SyntaxTree syntaxTree, int nodeIndex ) {
		if ( syntaxTree.isIdentifierNode(nodeIndex) ) {

			int attrReferenceKind = syntaxTree.getAttrReferenceKind(nodeIndex);

			switch ( attrReferenceKind ) {
				case 2:
				case 3:
					if ( syntaxTree.hasAttrType(nodeIndex) && syntaxTree.getAttrType(nodeIndex).isDelegateType() ) {
						this.highlighterCallback.delegateFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
					}
					break;
				case 6:
					this.highlighterCallback.namespaceFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
					break;
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 17:
					this.highlighterCallback.typeFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
					break;
				case 15:
					this.highlighterCallback.identifierFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
					break;
				case 16:
					if ( !syntaxTree.hasAttrType(nodeIndex) || !syntaxTree.getAttrType(nodeIndex).isDelegateType() ) {
						this.highlighterCallback.identifierFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
						break;
					} else {
						this.highlighterCallback.delegateFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
						break;
					}
			}

			switch ( attrReferenceKind ) {
				case 20:
					if ( syntaxTree.pl(nodeIndex) ) {
						this.highlighterCallback.delegateFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
						break;
					}
					break;
				case 21:
				case 22:
				case 23:
				case 24:
				case 25:
					this.highlighterCallback.typeFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
					break;
				case 26:
					this.highlighterCallback.keywordFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
					break;
				case 30:
					this.highlighterCallback.typeFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
					break;
			}
			// this.highlighterCallback.typeFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
		}


		int childCount = syntaxTree.getChildCount(nodeIndex);
		for ( int childNodeIndex = 0; childNodeIndex < childCount; childNodeIndex++ ) {
			fillSemanticHighlighter(syntaxTree, syntaxTree.getChildNode(nodeIndex, childNodeIndex));
		}
	}

	

	// fillSemanticHighlighter
	@Keep
    public void DW( FileEntry fileEntry, List<SyntaxTree> syntaxTrees ) {
        int fileEntryId = fileEntry.getId();
		if ( fileEntry.getCodeModel() == null 
			|| this.semanticHighlighterVersionMap.get(fileEntryId) == fileEntry.getVersion() ) {
			return;
		}

		this.semanticHighlighterVersionMap.put(fileEntryId, fileEntry.getVersion());

		this.highlighterCallback.releaseSyntaxTree();

		for ( SyntaxTree syntaxTree : syntaxTrees ) {

			// semanticHighlighterParser
			fillSemanticHighlighter(syntaxTree);

			this.syntaxTreeSpace.releaseSyntaxTree(syntaxTree);
		}

		this.highlighterCallback.fileFinished(fileEntry);
    }


	// fillLexerHighlighter
    @Keep
    public void FH( FileEntry fileEntry ) {
		if ( fileEntry.getCodeModel() == null || this.lexerHighlighterVersionMap.get(fileEntry.getId()) == fileEntry.getVersion() ) {
			return;
		}
		this.lexerHighlighterVersionMap.put(fileEntry.getId(), fileEntry.getVersion());
		Hw(fileEntry, 0, null);
	}



	// lexerHighlighterParser
    @Keep
    public void Hw( FileEntry fileEntry, int i, Reader reader ) {
		
		
		// clear
		this.highlighterCallback.j6();

		HashMap<Language, SyntaxTreeStyles> syntaxTreeStylesMap = new HashMap<>();
		List<Language> languages = fileEntry.getCodeModel().getLanguages();

		for ( Language language : languages ) {
			syntaxTreeStylesMap.put(language, this.aideModel.U2.makeSyntaxTreeStyles());
		}

		if ( reader == null ) {
			try {
				reader = fileEntry.getReader();
			}
			catch (Exception unused) {
			}
		}
		try {
			CodeModel codeModel = fileEntry.getCodeModel();
			// 填充语法树(仅用于高亮)
			codeModel.fillSyntaxTree(fileEntry, reader, syntaxTreeStylesMap);

			for ( Language language : languages ) {
				
				SyntaxTreeStyles syntaxTreeStyles = syntaxTreeStylesMap.get(language);
				// 添加词法高亮
				this.highlighterCallback.addSyntaxTreeStyles(language, syntaxTreeStyles);
				
				// 释放SyntaxTreeStyles
				this.aideModel.U2.DW(syntaxTreeStyles);
			}
			this.highlighterCallback.unifedLineFound(fileEntry, i);
		}
		finally {
			IOUtils.close(reader);
		}

    }


	// 移除已关闭文件的 词法 语义 高亮版本
    @Keep
    public void Zo( ) {
		FileSpace fileSpace = this.fileSpace;

		SetOfInt closedFileEntrys = new SetOfInt();

		FunctionOfIntLong.Iterator semanticVersionIterator = this.semanticHighlighterVersionMap.default_Iterator;
		semanticVersionIterator.init();
		while ( semanticVersionIterator.hasMoreElements() ) {

			int fileEntryId = semanticVersionIterator.nextKey();
			FileEntry fileEntry = fileSpace.getFileEntry(fileEntryId);
			if ( !fileEntry.isOpen() ) {
				closedFileEntrys.put(fileEntryId);
			}
		}

		FunctionOfIntLong.Iterator lexerVersionIterator = this.lexerHighlighterVersionMap.default_Iterator;
		lexerVersionIterator.init();
		while ( lexerVersionIterator.hasMoreElements() ) {
			int fileEntryId = lexerVersionIterator.nextKey();
			FileEntry fileEntry = fileSpace.getFileEntry(fileEntryId);
			if ( !fileEntry.isOpen() ) {
				closedFileEntrys.put(fileEntryId);
			}
		}


		SetOfInt.Iterator closedFileEntrysIterator = closedFileEntrys.default_Iterator;
		closedFileEntrysIterator.init();
		while ( closedFileEntrysIterator.hasMoreElements() ) {
			int fileEntryId = closedFileEntrysIterator.nextKey();
			this.semanticHighlighterVersionMap.remove(fileEntryId);
			this.lexerHighlighterVersionMap.remove(fileEntryId);
		}
    }


	// 调用编译器时的 词法 语义高亮
	@Keep
    public void j6( FileEntry fileEntry ) {
		if ( fileEntry.getCodeModel() == null 
			|| this.semanticHighlighterVersionMap.get(fileEntry.getId()) == fileEntry.getVersion() ) {
			return;
		}
		this.semanticHighlighterVersionMap.put(fileEntry.getId(), fileEntry.getVersion());

		// 重置，与j6相同
		this.highlighterCallback.releaseSyntaxTree();

		for ( SyntaxTree syntaxTree : this.syntaxTreeSpace.VH(fileEntry) ) {
			Language language = syntaxTree.getLanguage();
			CodeAnalyzer codeAnalyzer = language.getCodeAnalyzer();
			// 语义分析
			codeAnalyzer.v5(syntaxTree);

			// 填充语义分析高亮
			fillSemanticHighlighter(syntaxTree);

			// 释放语法树
			this.syntaxTreeSpace.releaseSyntaxTree(syntaxTree);
		}

		this.highlighterCallback.fileFinished(fileEntry);
    }
	
	
	// 正确代码备份
	@SuppressWarnings("all")
	private void fillSemanticHighlighter_bak( SyntaxTree syntaxTree, int nodeIndex ) {
		if ( syntaxTree.isIdentifierNode(nodeIndex) ) {

			int attrReferenceKind = syntaxTree.getAttrReferenceKind(nodeIndex);

			if ( attrReferenceKind != 2 
				&& attrReferenceKind != 3 ) {

				if ( attrReferenceKind != 30 ) {
					switch ( attrReferenceKind ) {
						case 6:
							this.highlighterCallback.namespaceFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
							break;
						case 7:
						case 8:
						case 9:
						case 10:
						case 11:
						case 12:
						case 13:
						case 14:
						case 17:
							this.highlighterCallback.typeFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
							break;
						case 15:
							this.highlighterCallback.identifierFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
							break;
						case 16:
							if ( !syntaxTree.hasAttrType(nodeIndex) || !syntaxTree.getAttrType(nodeIndex).isDelegateType() ) {
								this.highlighterCallback.identifierFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
								break;
							} else {
								this.highlighterCallback.delegateFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
								break;
							}
					}

					switch ( attrReferenceKind ) {
						case 20:
							if ( syntaxTree.pl(nodeIndex) ) {
								this.highlighterCallback.delegateFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
								break;
							}
							break;
						case 21:
						case 22:
						case 23:
						case 24:
						case 25:
							this.highlighterCallback.typeFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
							break;
						case 26:
							this.highlighterCallback.keywordFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
							break;
					}
				} else {
					this.highlighterCallback.typeFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));

				}

				// this.highlighterCallback.typeFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));

			} else if ( syntaxTree.hasAttrType(nodeIndex) && syntaxTree.getAttrType(nodeIndex).isDelegateType() ) {
				this.highlighterCallback.delegateFound(syntaxTree.getLanguage(), syntaxTree.getStartLine(nodeIndex), syntaxTree.getStartColumn(nodeIndex), syntaxTree.getEndLine(nodeIndex), syntaxTree.getEndColumn(nodeIndex));
			}
		}


		int childCount = syntaxTree.getChildCount(nodeIndex);
		for ( int childNodeIndex = 0; childNodeIndex < childCount; childNodeIndex++ ) {
			fillSemanticHighlighter(syntaxTree, syntaxTree.getChildNode(nodeIndex, childNodeIndex));
		}
	}
}

