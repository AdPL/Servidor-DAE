
/**
 * Clase que gestiona los eventos y usuarios
 * @author Adrián Pérez López
 * @author Rafael Galán Ruiz
 */

package ujaen.proyecto.proyecto_dae.beans;

import ujaen.proyecto.proyecto_dae.EmailServiceImpl;
import ujaen.proyecto.proyecto_dae.entities.Usuario;
import ujaen.proyecto.proyecto_dae.entities.Evento;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Calendar;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import ujaen.proyecto.proyecto_dae.dao.EventoDAO;
import ujaen.proyecto.proyecto_dae.dao.UsuarioDAO;
import ujaen.proyecto.proyecto_dae.servicios.dto.EventoDTO;
import ujaen.proyecto.proyecto_dae.servicios.EventoService;
import ujaen.proyecto.proyecto_dae.excepciones.EventoNoExiste;
import ujaen.proyecto.proyecto_dae.excepciones.IdentificacionErronea;
import ujaen.proyecto.proyecto_dae.servicios.UsuarioService;

public class GestorEventos implements EventoService, UsuarioService {
    @Autowired
    private EventoDAO eventoDAO;
    @Autowired
    private UsuarioDAO usuarioDAO;
    @Autowired
    private EmailServiceImpl email;
    @Autowired
    private SimpleMailMessage template;
    public GestorEventos() {}

    /**
     * 
     * IMPLEMENTACIÓN DE LOS MÉTODOS 
     * DE LA INTERFAZ USUARIO SERVICE
     * 
     */
    
    /**
     * Método para registrar un usuario en el sistema
     * @param nombre Nombre del usuario
     * @param pass1 Contraseña introducida
     * @param pass2 Comprobación de la contraseña (segundo campo)
     * @param email Email del usuario
     * @return Token de sesión si se completa el registro
     */
    @Override
    public int registrarUsuario(String nombre, String pass1, String pass2, String email) {
        int sesion = -1;
        
        if ( pass1.equals(pass2) ) {
            Usuario usuario = new Usuario(nombre, email, pass1);
            usuarioDAO.insertar(usuario);
            sesion = usuario.getToken();
        }
        return sesion;
    }

    @Override
    public int identificarUsuario(String nombre, String pass) throws IdentificacionErronea {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorNombre(nombre);
        if ( usuario == null ) throw new IdentificacionErronea("Datos incorrectos");
        
        int token = -1;
        if ( usuario.getNombre().equals(nombre) && usuario.getPassword().equals(pass) ) {
            usuario.generarNuevoToken();
            usuarioDAO.actualizar(usuario);
            token = usuario.getToken();
        }
        
        return token;
    }

    /**
     * Método que lista los eventos que organiza un usuario
     * @param sesion Token de la sesión actual del usuario
     * @return Colección con los eventos organizados por el usuario
     */
    @Override
    public Collection<EventoDTO> listaEventosOrganizador(int sesion) {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorToken(sesion);
        
        List<EventoDTO> eventosDTO = new ArrayList<>();
        for ( Evento evento : usuario.getEventosOrganizador() ) {
            eventosDTO.add(evento.getEventoDTO());
        }
        return eventosDTO;
    }
    
    /**
     * Devuelve una Colección de eventosDTO con los eventos que están aún por celebrar
     * @param sesion
     * @return Colección de eventosDTO
     */
    @Override
    public Collection<EventoDTO> listaEventosPorCelebrar(int sesion) {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorToken(sesion);
        
        Collection<EventoDTO> eventosPendientes = new ArrayList<>();
        
        for ( Evento evento : usuarioDAO.obtenerEventosInscritoPorCelebrar(usuario)) {
            eventosPendientes.add(evento.getEventoDTO());
        }
        return eventosPendientes;
    }
    
    /**
     * Devuelve una lista de eventos ya celebrados por el usuario
     * @param sesion
     * @return Colección de eventosDTO
     */
    @Override
    public Collection<EventoDTO> listaEventosCelebrados(int sesion) {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorToken(sesion);
        
        Collection<EventoDTO> eventosPendientes = new ArrayList<>();
        
        for ( Evento evento : usuarioDAO.obtenerEventosInscritoPasados(usuario)) {
            eventosPendientes.add(evento.getEventoDTO());
        }
        return eventosPendientes;
    }
    
    /**
     * 
     * IMPLEMENTACIÓN DE LOS MÉTODOS 
     * DE LA INTERFAZ EVENTO SERVICE
     * 
     */
    
    /**
     * Método que devuelve el evento que se busca por su titulo
     * @param titulo Título del evento que se busca
     * @return Evento correspondiente al título buscado
     */
    @Override
    public EventoDTO buscarEvento(String titulo) throws EventoNoExiste {
        Evento evento = eventoDAO.obtenerEventoPorTitulo(titulo);
        return evento.getEventoDTO();
    }

    /**
     * Este método devuelve los eventos que coincide con el tipo que se ha pasado como argumento
     * @param tipo Tipo del evento
     * @return Colección de eventos del tipo consultado
     */
    @Override
    public Collection<EventoDTO> buscarEvento(Tipo tipo) {
        Collection<EventoDTO> eventos = new ArrayList<>();
        for ( Evento e : eventoDAO.obtenerEventoPorTipo(tipo) ) {
            eventos.add(e.getEventoDTO());
        }
        return eventos;
    }

    /**
     * Método que devuelve los eventos que coinciden con el tipo y
     * con la descripción consultada
     * @param tipo Tipo del evento
     * @param descripcion Búsqueda en la descripción del evento
     * @return Los eventos que coinciden con el tipo y con la descripción
     */
    @Override
    public Collection<EventoDTO> buscarEvento(Tipo tipo, String descripcion) {
        Collection<EventoDTO> eventos = new ArrayList<>();
        for ( Evento e : eventoDAO.obtenerEventoPorTipoDescripcion(tipo, descripcion) ) {
            eventos.add(e.getEventoDTO());
        }
        return eventos;
    }

    /**
     * Método para crear un evento
     * @param titulo
     * @param descripcion
     * @param localizacion
     * @param tipo
     * @param fecha
     * @param nMax
     * @param sesion
     * @return El evento creado
     */
    @Override
    public EventoDTO crearEvento(String titulo, String descripcion, String localizacion, Tipo tipo, Calendar fecha, int nMax, int sesion) {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorToken(sesion);
        if( fecha.before(Calendar.getInstance()) ) return null;

        Evento evento = new Evento(nMax, titulo, descripcion, localizacion, tipo, fecha, usuario);
        eventoDAO.insertar(evento);
        return evento.getEventoDTO();
    }

    @Override
    public void inscribirUsuario(int sesion, EventoDTO evento) throws IdentificacionErronea, EventoNoExiste {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorToken(sesion);
        eventoDAO.inscribirUsuario(usuario, evento.getIdEvento());
    }
    
    @Override
    public void cancelarAsistencia(int sesion, EventoDTO evento) throws IdentificacionErronea, EventoNoExiste {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorToken(sesion);
        Evento e = eventoDAO.obtenerEventoPorTitulo(evento.getTitulo());
        Usuario u = eventoDAO.cancelarAsistencia(usuario, e);
        if ( u != null ) {
            String templateArgs[] = {evento.getTitulo(), evento.getFecha().getTime().toString(), evento.getLocalizacion(), u.getNombre()};
            String text = String.format(template.getText(), templateArgs);
            email.sendSimpleMessage(u.getEmail(), "Inscrito a evento", text);
        }
    }

    @Override
    public void cancelarEvento(int sesion, EventoDTO evento) throws IdentificacionErronea, EventoNoExiste {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorToken(sesion);
        Evento e = eventoDAO.obtenerEventoPorTitulo(evento.getTitulo());
        eventoDAO.cancelarEvento(usuario, e);
    }
}
