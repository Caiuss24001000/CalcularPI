package Generales;

import java.net.Socket;

public class ServicioCliente {
    
    
    public static Socket reintento(String direccion, int puerto) throws Exception{
        Socket conexion = null;
        
        for(;;)
        try {
            conexion = new Socket(direccion, puerto);
            break;
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Esperando conexi\u00f3n del puerto "+puerto+" ...\n");
            Thread.sleep(1000);
        }
        
        return conexion;
    }
}
