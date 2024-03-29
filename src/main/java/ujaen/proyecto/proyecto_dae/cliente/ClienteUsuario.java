/**
 * Cliente del Gestor de Eventos
 * @author Adrián Perez López
 * @author Rafael Galán Ruiz
 */
package ujaen.proyecto.proyecto_dae.cliente;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import org.springframework.context.ApplicationContext;
import ujaen.proyecto.proyecto_dae.servicios.dto.EventoDTO;
import ujaen.proyecto.proyecto_dae.beans.GestorEventos;
import ujaen.proyecto.proyecto_dae.beans.Tipo;
import ujaen.proyecto.proyecto_dae.excepciones.EventoNoExiste;
import ujaen.proyecto.proyecto_dae.excepciones.IdentificacionErronea;

/**
 *
 * @author adpl
 * @author Rafa
 */
public class ClienteUsuario {
    ApplicationContext context;

    public ClienteUsuario(ApplicationContext context) {
        this.context = context;
    }

    public void run() {
        String usuario = "";
        EventoDTO evento;
        Calendar fecha = GregorianCalendar.getInstance();
        Scanner sc = new Scanner(System.in);
        int sesion, opcion, nMax, dia, mes, anio;
        String nombre, pass, email, titulo, descripcion, localizacion;

        GestorEventos gestorEventos = (GestorEventos) context.getBean(GestorEventos.class);

        sesion = gestorEventos.registrarUsuario("adrian", "adrian", "adrian", "adrianpelopez@gmail.com");
        fecha.set(2018, 10, 20);
        gestorEventos.crearEvento("Feria", "Evento para la feria de Jaén", "Jaén", Tipo.FESTIVAL, fecha, 2, sesion);
        sesion = gestorEventos.registrarUsuario("rafa", "rafa", "rafa", "apl00034@red.ujaen.es");

        sesion = gestorEventos.registrarUsuario("antonio", "antonio", "antonio", "apl00034@red.ujaen.es");
        gestorEventos.crearEvento("Evento 1", "Evento 1", "Jaén", Tipo.FESTIVAL, fecha, 1, sesion);
        
        sesion = gestorEventos.identificarUsuario("adrian", "adrian");
        gestorEventos.inscribirUsuario(sesion, gestorEventos.buscarEvento("Evento 1"));
        sesion = gestorEventos.identificarUsuario("rafa", "rafa");
        gestorEventos.inscribirUsuario(sesion, gestorEventos.buscarEvento("Evento 1"));
        //sesion = gestorEventos.identificarUsuario("adrian", "adrian");
        //gestorEventos.cancelarAsistencia(sesion, gestorEventos.buscarEvento("Evento 1"));
        
        sesion = gestorEventos.registrarUsuario("pepe", "pepe", "pepe", "apl00034@red.ujaen.es");
        gestorEventos.crearEvento("Evento 2", "Evento 2", "Linares", Tipo.CULTURAL, fecha, 2, sesion);
        gestorEventos.registrarUsuario("paco", "paco", "paco", "paco@ujaen.es");
        
        

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        sesion = 0;

        do {
            System.out.println();
            if ( sesion > 0 ) {
                System.out.println("Identificado como " + usuario);
            }
            System.out.println();
            System.out.println();
            System.out.println("1. Registrar usuario");
            System.out.println("2. Identificar usuario");
            System.out.println("3. Crear evento");
            System.out.println("4. Listar mis eventos (Inscrito - no celebrados)");
            System.out.println("5. Listar mis eventos (Organizador)");
            System.out.println("6. Buscar evento por título");
            System.out.println("7. Buscar evento por tipo (FESTIVAL)");
            System.out.println("8. Buscar evento por tipo (FESTIVAL) y descripción");
            System.out.println("9. Inscribirse en un evento");
            System.out.println("10 Cancelar asistencia a un evento");
            System.out.println("11 Cancelar un evento");
            System.out.println("12 Lista de eventos pasados");
            System.out.println("0 Cerrar sesión");
            System.out.print("Opción: ");
            opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    sc.nextLine();

                    System.out.print("Nombre: ");
                    nombre = sc.nextLine();
                    System.out.print("Contraseña: ");
                    pass = sc.nextLine();
                    System.out.print("Email: ");
                    email = sc.nextLine();

                    sesion = gestorEventos.registrarUsuario(nombre, pass, pass, email);
                    if ( sesion > 0 ) {
                        usuario = nombre;
                    }

                break;

                case 2:
                    sc.nextLine();

                    System.out.print("Nombre: ");
                    nombre = sc.nextLine();
                    System.out.print("Contraseña: ");
                    pass = sc.nextLine();

                    try {
                        sesion = gestorEventos.identificarUsuario(nombre, pass);
                        if ( sesion > 0 ) {
                            usuario = nombre;
                        }
                    } catch ( IdentificacionErronea e ) {
                        System.err.println(e.getMessage());
                    }

                break;

                case 3:
                    if ( sesion > 0 ) {
                        sc.nextLine();
                        System.out.print("Título: ");
                        titulo = sc.nextLine();
                        System.out.print("Descripción: ");
                        descripcion = sc.nextLine();
                        System.out.print("Localización: ");
                        localizacion = sc.nextLine();
                        System.out.print("Máximo asistentes: ");
                        nMax = sc.nextInt();
                        System.out.print("Día: ");
                        dia = sc.nextInt();
                        System.out.print("Mes: ");
                        mes = sc.nextInt();
                        System.out.print("Año: ");
                        anio = sc.nextInt();
                        fecha.set(anio, mes, dia);
                        gestorEventos.crearEvento(titulo, descripcion, localizacion, Tipo.CONCIERTO, fecha, nMax, sesion);
                    } else {
                        System.out.println("ERROR: Debe estar identificado para crear un evento");
                    }
                break;

                case 4:
                    if ( sesion > 0 ) {
                        System.out.println(gestorEventos.listaEventosPorCelebrar(sesion));
                    } else {
                        System.out.println("ERROR: Debe estar identificado para consultar sus eventos");
                    }
                break;

                case 5:
                    if ( sesion > 0 ) {
                        System.out.println(gestorEventos.listaEventosOrganizador(sesion));
                    } else {
                        System.out.println("ERROR: Debe estar identificado para consultar sus eventos");
                    }
                break;

                case 6:
                    sc.nextLine();

                    System.out.print("Título del evento: ");
                    titulo = sc.nextLine();
                    try {
                        evento = gestorEventos.buscarEvento(titulo);
                        System.out.println(evento.toString());
                    } catch ( EventoNoExiste e ) {
                        System.err.println(e.getMessage());
                    }
                break;

                case 7:
                    System.out.println("Eventos de tipo " + Tipo.FESTIVAL.getTitulo() + ": " + Tipo.FESTIVAL.getDescripcion());
                    System.out.println(gestorEventos.buscarEvento(Tipo.FESTIVAL));
                break;

                case 8:
                    sc.nextLine();

                    System.out.print("Búsqueda: ");
                    titulo = sc.nextLine();
                    System.out.println("Eventos de tipo " + Tipo.FESTIVAL.getTitulo() + ": " + Tipo.FESTIVAL.getDescripcion());
                    System.out.println(gestorEventos.buscarEvento(Tipo.FESTIVAL, titulo));
                break;

                case 9:
                    sc.nextLine();

                    if ( sesion > 0 ) {
                        System.out.print("Título: ");
                        titulo = sc.nextLine();
                        evento = gestorEventos.buscarEvento(titulo);

                        gestorEventos.inscribirUsuario(sesion, evento);

                    } else {
                        System.out.println("ERROR: Debe estar identificado para consultar sus eventos");
                    }
                break;

                case 10:
                    sc.nextLine();

                    if ( sesion > 0 ) {
                        System.out.print("Título: ");
                        titulo = sc.nextLine();
                        evento = gestorEventos.buscarEvento(titulo);

                        gestorEventos.cancelarAsistencia(sesion, evento);

                    } else {
                        System.out.println("ERROR: Debe estar identificado para darse de baja de un evento");
                    }
                break;

                case 11:
                    sc.nextLine();

                    if ( sesion > 0 ) {
                        System.out.print("Título: ");
                        titulo = sc.nextLine();
                        evento = gestorEventos.buscarEvento(titulo);
                        
                        gestorEventos.cancelarEvento(sesion, evento);

                    } else {
                        System.out.println("ERROR: Debe estar identificado para consultar sus eventos");
                    }
                break;
                
                case 12:
                    if ( sesion > 0 ) {
                        System.out.println(gestorEventos.listaEventosCelebrados(sesion));
                    } else {
                        System.out.println("ERROR: Debe estar identificado para consultar sus eventos");
                    }
                break;
            }
        } while ( opcion != 0 );
    }
}
