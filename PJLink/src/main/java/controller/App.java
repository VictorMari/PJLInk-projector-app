package controller;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Projector p = new Projector("172.24.2.50");
        p.turnProjectorOff();
    }
}
