package br.com.legacy.classes;

/**
 * Created by renatorodrigues on 14/12/15.
 */
public class Boo {

    private String booMethod(Other other){

        System.out.println("boo method with other object parameter executed!");

        return other.toString();
    }

    public Other booMethod(){

        System.out.println("boo method executed!");

        return new Other();
    }
}
