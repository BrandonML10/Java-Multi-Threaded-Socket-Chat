import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        MarcoCliente marco = new MarcoCliente();
        marco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoCliente extends JFrame {
    public MarcoCliente() {
        setBounds(600, 300, 320, 400);
        LaminaMarcoCliente lamina = new LaminaMarcoCliente();
        add(lamina);
        setVisible(true);
    }
}

class LaminaMarcoCliente extends JPanel implements Runnable {
    private JTextField campoMensaje;
    private JComboBox<String> comboUsuarios;
    private JLabel labelNombre;
    private JButton btnEnviar;
    private JTextArea campochat;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String miNombre;

    public LaminaMarcoCliente() {
        miNombre = JOptionPane.showInputDialog("Ingresa tu nombre para el chat:");
        if (miNombre == null || miNombre.trim().isEmpty()) {
            System.exit(0); // Cierra si no pone nombre
        }

        setLayout(new FlowLayout());
        
        add(new JLabel("Usuario: "));
        labelNombre = new JLabel(miNombre);
        labelNombre.setForeground(Color.BLUE);
        add(labelNombre);
        
        add(new JLabel(" Enviar a:"));
        comboUsuarios = new JComboBox<>();
        add(comboUsuarios);
        
        campochat = new JTextArea(15, 25);
        campochat.setEditable(false);
        add(new JScrollPane(campochat));
        
        campoMensaje = new JTextField(20);
        add(campoMensaje);
        
        btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(new EnviaTexto());
        add(btnEnviar);

        // Iniciar conexión y escucha en un hilo secundario
        Thread hilo = new Thread(this);
        hilo.start();
    }

    @Override
    public void run() {
        try {
            // Cambiar "localhost" por la IP del servidor si se prueba en otra PC
            socket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Avisar al servidor que entramos
            Atributos datosConexion = new Atributos();
            datosConexion.setTipo("CONEXION");
            datosConexion.setNombre(miNombre);
            out.writeObject(datosConexion);
            out.flush();

            // Ciclo infinito para escuchar todo lo que llega del servidor
            while (true) {
                Atributos datosEntrantes = (Atributos) in.readObject();

                if (datosEntrantes.getTipo().equals("CONEXION")) {
                    // Actualizar combobox 
                    SwingUtilities.invokeLater(() -> {
                        comboUsuarios.removeAllItems();
                        for (String usuario : datosEntrantes.getUsuariosOnline()) {
                            if (!usuario.equals(miNombre)) { // No agregarnos a nosotros mismos
                                comboUsuarios.addItem(usuario);
                            }
                        }
                    });
                } else if (datosEntrantes.getTipo().equals("MENSAJE")) {
                    SwingUtilities.invokeLater(() -> {
                        campochat.append("\n" + datosEntrantes.getNombre() + ": " + datosEntrantes.getMensaje());
                    });
                }
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> campochat.append("\nError de conexión con el servidor."));
        }
    }

    private class EnviaTexto implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (comboUsuarios.getSelectedItem() == null || campoMensaje.getText().isEmpty()) return;

            try {
                Atributos datos = new Atributos();
                datos.setTipo("MENSAJE");
                datos.setNombre(miNombre);
                datos.setDestinatario(comboUsuarios.getSelectedItem().toString());
                datos.setMensaje(campoMensaje.getText());

                // Mostrar nuestro propio mensaje en nuestra pantalla
                campochat.append("\nYo a " + datos.getDestinatario() + ": " + campoMensaje.getText());
                campoMensaje.setText(""); // Limpiar campo

                out.writeObject(datos);
                out.flush(); // Empujar datos por la red
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}