package highlightUnit;
import java.awt.Color;
import java.util.LinkedList;

import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import ide.utils.Editor;
public class TheFramework{
	private static final LinkedList<Highlight> highlights = new LinkedList<>();
	public static void checkForErrors(Editor textArea){
		highlights.forEach(h->h.remove());
		highlights.clear();
		textArea.repaint();
		String text = textArea.getText();
		String line = "";
		if(text.contains("\n"))
			line = text.substring(0, text.indexOf('\n'));
		//Checking For ';'
		for(int i = 0; i < text.length(); i++){
			char ch0 = text.charAt(i);
			if(ch0 == '\n'){
				try{
					line = text.substring(i + 1, text.indexOf('\n', i + 1));
				}catch(Exception e){}
			}
			checkForSemiColons(i, ch0, line, text, textArea);
		}
	}

	public static void checkForSemiColons(int i, char ch0, String line, String text, Editor textArea){
		if(line.contains("while") || line.contains("for") || line.contains("if") ||
				line.trim().startsWith("//") || line.trim().startsWith("/*") || line.trim().startsWith("*")
				|| line.trim().endsWith("*/")){
			return;
		}
		else if(ch0 == ')'){
			if(i + 1 >= text.length()){
				int pos = i;
				addHighlightAt(pos, textArea);
			}
			else {
				char nextChar = '\u0000';
				inner:
					for(int nextI = i + 1; nextI < text.length(); nextI++){
						nextChar = text.charAt(nextI);
						if(nextChar != '\n' && nextChar != ' '){
							if(isSemiColonNeeded(nextChar)){
								int pos = i;
								addHighlightAt(pos, textArea);
							}
							break inner;
						}
					}
			}
		}
	}

	public static void addHighlightAt(int pos, Editor textArea){
		DefaultHighlightPainter painter = new DefaultHighlightPainter(Color.BLUE);
		try{
			textArea.getHighlighter().addHighlight(pos, pos + 1, painter);
			highlights.add(new Highlight(textArea, painter, pos, pos + 1));
		}catch(Exception e){e.printStackTrace();}
	}

	public static char getNextChar(int i, String text){
		char nextChar = '\u0000';
			for(int nextI = i + 1; nextI < text.length(); nextI++){
				nextChar = text.charAt(nextI);
				if(nextChar != '\n' && nextChar != ' '){
					return nextChar;
				}
			}
		return nextChar;
	}

	public static boolean isSemiColonNeeded(char c){
		return !"){+-*/%\'\",.;&|!".contains(c+"");
	}
}
