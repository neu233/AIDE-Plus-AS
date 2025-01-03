package abcd;

import com.aide.common.AppLog;
import com.aide.common.KeyStroke;
import com.aide.ui.AIDEEditor;
import com.aide.ui.AIDEEditorExtend;
import com.aide.ui.AIDEEditorPager;
import com.aide.ui.ServiceContainer;
import com.aide.ui.command.KeyStrokeCommand;
import com.aide.ui.command.MenuItemCommand;
import com.aide.ui.rewrite.R;
import com.aide.ui.util.FileSpan;
import com.aide.ui.util.FileSystem;
import com.aide.ui.views.editor.EditorModel;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.jface.text.BadLocationException;

/**
 * 代码格式化
 */
public class ma implements MenuItemCommand, KeyStrokeCommand {

	public ma() {

	}

	public KeyStroke getKeyStroke() {
		return new KeyStroke(34, true, true, false);
	}

	public int getMenuItemId() {
		return R.id.editorMenuFormatCode;
	}

	public String getName() {
		return "Format Code";
	}

	public boolean isEnabled() {
		if (ServiceContainer.getOpenFileService().Ws()) {
			if (ServiceContainer.getOpenFileService().XL()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean run() {
		AIDEEditorPager aideEditorPager = ServiceContainer.getMainActivity().getAIDEEditorPager();

		if (ZeroAicySetting.isEnableEclipseJavaFormat()) {
			String currentFilePath = aideEditorPager.getVisibleFile();
			if (currentFilePath == null) {
				return true;
			}

			String suffixName = FileSystem.getSuffixName(currentFilePath).toLowerCase();

			if ("java".equals(suffixName)) {
				// 
				AIDEEditor currentEditor = AIDEEditorExtend.getCurrentEditor(aideEditorPager);
				ThreadPoolService defaultThreadPoolService = ThreadPoolService.getDefaultThreadPoolService();
				defaultThreadPoolService.submit(new FormatterRunnable(currentEditor));
				return true;
			}
		}

		FileSpan currentFileSpan = aideEditorPager.getCurrentFileSpan();

		ServiceContainer.getMainActivity().delayedShowAnalyzingProgressDialog();

		// selectionStartLine
		int startLine = currentFileSpan.DW;
		// selectionEndLine
		int endLine = currentFileSpan.Hw;

		if (startLine == endLine) {
			endLine = ServiceContainer.getMainActivity().getAIDEEditorPager().getCurrentFileLineCount();
			startLine = 1;
		}
		ServiceContainer.getEngineService().Hw(ServiceContainer.getOpenFileService().getVisibleFile(), startLine,
				endLine, ServiceContainer.getMainActivity().getAIDEEditorPager().getTabSize());
		return true;
	}

	public static class FormatterRunnable implements Runnable {

		AIDEEditor currentEditor;

		public FormatterRunnable(AIDEEditor currentEditor) {
			this.currentEditor = currentEditor;
		}

		@Override
		public void run() {
			try {
				synchronized (currentEditor) {
					final AIDEEditor.t editorModel = AIDEEditorExtend.getEditorModel(currentEditor);
					if (editorModel == null) {
						return;
					}
					synchronized (editorModel) {
						format(editorModel);
					}
				}
			} catch (Throwable e) {
				AppLog.e("Format Code", e);
			}

		}

		private void format(AIDEEditor.t editorModel) throws MalformedTreeException, BadLocationException {
			EditorModel.h h = editorModel.pN(null);
			String inputText = String.valueOf(h.j6, 0, h.DW);

			int indentationLevel = 0;
			String lineSeparator = "\n";
			IDocument doc = new Document(inputText);

			DefaultCodeFormatterOptions options = DefaultCodeFormatterOptions.getDefaultSettings();
			DefaultCodeFormatter codeFormatter = new DefaultCodeFormatter(options);

			int kind = CodeFormatter.K_COMPILATION_UNIT;
			// 修改增量 基于 charArray offset
			TextEdit edit = codeFormatter.format(kind, inputText, 0, inputText.length(), indentationLevel,
					lineSeparator);
			if (edit == null) {
				return;
			}
			// 提交修改
			edit.apply(doc);

			String outputText = doc.get();
			// 在主线程修改 EditorModel
			currentEditor.post(new ApplyTextRunnable(currentEditor, outputText));
		}

	}

	public static class ApplyTextRunnable implements Runnable {
		AIDEEditor currentEditor;
		String formatterOutputText;
		public ApplyTextRunnable(AIDEEditor currentEditor, String formatterOutputText) {
			this.currentEditor = currentEditor;
			this.formatterOutputText = formatterOutputText;
		}

		@Override
		public void run() {
			synchronized (currentEditor) {
				final AIDEEditor.t editorModel = AIDEEditorExtend.getEditorModel(currentEditor);
				if (editorModel == null) {
					return;
				}
				synchronized (editorModel) {
					String outputText = this.formatterOutputText;

					int startLine = 1;
					int startColumn = 1;

					int endLine = editorModel.getLineCount();

					int endColumn = editorModel.getColumnCount(endLine);

					// 替换 行列皆以 1开始
					editorModel.cb(startLine, startColumn, endLine + 1, endColumn + 1, outputText, false, true);
				}
			}
		}

	}

	//									if( !true ){
	//										// 废弃
	//										ServiceContainer.getRefactoringService().DW( test(currentFilePath, inputText, editorModel, edit));
	//										return;
	//									}

	//	public List<Modification> test(String filePath, String inputText, EditorModel editorModel, TextEdit edit) {
	//		int lineCount = editorModel.getLineCount();
	//
	//		int[] lineEnds = new int[lineCount];
	//		// 构造 lines
	//		for (int line = 0, offset = 0; line < lineCount; line++) {
	//			// 最后一位是换行符
	//			offset += editorModel.getColumnCount(line) + 1;
	//			lineEnds[line] = offset;
	//		}
	//		System.out.println(Arrays.toString(lineEnds));
	//
	//		AppLog.println_d("****************************************");
	//		AppLog.println_d(inputText);
	//		AppLog.println_d("****************************************");
	//
	//		List<Modification> modifications = new ArrayList<>();
	//		// 都是 ReplaceEdit
	//		for (TextEdit child : edit.getChildren()) {
	//			if (!(child instanceof ReplaceEdit)) {
	//				continue;
	//			}
	//
	//			ReplaceEdit replaceEdit = (ReplaceEdit) child;
	//
	//			int startOffset = replaceEdit.getOffset();
	//
	//			int replaceEditLength = replaceEdit.getLength();
	//			if (replaceEditLength == 0) {
	//				continue;
	//			}
	//			int endOffset = startOffset + replaceEditLength;
	//
	//			if (endOffset < startOffset) {
	//				int temp = endOffset;
	//				endOffset = startOffset;
	//				startOffset = temp;
	//			}
	//
	//			int startLine = startOffset >= 0 ? Util.getLineNumber(startOffset, lineEnds, 0, lineEnds.length - 1) : 0;
	//			int startColumn = startOffset >= 0 ? Util.searchColumnNumber(lineEnds, startLine, startOffset) : 0;
	//
	//			int endLine = endOffset >= 0 ? Util.getLineNumber(endOffset, lineEnds, 0, lineEnds.length - 1) : 0;
	//
	//			int endColumn = endOffset >= 0 ? Util.searchColumnNumber(lineEnds, endLine, endOffset) : 0;
	//			// 
	//			endColumn += 1;
	//
	//			AppLog.println_d("startOffset %s, [%s, %s]", startOffset, startLine, startColumn);
	//			AppLog.println_d("endOffset %s, [%s, %s]", endOffset, endLine, endColumn);
	//			AppLog.println_d(child.toString());
	//
	//			// 从 1开始
	//			//*
	//			// startLine += 1;
	//			// endLine += 1;
	//
	//			// startColumn += 1;
	//			//*/
	//			Modification modification = new Modification(startLine, startColumn, endLine, endColumn, filePath,
	//					replaceEdit.getText(), false) {
	//			};
	//			System.out.println(modification);
	//
	//			modifications.add(modification);
	//			System.out.println();
	//		}
	//		return modifications;
	//	}

}

