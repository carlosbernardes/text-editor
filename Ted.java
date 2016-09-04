package TextEditor;
import java.io.*;
import java.nio.file.Paths;

public class Ted {

    public static void main(String[] args) throws IOException {
        if (args.length > 0)
        {
            Buffer b = new FileBuffer();
            b.open(Paths.get(args[0]));
            new BufferView(b);
        }
        else new BufferView();
    }
}