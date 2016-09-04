package TextEditor;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.nio.file.Paths;

public class BufferView {

    private static Terminal term;
    private Buffer buf;
    private Buffer buf1;
    private Buffer buf2;
    private int bufferToUse=0;
    private int firstLine=0;
    private int fixedCol=0;
    private boolean hasName=false;
    public BufferView() throws IOException {
        term = TerminalFacade.createTerminal();
        term.setCursorVisible(true);
        term.enterPrivateMode();
        this.buf = new Buffer();
        this.buf1 = new Buffer();
        this.buf2 = new Buffer();
        do
        {
            Key k = readInput();
            handleKey(k);
        }while(true);

    }

    public BufferView(Buffer buf) throws IOException {
        term = TerminalFacade.createTerminal();
        term.setCursorVisible(true);
        term.enterPrivateMode();
        this.buf = buf;
        hasName=true;
        refresh();
        do
        {
            Key k = readInput();
            handleKey(k);
        }while(true);

    }

    /**
     * Cycle until user inputs something
     */
    public Key readInput() {
        Key r = null;
        while (r == null) {
            r = term.readInput();
        }
        return r;
    }

    /**
     * Handles keys from user input
     */

    public void handleKey(Key k) throws IOException {

        System.out.println(fixedCol);
        switch (k.getKind()) {

            case Tab:
                if(!hasName) usingTab();
                break;
            case Delete:
                buf.moveRight();
                System.out.println("!#");
                buf.deleteChar();
                term.clearScreen();
                break;
            case NormalKey:
                normalKey(k);
                break;
            case Backspace:
                buf.deleteChar();
                term.clearScreen();
                break;
            case ArrowLeft:
                buf.moveLeft();
                break;
            case ArrowRight:
                buf.moveRight();
                break;
            case ArrowUp:
                buf.moveUp();
                term.clearScreen();
                break;
            case ArrowDown:
                buf.moveDown();
                break;
            case Enter:
                buf.insert('\n');
                term.clearScreen();
                break;
        }
        dealWithCursor();
        refresh();
    }

    /**
     * Changes buffer when working in non-saved files (needs fix)
     */
    private void usingTab()
    {
        if(bufferToUse==0)
        {
            buf=buf2;
            bufferToUse=1;
        }
        else if(bufferToUse==1)
        {
            buf=buf1;
            bufferToUse=0;
        }
        term.clearScreen();
    }
    private void normalKey(Key k) throws IOException {
         if(k.isCtrlPressed() && k.getCharacter()=='k')
         {
             buf.killLine(buf.getCursorLine());
             buf.setCursorColumnTo(0);
             term.clearScreen();

         }
         else if(k.isCtrlPressed() && k.getCharacter()=='s')
         {
            if(hasName)buf.save();
         }
        else {
             buf.insert(k.getCharacter());
             term.putCharacter(k.getCharacter());
         }
    }
    /**
     * Sets the limits of the cursor in the terminal
     */
    private void dealWithCursor()
    {
        if(buf.getCursorLine()< firstLine) {
            term.clearScreen();
            firstLine--;
        }

        //limite da barra de stats
        if(buf.getCursorLine() >= firstLine + getTermHeight()-2) {
            term.clearScreen();
            firstLine++;
            //System.out.println(firstLine);
        }

        if(buf.getCursorColumn() < fixedCol) {
            term.clearScreen();
            fixedCol--;
        }

        if(buf.getCursorColumn() >= fixedCol + getTermWidth()) {
            term.clearScreen();
            fixedCol++;
        }
    }

    /**
     * Updates the visual terminal
     */
    private void refresh() {
        int ln = firstLine;
        while(ln < buf.getSize() && ln < firstLine+getTermHeight()){

            term.moveCursor(0, ln - firstLine);
            StringBuilder line = buf.getI(ln);

            int i = fixedCol;

            while(i < line.length() && i < fixedCol + getTermWidth()){
                term.putCharacter(line.charAt(i));
                i++;
            }
            ln++;
        }

        printLineNumber();
        term.moveCursor(buf.getCursorColumn() - fixedCol,buf.getCursorLine() - firstLine);
        term.flush();
    }


    /**
     * Print current buffer stats
     */
    private void printLineNumber()
    {
        for(int i=0;i<getTermWidth();i++)
        {
            term.moveCursor(i,getTermHeight()-2);
            term.putCharacter(ACS.SINGLE_LINE_HORIZONTAL);
        }
        term.moveCursor(0,getTermHeight()-1);
        String s = Integer.toString(buf.getCursorLine());
        term.putCharacter('L');
        for(int i=0;i<s.length();i++) term.putCharacter(s.charAt(i));
        term.putCharacter(' ');
        term.putCharacter(ACS.SINGLE_LINE_VERTICAL);
        String ss = Integer.toString(buf.getCursorColumn());
        term.putCharacter(' ');
        term.putCharacter('C');
        for(int i=0;i<ss.length();i++) term.putCharacter(ss.charAt(i));
        String sss = "            Using buffer " + (bufferToUse+1) +". Press TAB to change";
        if(!hasName)for(int i=0;i<sss.length();i++) term.putCharacter(sss.charAt(i));
    }

    /**
     * Returns the terminal Width
     * @return width of the terminal
     */
    private int getTermWidth() {
        return term.getTerminalSize().getColumns();
    }

    /**
     * Returns the terminal Height
     * @return height of the terminal
     */
    private int getTermHeight() {
        return term.getTerminalSize().getRows();
    }

}