package TextEditor;


public class FileBuffer extends Buffer {
    private boolean modified = false;

    FileBuffer() {
    }

    @Override
    public void insert(char c) {
        super.insert(c);
        this.modified = true;
    }

}