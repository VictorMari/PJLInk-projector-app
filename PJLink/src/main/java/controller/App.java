package controller;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args )
    {
        Projector p = new Projector("::1");
        System.out.println(p.getProjectorClass());

    }

}
