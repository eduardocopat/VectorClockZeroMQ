import org.fusesource.jansi.AnsiConsole;

import java.util.Vector;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;

public class VectorPrinter {
    private final Vector<Integer> vector;
    private Integer myIndex;

    public VectorPrinter(Vector<Integer> vector, Integer myIndex) {
        this.vector = vector;
        this.myIndex = myIndex;
    }

    public void print(){
        AnsiConsole.systemInstall();

        System.out.println(".......................");
        System.out.print("My vector is now: {");
        for (int i = 0; i < vector.size(); i++) {
            if(i == (int) myIndex)
                System.out.print(ansi().fg(GREEN).a(vector.get(i).toString()).reset());
            else
                System.out.print(vector.get(i));

            if (i + 1 == vector.size())
                System.out.print("}");
            else
                System.out.print(",");
        }
        System.out.println();
        System.out.println(".......................");

        AnsiConsole.systemUninstall();
    }

}
