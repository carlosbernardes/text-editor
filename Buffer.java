package TextEditor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

public class Buffer {
    private Position cursor;
    private ArrayList<StringBuilder> lineList = new ArrayList<StringBuilder>();
    private LinkedList<Buffer> undos = new LinkedList<Buffer>();
    private RandomAccessFile file = null;
    private boolean modified = false;
    private Path savePath = null;

    public class InvalidCharException extends RuntimeException {

    }

    public class InvalidLineException extends RuntimeException {

    }

    public class InvalidColumnException extends RuntimeException {
        //TO DO
    }

    public Buffer() {
        cursor = new Position(0, 0);
        StringBuilder s = new StringBuilder("");
        lineList.add(s);
    }

    /**
     * Get the Line at index
     *
     * @param index line number
     * @return String a line
     */
    public String getLine(int index) {
        return lineList.get(index).toString();
    }

    /**
     * Insert a Char at the Cursor position
     *
     * @param c a Char
     */
    public void insert(char c) {
        if (c != '\n') {
            lineList.get(cursor.x).insert(cursor.y, c);
            cursor.y++;
        } else {
            insertLn();
            cursor.y = 0;
        }
    }

    /**
     * Break the line at the cursor position
     */
    public void insertLn() {
        StringBuilder line = lineList.get(cursor.x);
        lineList.add(cursor.x + 1, new StringBuilder(line.substring(cursor.y, line.length())));
        lineList.get(cursor.x).delete(cursor.y, line.length()); // apagar a quebra da linha anterior
        cursor.x++;
        cursor.y = lineList.get(cursor.x).length();
    }

    /**
     * Insert a string at the current position
     *
     * @param str a string to add
     */
    public void insertString(String str) throws InvalidCharException {
        if (str.contains("\n"))
            throw new InvalidCharException();
        else {
            StringBuilder line = lineList.get(cursor.x);
            line.insert(cursor.y, str);
            cursor.y += str.length();
        }
    }

    /**
     * Delete the char preceding the current cursor position
     */
    public void deleteChar() {
        if (cursor.y == 0 && cursor.x == 0)
            return;

        StringBuilder line = lineList.get(cursor.x);
        if (cursor.y == 0) { //apagar uma linha em que o cursor-col = 0
            StringBuilder prevLine = lineList.get(cursor.x - 1);
            cursor.y = prevLine.length();
            prevLine.append(line); //cola a linha onde houve o del no 1pos à linha anterior
            lineList.remove(cursor.x);
            cursor.x--;
        } else {
            lineList.get(cursor.x).deleteCharAt(cursor.y - 1);
            cursor.y--;
        }
    }

    /**
     * Move the cursor to the previous (logical) line
     */
    public void moveUp() {
        if (getCursorLine() != 0) {
            setCursorLineTo(getCursorLine() - 1);
            if (lineList.get(getCursorLine()).length() < getCursorColumn())
                setCursorColumnTo(lineList.get(getCursorLine()).length());
        }
    }

    /**
     * Move the cursor to the next (logical) line
     */
    public void moveDown() {
        if (cursor.x != lineList.size() - 1)
            cursor.x++;
        else
            cursor.y = lineList.get(cursor.x).length();

        //se a linha abaixo tiver um comp maior que a pos actual do cursor
        if (lineList.get(cursor.x).length() < cursor.y)
            cursor.y = lineList.get(cursor.x).length();
        else {
            if (cursor.x != lineList.size() - 1)
                cursor.y = lineList.get(cursor.x).length();
        }

    }

    /**
     * Move the cursor (cursor.y) one position to the left
     */
    public void moveLeft() {
        if (cursor.y == 0 && cursor.x == 0)
            return;
        if (cursor.y == 0) {
            cursor.x--;
            cursor.y = lineList.get(cursor.x).length();
        } else
            cursor.y--;
    }

    /**
     * Move the cursor (cursor.y) one position to the right
     */
    public void moveRight() {
        if (cursor.x == lineList.size() - 1 && cursor.y == lineList.get(cursor.x).length())
            return;
        if (lineList.get(cursor.x).length() == cursor.y) {
            cursor.x++;
            cursor.y = 0;
        } else
            cursor.y++;
    }

    /**
     * Creates a new Line by breaking it (current line) at the cursor position
     */
    public void newLine() {
        lineList.add(cursor.x + 1, new StringBuilder());
        cursor.x++;
        cursor.y = 0;
    }

    /**
     * Returns the size of the lineList.
     */
    public int getSize() {
        return this.lineList.size();
    }

    /**
     * Returns the cursor line
     */
    public int getCursorLine() {
        return cursor.x;
    }

    /**
     * Returns the cursor column
     */
    public int getCursorColumn() {
        return cursor.y;
    }


    /**
     * Sets the cursor.x to a position in choice ||
     */
    public void setCursorLineTo(int i) throws InvalidLineException {
        if (this.getSize() <= i) throw new InvalidLineException();
        cursor.x = i;
    }

    /**
     * Sets the cursor.y to a position in choice ||
     */
    public void setCursorColumnTo(int i) {
        if (this.getI(cursor.x).length() < i) throw new InvalidColumnException();
        cursor.y = i;
    }

    /**
     * Kills the i-nesim line ||
     */
    public void killLine(int i) {
        if (lineList.size() == 1) {
            lineList.remove(0);
            lineList.add(new StringBuilder(""));
            setCursorColumnTo(0);
            setCursorLineTo(0);
        } else if (i > lineList.size()) throw new InvalidLineException();
        else if (i == getSize() - 1) {
            lineList.remove(i);
            setCursorLineTo(i - 1);
        } else {
            lineList.remove(i);
        }
    }

    /**
     * Returns the i-nesim line
     */
    public StringBuilder getI(int i) throws InvalidLineException {
        if (i >= this.getSize()) throw new InvalidLineException();
        return this.lineList.get(i);
    }

    /**
     * Returns the linelist of buffer
     */
    public ArrayList<StringBuilder> getList() {
        return this.lineList;
    }


    /**
     * Saves the file. Do not work with unsaved files (needs fix)
     */
    public void save() throws IOException {
            file = new RandomAccessFile(savePath.toFile(), "rw");
            file.setLength(0);
            for (int i = 0; i < getSize(); i++) {
                String s = getLine(i);
                file.writeBytes(s);
                file.writeBytes("\n");
            }
    }

    /**
     * Opens file from path given by user
     */
    public void open(Path path) throws IOException {
        savePath=path;
        file = new RandomAccessFile(path.toFile(), "rw");
        String s = file.readLine();
        while (s != null) {
            insertString(s);
            insertLn();
            s = file.readLine();
        }
    }
}
