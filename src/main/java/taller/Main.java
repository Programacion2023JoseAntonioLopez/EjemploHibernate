package taller;

import jakarta.persistence.*;

import java.util.List;
import java.util.Scanner;

/**
 * Aplicación principal para gestionar coches en un taller.
 *
 * Este programa implementa un CRUD completo usando JPA:
 * - Create  → crearCoche()
 * - Read    → leerCoche(), mostrarCoches() y buscarPorMarca()
 * - Update  → modificarCoche()
 * - Delete  → borrarCoche()
 */
public class Main {

    /** Fábrica de EntityManager (crea conexiones). */
    private static EntityManagerFactory emf;

    /** EntityManager: permite interactuar con la base de datos. */
    private static EntityManager em;

    /** Scanner único para toda la aplicación (entrada por teclado). */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * Lee un número entero desde teclado con control de errores.
     * Evita que el programa falle si el usuario introduce texto.
     */
    private static int leerEntero(String mensaje) {
        int numero = 0;
        boolean correcto = false;

        while (!correcto) {
            try {
                System.out.print(mensaje);
                numero = Integer.parseInt(sc.nextLine().trim());
                correcto = true;
            } catch (NumberFormatException e) {
                System.out.println("Error: debes introducir un número entero.");
            }
        }

        return numero;
    }

    /**
     * Lee un texto obligatorio (no puede estar vacío).
     */
    private static String leerTextoObligatorio(String mensaje) {
        String texto;

        do {
            System.out.print(mensaje);
            texto = sc.nextLine().trim();

            if (texto.isEmpty()) {
                System.out.println("Error: este campo no puede estar vacío.");
            }
        } while (texto.isEmpty());

        return texto;
    }

    /**
     * Método auxiliar para hacer rollback si algo falla.
     * Se usa cuando ocurre un error en una transacción.
     */
    private static void rollbackSeguro(EntityTransaction tx) {
        try {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } catch (Exception e) {
            System.out.println("Error al deshacer la transacción.");
        }
    }

    /**
     * Comprueba si hay coches en la base de datos.
     * Si no hay ninguno, inserta 6 coches de ejemplo.
     */
    private static void inicializarCoches() {
        EntityTransaction tx = null;

        try {
            Long total = em.createNamedQuery("Coche.contarTodos", Long.class)
                    .getSingleResult();

            if (total == 0) {
                System.out.println("No hay coches en la base de datos.");
                System.out.println("Insertando 6 coches de ejemplo...");

                tx = em.getTransaction();
                tx.begin();

                em.persist(new Coche("1111AAA", "Seat", "Cordoba", 5));
                em.persist(new Coche("2222BBB", "Ford", "Focus", 5));
                em.persist(new Coche("3333CCC", "Seat", "Ibiza", 5));
                em.persist(new Coche("4444DDD", "BMW", "Serie 3", 5));
                em.persist(new Coche("5555EEE", "Audi", "A4", 5));
                em.persist(new Coche("6666FFF", "Audi", "A3", 5));

                tx.commit();

                System.out.println("Coches de ejemplo insertados correctamente.");
            }
        } catch (Exception e) {
            rollbackSeguro(tx);
            System.out.println("Error al inicializar los coches.");
        }
    }

    /**
     * Muestra la cabecera de la tabla para los listados.
     */
    private static void mostrarCabeceraTabla() {
        System.out.printf("%-12s %-12s %-15s %-10s%n",
                "MATRICULA", "MARCA", "MODELO", "PLAZAS");
        System.out.println("-----------------------------------------------------");
    }

    /**
     * CREATE → Inserta un nuevo coche en la base de datos.
     */
    private static void crearCoche() {

        EntityTransaction tx = null;
        boolean valido = true;

        try {
            System.out.println("\nALTA DE NUEVO COCHE");

            String matricula = leerTextoObligatorio("Matrícula: ");
            String marca = leerTextoObligatorio("Marca: ");
            String modelo = leerTextoObligatorio("Modelo: ");
            int numPlazas = leerEntero("Número de plazas: ");

            if (numPlazas <= 0) {
                System.out.println("Error: número de plazas inválido.");
                valido = false;
            }

            if (valido && em.find(Coche.class, matricula) != null) {
                System.out.println("Error: ya existe un coche con esa matrícula.");
                valido = false;
            }

            if (valido) {
                Coche c = new Coche(matricula, marca, modelo, numPlazas);

                tx = em.getTransaction();
                tx.begin();

                em.persist(c);

                tx.commit();

                System.out.println("Coche creado correctamente.");
            }

        } catch (PersistenceException e) {
            rollbackSeguro(tx);
            System.out.println("Error de base de datos.");
        } catch (Exception e) {
            rollbackSeguro(tx);
            System.out.println("Error inesperado.");
        }
    }

    /**
     * READ → Consulta un coche por su matrícula.
     */
    private static void leerCoche() {
        try {
            System.out.println("\nCONSULTA DE COCHE");

            String matricula = leerTextoObligatorio("Matrícula: ");

            Coche c = em.find(Coche.class, matricula);

            if (c != null) {
                mostrarCabeceraTabla();
                System.out.printf("%-12s %-12s %-15s %-10d%n",
                        c.getMatricula(), c.getMarca(), c.getModelo(), c.getNum_plazas());
            } else {
                System.out.println("No existe.");
            }

        } catch (Exception e) {
            System.out.println("Error al consultar.");
        }
    }

    /**
     * UPDATE → Modifica un coche existente.
     */
    private static void modificarCoche() {

        EntityTransaction tx = null;
        boolean valido = true;

        try {
            System.out.println("\nMODIFICACIÓN DE COCHE");

            String matricula = leerTextoObligatorio("Matrícula: ");

            Coche c = em.find(Coche.class, matricula);

            if (c == null) {
                System.out.println("No existe.");
            } else {
                System.out.println("Datos actuales:");
                mostrarCabeceraTabla();
                System.out.printf("%-12s %-12s %-15s %-10d%n",
                        c.getMatricula(), c.getMarca(), c.getModelo(), c.getNum_plazas());

                String marca = leerTextoObligatorio("Nueva marca: ");
                String modelo = leerTextoObligatorio("Nuevo modelo: ");
                int plazas = leerEntero("Plazas: ");

                if (plazas <= 0) {
                    System.out.println("Error: número de plazas inválido.");
                    valido = false;
                }

                if (valido) {
                    tx = em.getTransaction();
                    tx.begin();

                    c.setMarca(marca);
                    c.setModelo(modelo);
                    c.setNum_plazas(plazas);

                    tx.commit();

                    System.out.println("Modificado correctamente.");
                }
            }

        } catch (Exception e) {
            rollbackSeguro(tx);
            System.out.println("Error al modificar.");
        }
    }

    /**
     * DELETE → Elimina un coche.
     */
    private static void borrarCoche() {

        EntityTransaction tx = null;

        try {
            System.out.println("\nBORRAR COCHE");

            String matricula = leerTextoObligatorio("Matrícula: ");

            Coche c = em.find(Coche.class, matricula);

            if (c == null) {
                System.out.println("No existe.");
            } else {
                tx = em.getTransaction();
                tx.begin();

                em.remove(c);

                tx.commit();

                System.out.println("Borrado correctamente.");
            }

        } catch (Exception e) {
            rollbackSeguro(tx);
            System.out.println("Error al borrar.");
        }
    }

    /**
     * READ (LISTADO) → Muestra todos los coches en formato tabulado.
     */
    public static void mostrarCoches() {
        try {
            Query q = em.createNamedQuery("Coche.listarTodos", Coche.class);

            List<Coche> lista = q.getResultList();

            if (lista.isEmpty()) {
                System.out.println("No hay coches registrados.");
            } else {
                System.out.println("\nLISTADO DE COCHES");
                mostrarCabeceraTabla();

                for (Coche c : lista) {
                    System.out.printf("%-12s %-12s %-15s %-10d%n",
                            c.getMatricula(), c.getMarca(), c.getModelo(), c.getNum_plazas());
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar.");
        }
    }

    /**
     * READ → Busca coches de una marca concreta.
     */
    private static void buscarPorMarca() {
        try {
            System.out.println("\nBÚSQUEDA POR MARCA");

            String marcaBuscada = leerTextoObligatorio("Marca: ");

            Query q = em.createNamedQuery("Coche.buscarPorMarca", Coche.class);
            q.setParameter("marca", marcaBuscada);

            List<Coche> lista = q.getResultList();

            if (lista.isEmpty()) {
                System.out.println("No hay coches de la marca " + marcaBuscada + ".");
            } else {
                System.out.println("\nCOCHES DE LA MARCA: " + marcaBuscada.toUpperCase());
                mostrarCabeceraTabla();

                for (Coche c : lista) {
                    System.out.printf("%-12s %-12s %-15s %-10d%n",
                            c.getMatricula(), c.getMarca(), c.getModelo(), c.getNum_plazas());
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar por marca.");
        }
    }

    /**
     * Muestra el menú por pantalla.
     */
    private static void mostrarMenu() {
        System.out.println("\n****************************");
        System.out.println("  TALLER DE COCHES SEVERO");
        System.out.println("****************************");
        System.out.println("1. Alta");
        System.out.println("2. Consulta");
        System.out.println("3. Modificar");
        System.out.println("4. Borrar");
        System.out.println("5. Listar");
        System.out.println("6. Buscar por marca");
        System.out.println("7. Salir");
    }

    /**
     * Método principal.
     */
    public static void main(String[] args) {

        try {
            emf = Persistence.createEntityManagerFactory("default");
            em = emf.createEntityManager();

            inicializarCoches();

            int opcion;

            do {
                mostrarMenu();
                opcion = leerEntero("Opción: ");

                switch (opcion) {
                    case 1 -> crearCoche();
                    case 2 -> leerCoche();
                    case 3 -> modificarCoche();
                    case 4 -> borrarCoche();
                    case 5 -> mostrarCoches();
                    case 6 -> buscarPorMarca();
                    case 7 -> System.out.println("Saliendo de la aplicación...");
                    default -> System.out.println("Opción no válida.");
                }

            } while (opcion != 7);

        } catch (PersistenceException e) {
            System.out.println("Error al conectar con la BD.");
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
            sc.close();
        }
    }
}