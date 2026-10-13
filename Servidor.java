import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Servidor {
    public static void main(String[] args) {
        MarcoServidor marco = new MarcoServidor();
        marco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoServidor extends JFrame implements Runnable {
    private JTextArea areatexto;
    // HashMap para guardar NombreUsuario 
    private HashMap<String, ObjectOutputStream> clientesConectados = new HashMap<>();

    public MarcoServidor() {
        setBounds(1200, 300, 280, 350);
        setTitle("Servidor Central");
        JPanel lamina = new JPanel(new BorderLayout());
        areatexto = new JTextArea();
        areatexto.setEditable(false);
        lamina.add(new JScrollPane(areatexto), BorderLayout.CENTER);
        add(lamina);
        setVisible(true);
        
        Thread hilo = new Thread(this);
        hilo.start();
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(12345)) { // Puerto único del servidor
            areatexto.append("Servidor iniciado escuchando en el puerto 12345...\n");
            while (true) {
                Socket socket = server.accept();
                // Por cada cliente que se conecta, creamos un nuevo Hilo para atenderlo
                new ManejadorCliente(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase interna para manejar a cada cliente en su propio hilo
    private class ManejadorCliente extends Thread {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String nombreUsuario;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    Atributos datos = (Atributos) in.readObject();

                    if (datos.getTipo().equals("CONEXION")) {
                        nombreUsuario = datos.getNombre();
                        clientesConectados.put(nombreUsuario, out); // Lo registramos
                        
                        SwingUtilities.invokeLater(() -> {
                            areatexto.append("Conectado: " + nombreUsuario + "\n");
                        });

                        enviarListaActualizada(); // Avisar a todos que alguien entró

                    } else if (datos.getTipo().equals("MENSAJE")) {
                        SwingUtilities.invokeLater(() -> {
                            areatexto.append(datos.getNombre() + " para " + datos.getDestinatario() + ": " + datos.getMensaje() + "\n");
                        });

                        // Rutear el mensaje al destinatario correcto
                        ObjectOutputStream outDestinatario = clientesConectados.get(datos.getDestinatario());
                        if (outDestinatario != null) {
                            outDestinatario.writeObject(datos);
                            outDestinatario.flush(); // Asegurar el envío
                        }
                    }
                }
            } catch (Exception e) {
                // Si hay error (ej. se cerró el cliente), lo sacamos de la lista
                if (nombreUsuario != null) {
                    clientesConectados.remove(nombreUsuario);
                    SwingUtilities.invokeLater(() -> areatexto.append("Desconectado: " + nombreUsuario + "\n"));
                    enviarListaActualizada();
                }
            }
        }

        private void enviarListaActualizada() {
            try {
                ArrayList<String> listaNombres = new ArrayList<>(clientesConectados.keySet());
                Atributos aviso = new Atributos();
                aviso.setTipo("CONEXION");
                aviso.setUsuariosOnline(listaNombres);

                // Enviar la nueva lista a todos los clientes conectados
                for (ObjectOutputStream canal : clientesConectados.values()) {
                    canal.writeObject(aviso);
                    canal.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}