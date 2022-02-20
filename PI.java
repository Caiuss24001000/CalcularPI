import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Generales.ServicioCliente;

public class PI {
    static double pi = 0;
    static Object obj = new Object();
    static int numNodos = 4;

    //Cliente (nodo 0)
    //Conectarse a cada nodo servidor 
    //Esperar resultado de cada nodo servidor (nodo 1-4)
    //PI = la suma de las sumatorias calculadas por los servidores 
    //Desplegar el valor de PI calculado 
    //Terminar el programa
    static class Worker extends Thread{
        int nodo;
        Worker(int nodo){
            this.nodo = nodo;
        }

        public void run(){
            try {
                Socket conexion = ServicioCliente.reintento("localhost", 50000+nodo);

                //DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                
                //Según yo
                synchronized(obj){
                    pi = pi + entrada.readDouble();
                }
                
                conexion.close();
            } catch (Exception e) {
                //TODO: handle exception
                System.out.println("Error al conectarse con el nodo "+this.nodo);
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        //nodo 0 cliente --> nodos 1..4 servidores(se pasan por parámetros)
        //Servidores en nodos 5000x donde x es el nodo servidor

        int nodo = 0;//args

        if(args.length != 1){
            System.out.println("Error!! Parametros incorrectos\nCorrecto uso: java PI <numero entero>");
            System.exit(0);
        }else{
            nodo = Integer.parseInt(args[0]);
            if(nodo > numNodos || nodo < 0){
                System.out.println("Error!! Nodo incorrecto\nEl nodo debe de ser entre 0 y "+numNodos);
                System.exit(0);
            }
        }
        
        if(nodo == 0){
            Worker[] w = crearNodos(numNodos);
            iniciarNodos(w);
            barreraNodos(w);

            System.out.println("El valor de PI es: "+ pi);
        }else{
            crearServidor(nodo);
        }
    }

    static Worker[] crearNodos(int nodos){
        Worker[] w = new Worker[nodos];

        for (int i = 0; i < nodos; i++) {
            w[i] = new Worker(i+1);
        }

        return w;
    }

    static void iniciarNodos(Worker[] w){

        for (int i = 0; i < w.length; i++) {
            w[i].start();
        }
    }

    static void barreraNodos(Worker[] w) throws Exception{

        for (int i = 0; i < w.length; i++) {
            w[i].join();
        }
    }
    //Servidores 
    //Esperar la conexión del nodo cliente --> yes
    //Calcular la suma del millon de terminos que le corresponde --> yes
    //Enviar al cliente el resultado de la suma que le corresponde --> yes
    //Terminar el programa --> yes
    static void crearServidor(int nodo) throws Exception{
        try (ServerSocket servidor = new ServerSocket(50000+nodo)) {

            for(;;){
                Socket conexion = servidor.accept();
            
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            
                salida.writeDouble(sumatoriaLeibniz(nodo));

                conexion.close();
            }
        }catch (Exception e) {
            //TODO: handle exception
            System.out.println("Error en el servidor del nodo "+nodo);
        }
    }
    
    static double sumatoriaLeibniz(int nodo){
        double resultado = 0.0;
        for (int i = 0; i < 1000000; i++) {
            resultado = resultado + 4.0/(8*i+2*(nodo-2)+3);
        }
        if(nodo%2 == 0){
            return -resultado;
        }else{
            return resultado;
        }
    }
}
