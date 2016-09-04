package TextEditor;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Random;

public class BufferTest extends TestCase{
    static String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    static int lenght = 500;
    static int seed = 56;

    @Test
    public void testInsert() throws Exception {
        Buffer b = new Buffer();
        b.insert('\n');
        assertEquals(Integer.valueOf(2), Integer.valueOf(b.getSize()));
        b.insert('c');
        assertEquals( "c" , b.getLine(b.getCursorLine()) );
    }

    @Test
    public void testMoveUp() throws Exception {
        Buffer b = genBuffer(lenght);

        for (int i = b.getSize()-1; i >= 0; i--) {
            assertEquals(b.getCursorLine(), i);

               if(i > 0 && i < b.getSize()-1)
                    assertEquals( b.getI(i-1).length() , b.getCursorColumn() );

            b.moveUp();
        }
        assertEquals(0 , b.getCursorLine());
    }

    @Test
    public void testMoveDown() throws Exception {
        Buffer b = genBuffer(lenght);
        b.setCursorLineTo(0);
        for(int i=0; i < b.getSize()-1; i++){
            assertEquals(b.getCursorLine() , i);
            if( i > 0 )
                assertEquals( b.getI(i).length() , b.getCursorColumn() );
            b.moveDown();
        }
        assertEquals( b.getSize()-1 , b.getCursorLine() );

        b.moveDown();
        assertEquals( b.getCursorColumn() , b.getI(b.getSize()-1).length() );
    }

    @Test
    public void testMoveLeft() throws Exception {
        Buffer b = new Buffer();

        b.moveLeft();
        assertEquals(b.getCursorColumn() , 0);
        assertEquals(b.getCursorLine() , 0);

        b.getList().add(1 , new StringBuilder() );
        b.moveLeft();
        assertEquals(b.getCursorColumn() , 0);
        assertEquals(b.getCursorLine() , 0);
    }

    @Test
    public void testMoveRight() throws Exception {
        Buffer b = new Buffer();
        b.moveLeft();
        assertEquals(b.getCursorColumn() , 0);
        assertEquals(b.getCursorLine() , 0);

        b.newLine();
        b.moveRight();
        assertEquals(b.getCursorColumn() , 0);
        assertEquals(b.getCursorLine() , 1);

        b.insertString("Laboratório de Programação");
        for( int i=0; i < 10; i++ )
            b.moveLeft();

        assertEquals(b.getCursorColumn() , 16);
        assertEquals(b.getCursorLine() , 1);
    }

    @Test
    public void testInsertLn() throws Exception {
        Buffer b = new Buffer();

        b.insertString("Laboratório de Programação");
        assertEquals(b.getSize(),1);

        for( int i=0; i<11; i++)
            b.moveLeft();

        assertEquals(b.getCursorColumn(),15);
        assertEquals(b.getCursorLine(),0);
        b.insertLn();
        assertEquals(b.getCursorColumn(),b.getLine(1).length());
        assertEquals(b.getCursorLine(), 1);

        b.setCursorLineTo(1);
        b.setCursorColumnTo(0);
        b.insertLn();
        assertEquals(b.getLine(1).length(), 0);
        assertEquals("", b.getLine(1));
        assertEquals(b.getLine(2).length(), 11);
        assertEquals("Programação" , b.getLine(2));
    }

    @Test
    public void testDeleteChar() throws Exception {
        Buffer b = new Buffer();

        b.deleteChar();
        assertEquals("", b.getLine(0));

        b.insertString("Laboratório de Programação");
        b.deleteChar();
        assertEquals("Laboratório de Programaçã",b.getLine(0));

        b.getList().add(1,new StringBuilder());
        b.setCursorColumnTo(0);
        b.setCursorLineTo(1);
        b.deleteChar();
        assertEquals("Laboratório de Programaçã",b.getLine(0));

    }

    public static Buffer genBuffer(int lenght){
        Buffer b = new Buffer();
        for ( int i=0 ; i < lenght; i++){
            b.insertString(genString());
            b.newLine();
        }
        return b;
    }

    public static String genString(){
        Random r = new Random(seed++);
        int len = r.nextInt(lenght);
        StringBuilder str = new StringBuilder(lenght);
            for( int i = 0; i < len; i++ )
                str.append( chars.charAt( r.nextInt(chars.length()) ) );
        return str.toString();
        }

    @Test
    public void testInsertString() throws Exception {
        Buffer b = new Buffer();
        Throwable e = null;

        b.insertString("Laboratório");
        assertEquals(b.getCursorColumn(),11);
        b.insertString(" de");
        assertEquals(b.getCursorColumn(),14);
        b.insertString(" Programação");
        assertEquals(b.getCursorColumn(), 26);
        assertEquals(b.getCursorColumn(),14);
        assertEquals(b.getCursorLine(), 0);

        try {
            b.insertString("\n");
        }
        catch (Throwable ex) {
            e=ex;
        }
        assertTrue(e instanceof Buffer.InvalidCharException);
    }

    @Test
    public void testNewLine() throws Exception {
        Buffer b = new Buffer();
        b.newLine();
        assertEquals(b.getSize(), 2);
    }

    @Test
    public void testGetSize() throws Exception {
        Buffer b = new Buffer();
        assertEquals(b.getSize(), 1);
        b.newLine();
        b.newLine();
        assertEquals(b.getSize(), 3);
    }

    @Test
    public void testGetCursorLine() throws Exception {
        Buffer b = new Buffer();
        assertEquals(b.getCursorLine(), 0);
        b.newLine();
        assertEquals(b.getCursorLine(), 1);
    }

    @Test
    public void testGetCursorColumn() throws Exception {
        Buffer b = new Buffer();
        Throwable e = null;
        assertEquals(b.getCursorColumn(), 0);
        b.insertString("Laboratório de Programação");
        assertEquals(b.getCursorColumn(), 26);
    }

    @Test
    public void testSetCursorLineTo() throws Buffer.InvalidLineException {
      Throwable e = null;
      Buffer b = new Buffer();
      b.insertString("Laboratório de Programação");
      b.insertLn();
      assertEquals(b.getCursorLine(),1);
      b.setCursorLineTo(0);
      assertEquals(b.getCursorLine(),0);
      try {
            b.setCursorLineTo(2);
        } catch (Throwable ex){
            e=ex;
        }
        assertTrue(e instanceof Buffer.InvalidLineException);
    }
    @Test (expected = Buffer.InvalidColumnException.class)
    public void testSetCursorColumnTo()throws Buffer.InvalidColumnException {
        Throwable e = null;
        Buffer b = new Buffer();
        b.insertString("Laboratório de Programação");
        assertEquals(b.getCursorColumn(),26);
        b.setCursorColumnTo(20);
        assertEquals(b.getCursorColumn(),20);
         try {
            b.setCursorColumnTo(28);
        } catch (Throwable ex){
            e=ex;
        }
        assertTrue(e instanceof Buffer.InvalidColumnException);
    }
}