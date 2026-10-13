import java.io.Serializable;
import java.util.ArrayList;

public class Atributos implements Serializable {
    private static final long serialVersionUID = 1L; 
    private String tipo; // "CONEXION" o "MENSAJE"
    private String nombre; // Quien envía
    private String destinatario; // A quién va dirigido
    private String mensaje;
    private ArrayList<String> usuariosOnline; // Lista de nombres conectados

    // Getters y Setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public ArrayList<String> getUsuariosOnline() { return usuariosOnline; }
    public void setUsuariosOnline(ArrayList<String> usuariosOnline) { this.usuariosOnline = usuariosOnline; }
}