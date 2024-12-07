package abcd;


/*
 author : 罪慾
 date : 2024/10/2 22:46
 description : QQ3115093767
 */

import androidx.annotation.Keep;

import com.aide.codemodel.api.ClassType;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Member;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.Type;
import com.aide.codemodel.api.collections.ListOf;
import com.aide.codemodel.api.collections.MapOfInt;
import com.aide.codemodel.api.collections.SetOf;
import com.aide.codemodel.language.xml.XmlCodeModel;
import com.aide.common.AppLog;
import io.github.zeroaicy.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class v7 {

    private static final List<ClassType> viewClassTypes = new ArrayList<>();

    private ListOf<Member> FH;

    private Model model;


    public v7( Model model ) {
        this.model = model;

		// 没用entitySpace没有类
		//initViewClassTypes();

    }

	private void initViewClassTypes( ) {

		if ( ! viewClassTypes.isEmpty() ) {
			return;
		}

		AppLog.d("initViewClassTypes()");
		//this.model.codeCompleterCallback.j6();

		//(syntaxTree.getFile(), syntaxTree.getLanguage()).default_Iterator;
		MapOfInt<ClassType>.Iterator default_Iterator = model.entitySpace.XG().default_Iterator;
		// init
		default_Iterator.init();

		ClassType viewClassType = this.model.entitySpace.getRootNamespace().getMemberNamespace(this.model.identifierSpace.get("android")).getMemberNamespace(this.model.identifierSpace.get("view")).getAllMemberClassTypes().get(this.model.identifierSpace.get("View"));

		long loadSyntaxStartTime = System.currentTimeMillis();
		// 只写操作
		int classTypesSize = 0;

		while ( default_Iterator.hasMoreElements() ) {
			classTypesSize++;
			ClassType classType = default_Iterator.nextValue();
			if ( isViewClassType(classType, viewClassType) ) {
				this.viewClassTypes.add(classType);
			}
		}
		AppLog.d("y7", "init 遍历并loadSyntax %s个 耗时 %s 秒", classTypesSize, ( System.currentTimeMillis() - loadSyntaxStartTime ) / 1000f);		
	}


    private boolean v5( FileEntry fileEntry ) {
        return fileEntry.getParentDirectory().getFullNameString().startsWith("layout");
    }

    private String getParentName( SyntaxTree syntaxTree ) {
        return syntaxTree.getFile().getParentDirectory().getFullNameString();
    }

    public void DW( SyntaxTree syntaxTree, int i, int i2 ) {
        // DW 补全资源
        String fileName = getParentName(syntaxTree);
        if ( fileName.startsWith("layout") ) {

        }
    }



	@Keep
	public void J8( SyntaxTree syntaxTree, int i, int i2 ) {

	}

	// init codeCompleter
	@Keep
    public void FH( SyntaxTree syntaxTree, int i, int i2 ) {
		//android.widget
		
		long time = System.currentTimeMillis();
        // 提示节点
        String fileName = getParentName(syntaxTree);

        Log.d("fileName", syntaxTree.getFile().getFullNameString());
		
		if ( fileName.startsWith("layout") ) {

			// 重置代码提示
			this.model.codeCompleterCallback.listStarted();

			if ( this.viewClassTypes.isEmpty() ) {
				initViewClassTypes();
			}

			for ( int index = 0; index < this.viewClassTypes.size(); index++ ) {
				ClassType viewClassType = viewClassTypes.get(index);
				//j6.codeCompleterCallback.Ws(data);//Java的导包方式
				//j6.codeCompleterCallback.Zo(data.getFullyQualifiedNameString()); // 粗
				//j6.codeCompleterCallback.DW(data.getFullyQualifiedNameString()); // 细

				//member.getIdentifierString().equals("id") ? "android:id=\"@+id/" + (this.model.identifierSpace.getString(syntaxTree.getFile().getNameIdentifier()) + syntaxTree.getIdentifierString(i2)) + "|\"" : "android:" + member.getIdentifierString() + "=\"|\"");
				/// 显示 ClassType简称 填充 全称
				String fullyQualifiedNameString = viewClassType.getFullyQualifiedNameString();
				if ( fullyQualifiedNameString.startsWith("android.widget.") ) {
					fullyQualifiedNameString = viewClassType.getIdentifierString();
				}
				this.model.codeCompleterCallback.aM(viewClassType, fullyQualifiedNameString);
            }
			return;
        }
		
        if ( syntaxTree.getFile().getFullNameString().equals("AndroidManifest.xml") ) {

        } else if ( fileName.startsWith("xml") ) {

        } else if ( fileName.startsWith("values") ) {

        } else if ( fileName.startsWith("menu") ) {

        } else if ( fileName.startsWith("anim") ) {

        } else if ( fileName.startsWith("color") ) {

        } else if ( fileName.startsWith("font") ) {

        } else if ( fileName.startsWith("animator") ) {

        } else if ( fileName.startsWith("transition") ) {

        } else if ( fileName.startsWith("interpolator") ) {

        } else if ( fileName.startsWith("mipmap") ) {



		} else if ( fileName.startsWith("navigation") ) {

        } else if ( fileName.startsWith("drawable") ) {

        }

		AppLog.d("y7", "FH 耗时 %s 秒", ( System.currentTimeMillis() - time ) / 1000);
    }

	// 
	public static class FilterTask implements Callable<List<ClassType>> {
		// 只读
		final List<ClassType> classTypes;
		// 开始遍历
		final int start, end;

		final ClassType view, viewGroup;

		public FilterTask( List<ClassType> classTypes, int start, int end, ClassType view, ClassType viewGroup ) {
			this.classTypes = classTypes;
			this.start = start;
			this.end = end;
			this.view = view;
			this.viewGroup = viewGroup;
		}

		@Override
		public List<ClassType> call( ) throws Exception {
			List<ClassType> copy = new ArrayList<>(end - start);
			for ( int i = this.start; i < end; i++ ) {
				ClassType classType = classTypes.get(i);
				//synchronized(ClassType.class){
				SetOf<Type> allSuperTypes = classType.getAllSuperTypes();

				if ( !allSuperTypes.contains(view) 
					&& !allSuperTypes.contains(viewGroup) ) {
					continue;
				}
				copy.add(classType);
				//}
			}
			return copy;
		}
	}


	private boolean isViewClassType( ClassType classType, ClassType view ) {
		if( classType.getFullyQualifiedNameString().startsWith("java.")){
			return false;
		}
		return classType.isSubClassTypeOf(view);
	}

	@Keep
    public boolean Hw( FileEntry fileEntry ) {
        return fileEntry.getCodeModel() instanceof XmlCodeModel;
    }

	
	/**
	 * value[ "" ] 补全
	 */
    @Keep
	public void j6( SyntaxTree syntaxTree, int i ) {
        // 补全“”里面的属性
        // int identifier = syntaxTree.getIdentifier(syntaxTree.getChildNode(syntaxTree.getChildNode(i, 0), 2));

		// SyntaxTreeUtils.printNode(syntaxTree, syntaxTree.getRootNode());
		
		int identifier = syntaxTree.getIdentifier(syntaxTree.getChildNode(syntaxTree.getChildNode(i, 0), 2));

		if ( identifier == this.model.identifierSpace.get("layout_width")
			|| identifier == this.model.identifierSpace.get("layout_height") ) {
			this.model.codeCompleterCallback.listElementKeywordFound("fill_parent");
			this.model.codeCompleterCallback.listElementKeywordFound("match_parent");
			this.model.codeCompleterCallback.listElementKeywordFound("wrap_content");
			return;
		}


		if ( identifier == this.model.identifierSpace.get("orientation") ) {
			this.model.codeCompleterCallback.listElementKeywordFound("horizontal");
			this.model.codeCompleterCallback.listElementKeywordFound("vertical");
			return;
		}

		if ( identifier == this.model.identifierSpace.get("visibility") ) {
			this.model.codeCompleterCallback.listElementKeywordFound("visible");
			this.model.codeCompleterCallback.listElementKeywordFound("invisible");
			this.model.codeCompleterCallback.listElementKeywordFound("gone");
			return;
		}
		
		if ( identifier == this.model.identifierSpace.get("textAllignment") ) {
			this.model.codeCompleterCallback.listElementKeywordFound("inherit");
			this.model.codeCompleterCallback.listElementKeywordFound("gravity");
			this.model.codeCompleterCallback.listElementKeywordFound("textStart");
			this.model.codeCompleterCallback.listElementKeywordFound("textEnd");
			this.model.codeCompleterCallback.listElementKeywordFound("center");
			this.model.codeCompleterCallback.listElementKeywordFound("viewStart");
			this.model.codeCompleterCallback.listElementKeywordFound("viewEnd");
			return;
		}
		
		if ( identifier == this.model.identifierSpace.get("gravity") 
			|| identifier == this.model.identifierSpace.get("layout_gravity") 
			|| identifier == this.model.identifierSpace.get("foregroundGravity") ) {
				
			this.model.codeCompleterCallback.listElementKeywordFound("top");
			this.model.codeCompleterCallback.listElementKeywordFound("bottom");
			this.model.codeCompleterCallback.listElementKeywordFound("left");
			this.model.codeCompleterCallback.listElementKeywordFound("right");
			this.model.codeCompleterCallback.listElementKeywordFound("center_vertical");
			this.model.codeCompleterCallback.listElementKeywordFound("fill_vertical");
			this.model.codeCompleterCallback.listElementKeywordFound("center_horizontal");
			this.model.codeCompleterCallback.listElementKeywordFound("fill_horizontal");
			this.model.codeCompleterCallback.listElementKeywordFound("center");
			this.model.codeCompleterCallback.listElementKeywordFound("fill");
			this.model.codeCompleterCallback.listElementKeywordFound("clip_vertical");
			this.model.codeCompleterCallback.listElementKeywordFound("clip_horizontal");
			this.model.codeCompleterCallback.listElementKeywordFound("start");
			this.model.codeCompleterCallback.listElementKeywordFound("end");

			return ;
		}

		if ( identifier == this.model.identifierSpace.get("allowSingleTap") 
			|| identifier == this.model.identifierSpace.get("layout_centerInParent") 
			|| identifier == this.model.identifierSpace.get("layout_centerHorizontal")
			|| identifier == this.model.identifierSpace.get("layout_centerVertical") 
			|| identifier == this.model.identifierSpace.get("layout_alignParentTop") 
			|| identifier == this.model.identifierSpace.get("layout_alignParentStart")
			|| identifier == this.model.identifierSpace.get("layout_alignParentRight") 
			|| identifier == this.model.identifierSpace.get("layout_alignParentEnd") 
			|| identifier == this.model.identifierSpace.get("layout_alignParentLeft") 
			|| identifier == this.model.identifierSpace.get("layout_alignParentBottom")
			|| identifier == this.model.identifierSpace.get("isIndicator") 
			|| identifier == this.model.identifierSpace.get("indeterminate") 
			|| identifier == this.model.identifierSpace.get("cropToPadding")
			|| identifier == this.model.identifierSpace.get("baselineAlignBottom")
			|| identifier == this.model.identifierSpace.get("adjustViewBounds")
			|| identifier == this.model.identifierSpace.get("fillViewport") 
			|| identifier == this.model.identifierSpace.get("useDefaultMargins") 
			|| identifier == this.model.identifierSpace.get("rowOrderPreserved") 
			|| identifier == this.model.identifierSpace.get("columnOrderPreserved")
			|| identifier == this.model.identifierSpace.get("stretchColumns")
			|| identifier == this.model.identifierSpace.get("shrinkColumns") 
			|| identifier == this.model.identifierSpace.get("measureAllChildren") 
			|| identifier == this.model.identifierSpace.get("measureWithLargestChild") 
			|| identifier == this.model.identifierSpace.get("baselineAligned") 
			|| identifier == this.model.identifierSpace.get("clipToPadding") 
			|| identifier == this.model.identifierSpace.get("clipChildren") ) {

			this.model.codeCompleterCallback.listElementKeywordFound("true");
			this.model.codeCompleterCallback.listElementKeywordFound("false");

			return ;
		}

		if ( identifier == this.model.identifierSpace.get("ignoreGravity")
			|| identifier == this.model.identifierSpace.get("handle") 
			|| identifier == this.model.identifierSpace.get("content") 
			|| identifier == this.model.identifierSpace.get("layout_alignLeft") 
			|| identifier == this.model.identifierSpace.get("layout_alignRight")
			|| identifier == this.model.identifierSpace.get("layout_alignStart") 
			|| identifier == this.model.identifierSpace.get("layout_alignEnd")
			|| identifier == this.model.identifierSpace.get("layout_alignTop") 
			|| identifier == this.model.identifierSpace.get("layout_alignBottom") 
			|| identifier == this.model.identifierSpace.get("layout_alignBaseline") 
			|| identifier == this.model.identifierSpace.get("layout_toStartOf") 
			|| identifier == this.model.identifierSpace.get("layout_toLeftOf")
			|| identifier == this.model.identifierSpace.get("layout_toEndOf")
			|| identifier == this.model.identifierSpace.get("layout_toRightOf") 
			|| identifier == this.model.identifierSpace.get("layout_above")
			|| identifier == this.model.identifierSpace.get("layout_below") ) {


			this.model.codeCompleterCallback.listElementKeywordFound("@id/");
			return;

		}

		if ( identifier != this.model.identifierSpace.get("background") 
			&& identifier != this.model.identifierSpace.get("src") 
			&& identifier != this.model.identifierSpace.get("thumb") 
			&& identifier != this.model.identifierSpace.get("track")
			&& identifier != this.model.identifierSpace.get("popupBackground") ) {
			this.model.codeCompleterCallback.listElementKeywordFound("@drawable/");
			return;
		}
		if ( identifier == this.model.identifierSpace.get("prompt") 
			|| identifier == this.model.identifierSpace.get("textOn")
			|| identifier == this.model.identifierSpace.get("textOff")
			|| identifier == this.model.identifierSpace.get("text") ) {

			this.model.codeCompleterCallback.listElementKeywordFound("@string/");
			return;

		}

		if ( identifier == this.model.identifierSpace.get("textStyle") ) {
			this.model.codeCompleterCallback.listElementKeywordFound("normal");
			this.model.codeCompleterCallback.listElementKeywordFound("bold");
			this.model.codeCompleterCallback.listElementKeywordFound("italic");
			return;
		}
		if ( identifier == this.model.identifierSpace.get("typeface") ) {
			this.model.codeCompleterCallback.listElementKeywordFound("normal");
			this.model.codeCompleterCallback.listElementKeywordFound("sans");
			this.model.codeCompleterCallback.listElementKeywordFound("serif");
			this.model.codeCompleterCallback.listElementKeywordFound("monospace");
			return;
		}
		if ( identifier == this.model.identifierSpace.get("ellipsize") ) {
			this.model.codeCompleterCallback.listElementKeywordFound("none");
			this.model.codeCompleterCallback.listElementKeywordFound("start");
			this.model.codeCompleterCallback.listElementKeywordFound("middle");
			this.model.codeCompleterCallback.listElementKeywordFound("end");
			this.model.codeCompleterCallback.listElementKeywordFound("marquee");
			return;
		}
		if ( identifier == this.model.identifierSpace.get("inputType") ) {
			this.model.codeCompleterCallback.listElementKeywordFound("none");
			this.model.codeCompleterCallback.listElementKeywordFound("text");
			this.model.codeCompleterCallback.listElementKeywordFound("textCapCharacters");
			this.model.codeCompleterCallback.listElementKeywordFound("textCapWords");
			this.model.codeCompleterCallback.listElementKeywordFound("textCapSentences");
			this.model.codeCompleterCallback.listElementKeywordFound("textAutoCorrect");
			this.model.codeCompleterCallback.listElementKeywordFound("textAutoComplete");
			this.model.codeCompleterCallback.listElementKeywordFound("textMultiLine");
			this.model.codeCompleterCallback.listElementKeywordFound("textImeMultiLine");
			this.model.codeCompleterCallback.listElementKeywordFound("textNoSuggestions");
			this.model.codeCompleterCallback.listElementKeywordFound("textUri");
			this.model.codeCompleterCallback.listElementKeywordFound("textEmailAddress");
			this.model.codeCompleterCallback.listElementKeywordFound("textEmailSubject");
			this.model.codeCompleterCallback.listElementKeywordFound("textShortMessage");
			this.model.codeCompleterCallback.listElementKeywordFound("textLongMessage");
			this.model.codeCompleterCallback.listElementKeywordFound("textPersonName");
			this.model.codeCompleterCallback.listElementKeywordFound("textPostalAddress");
			this.model.codeCompleterCallback.listElementKeywordFound("textPassword");
			this.model.codeCompleterCallback.listElementKeywordFound("textVisiblePassword");
			this.model.codeCompleterCallback.listElementKeywordFound("textWebEditText");
			this.model.codeCompleterCallback.listElementKeywordFound("textFilter");
			this.model.codeCompleterCallback.listElementKeywordFound("textPhonetic");
			this.model.codeCompleterCallback.listElementKeywordFound("textWebEmailAddress");
			this.model.codeCompleterCallback.listElementKeywordFound("textWebPassword");
			this.model.codeCompleterCallback.listElementKeywordFound("number");
			this.model.codeCompleterCallback.listElementKeywordFound("numberSigned");
			this.model.codeCompleterCallback.listElementKeywordFound("numberDecimal");
			this.model.codeCompleterCallback.listElementKeywordFound("numberPassword");
			this.model.codeCompleterCallback.listElementKeywordFound("phone");
			this.model.codeCompleterCallback.listElementKeywordFound("datetime");
			this.model.codeCompleterCallback.listElementKeywordFound("date");
			this.model.codeCompleterCallback.listElementKeywordFound("time");
			return;
		}
    }
}



