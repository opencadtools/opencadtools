package com.iver.cit.gvsig.gui.tokenmarker;

import javax.swing.JFrame;
import javax.swing.text.Segment;

import com.iver.utiles.console.JConsole;
import com.iver.utiles.console.jedit.KeywordMap;
import com.iver.utiles.console.jedit.Token;
import com.iver.utiles.console.jedit.TokenMarker;

/**
 * Console token marker.
 * 
 * @author Vicente Caballero Navarro
 */
public class ConsoleToken extends TokenMarker {

    public ConsoleToken(KeywordMap keywordMap) {
	keywords = keywordMap;
    }

    public static void main(String[] args) {
	JConsole console = new JConsole();
	// console.addText("-", 0);
	KeywordMap keywordMap = new KeywordMap(true);
	keywordMap.add("hola", Token.LABEL);
	ConsoleToken consoleToken = new ConsoleToken(keywordMap);
	console.setTokenMarker(consoleToken);
	JFrame frame = new JFrame();
	frame.getContentPane().add(console);
	frame.pack();
	frame.setVisible(true);
    }

    // private members
    private KeywordMap keywords;

    private int lastOffset;
    private int lastKeyword;

    @Override
    public byte markTokensImpl(byte token, Segment line, int lineIndex) {
	char[] array = line.array;
	int offset = line.offset;
	lastOffset = offset;
	lastKeyword = offset;
	int length = line.count + offset;

	loop: for (int i = offset; i < length; i++) {
	    int i1 = (i + 1);

	    char c = array[i];

	    switch (token) {
	    case Token.NULL:
		switch (c) {
		case '@':
		    addToken(i - lastOffset, token);
		    addToken(length - i, Token.KEYWORD2);
		    token = Token.NULL;
		    lastOffset = lastKeyword = length;
		    break loop;

		case '#':
		    doKeyword(line, i, c);
		    addToken(i - lastOffset, token);
		    token = Token.LABEL;
		    lastOffset = lastKeyword = i;
		    break;
		case '*':
		    doKeyword(line, i, c);
		    addToken(i - lastOffset, token);
		    token = Token.KEYWORD3;
		    lastOffset = lastKeyword = i;
		    break;

		case ' ':
		    doKeyword(line, i, c);
		}
		break;
	    case Token.LABEL:
		if (c == '>') {
		    addToken(i1 - lastOffset, token);
		    token = Token.NULL;
		    lastOffset = lastKeyword = i1;
		}
		break;
	    case Token.LITERAL2:
		/*
		 * if(c == '\'') { addToken(i1 - lastOffset, Token.LITERAL2);
		 * token = Token.NULL; lastOffset = lastKeyword = i1; }
		 */
		break;
	    case Token.KEYWORD2:
		/*
		 * if(c == '\'') { addToken(i1 - lastOffset, Token.KEYWORD2);
		 * token = Token.NULL; lastOffset = lastKeyword = i1; }
		 */
		break;
	    case Token.KEYWORD3:
		/*
		 * if(c == '\'') { addToken(i1 - lastOffset, Token.KEYWORD3);
		 * token = Token.NULL; lastOffset = lastKeyword = i1; }
		 */
		break;
	    default:
		throw new InternalError("Invalid state: " + token);
	    }
	}

	if (token == Token.NULL)
	    doKeyword(line, length, '\0');

	switch (token) {
	case Token.LABEL:
	case Token.LITERAL2:
	    addToken(length - lastOffset, token);
	    token = Token.NULL;
	    break;
	default:
	    addToken(length - lastOffset, token);
	    break;
	}

	return token;
    }

    private boolean doKeyword(Segment line, int i, char c) {
	int i1 = i + 1;

	int len = i - lastKeyword;
	byte id = keywords.lookup(line, lastKeyword, len);
	if (id != Token.NULL) {
	    if (lastKeyword != lastOffset)
		addToken(lastKeyword - lastOffset, Token.NULL);
	    addToken(len, id);
	    lastOffset = i;
	}
	lastKeyword = i1;
	return false;
    }
}
