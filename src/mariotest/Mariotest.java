package mariotest;

import GameGFX.*;
import Mapa.*;
import Objetos.Utilidad.BarrilSpawner;
import Mapa.TiledTMXParser;
import Objetos.*;
import Objetos.Utilidad.*;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author LENOVO
 */
public class Mariotest extends Canvas implements Runnable {
    
    // CONSTANTES DEL JUEGO 
    private static final int MILLIS_PER_SEC = 1000;
    private static final int NANOS_PER_SEC = 1000000000;
    private static final double NUM_TICKS = 60.0;  // 60 FPS
    
    // Configuración de ventana
    private static final String NOMBRE_JUEGO = "Diego Kong";
    private static final int VENTANA_WIDTH = 920;
    private static final int VENTANA_HEIGHT = 760;
    
    // COMPONENTES DEL JUEGO 
    private GestorNiveles gestorNiveles;
    private Thread thread;
    private Handler handler;
    private BarrilSpawner barrelSpawner;
    private FuegoSpawner fuegoSpawner;
    private DiegoKong kong;
    private ItemSpawner itemSpawner;
    private static Texturas textura;
    
    // Estado del juego
    public boolean running;
    private boolean debug = false;
    
    // Estadísticas
    private int fps = 0;
    private int tps = 0;
    
    /**
     * Constructor del juego
     */
    public Mariotest() {
        initialize();
    }
    
    /**
     * Punto de entrada del programa
     */
    public static void main(String[] args) {
        new Mariotest();
    }
    
    /**
     * Inicializa todos los componentes del juego
     */
private void initialize() {
    System.out.println("\n[INIT] Cargando texturas...");
    textura = new Texturas();
    
    System.out.println("[INIT] Inicializando handler...");
    handler = new Handler();
    
    System.out.println("[INIT] Configurando controles...");
    this.addKeyListener(new Teclas(handler));
    
    System.out.println("[INIT] Creando jugador...");
    handler.setPlayer(new Player(100, 100, 2, handler));
    
    // ✅ CREAR GESTOR DE NIVELES
    System.out.println("[INIT] Inicializando gestor de niveles...");
    gestorNiveles = new GestorNiveles(this, handler);
    
    // ✅ CARGAR NIVEL 1
    gestorNiveles.inicializarNivel(1);
    
    System.out.println("[INIT] Creando ventana...");
    new Ventana(VENTANA_WIDTH, VENTANA_HEIGHT, NOMBRE_JUEGO, this);
    
    System.out.println("[INIT] Iniciando game loop...");
    start();
    
    System.out.println("\n========================================");
    System.out.println("    JUEGO INICIADO CORRECTAMENTE");
    System.out.println("========================================");
    
}
    
    /**
     * Inicia el thread del juego
     */
    private synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    
    private void activarFuegosNivel() {
    if (fuegoSpawner != null && !fuegoSpawner.isActivo()) {
        fuegoSpawner.activar();
        System.out.println("[NIVEL] Sistema de fuegos activado!");
    }
}
    
    private void verificarColisionesFuego() {
    Player player = handler.getPlayer();
    if (player == null) return;
    
    for (GameObjetos obj : new java.util.ArrayList<>(handler.getGameObjs())) {
        if (obj.getId() == ObjetosID.Fuego) {
            Fuego fuego = (Fuego) obj;
            
            if (fuego.colisionaConJugador(player)) {
                // Jugador tocó fuego - daño o muerte
                System.out.println("[COLISION] Jugador tocó fuego!");
                
                // Aquí puedes:
                // - Restar vida
                // - Hacer respawn
                // - Reproducir sonido de daño
                // - Mostrar animación de daño
                
                respawnJugador(); // Ejemplo simple: respawn inmediato
                
                break; // Solo procesar una colisión por tick
            }
        }
    }
}
    /**
     * Detiene el thread del juego
     */
    private synchronized void stop() {
        try {
            thread.join();
            running = false;
            System.out.println("\n[STOP] Juego detenido correctamente");
        } catch (InterruptedException e) {
            System.err.println("[ERROR] Error deteniendo el juego:");
            e.printStackTrace();
        }
    }
    
    /**
     * Game loop principal
     * Usa un sistema de fixed timestep para mantener 60 TPS constantes
     */
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = NUM_TICKS;
        double ns = NANOS_PER_SEC / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int updates = 0;
        
        // Dar foco a la ventana para capturar teclas
        this.requestFocus();
        
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            // Actualizar lógica del juego a 60 TPS
            while (delta >= 1) {
                tick();
                updates++;
                delta--;
            }
            
            // Renderizar (ilimitado, V-Sync controlará)
            if (running) {
                render();
                frames++;
            }
            
            // Actualizar contador de FPS cada segundo
            if (System.currentTimeMillis() - timer > MILLIS_PER_SEC) {
                timer += MILLIS_PER_SEC;
                fps = frames;
                tps = updates;
                
                // Mostrar stats cada 5 segundos
                if (debug) {
                    System.out.println(String.format(
                        "[STATS] FPS: %d | TPS: %d | Objetos: %d | Player: (%.0f, %.0f)",
                        fps, tps, 
                        handler.getGameObjs().size(),
                        handler.getPlayer().getX(),
                        handler.getPlayer().getY()
                    ));
                }
                
                updates = 0;
                frames = 0;
            }
        }
        
        stop();
    }
    
    /**
     * Actualiza la lógica del juego
     * Llamado 60 veces por segundo
     */
private void tick() {
    // ========================================
    // 1. ACTUALIZAR GESTOR DE NIVELES PRIMERO
    // ========================================
    if (gestorNiveles != null) {
        gestorNiveles.tick();
    }
    // ========================================
    // 2. ACTUALIZAR HANDLER (solo si el nivel lo permite)
    // ========================================
    // ✅ SOLO UNA VEZ - NO DUPLICAR
    if (gestorNiveles != null && gestorNiveles.permitirMovimientoJugador()) {
        handler.tick();
    } else if (gestorNiveles == null) {
        // Fallback si no hay gestor (no debería pasar)
        handler.tick();
    }
    
    // ========================================
    // 3. ACTUALIZAR SPAWNERS (solo si el nivel lo permite)
    // ========================================
    if (gestorNiveles == null || gestorNiveles.permitirMovimientoJugador()) {
        if (barrelSpawner != null) {
            barrelSpawner.tick();
        }
        if (fuegoSpawner != null) {
            fuegoSpawner.tick();
        }
        if (itemSpawner != null) {
            itemSpawner.tick();
        }
    }
    
    // ========================================
    // 4. VERIFICAR COLISIONES MORTALES
    // ========================================
    verificarColisionesMortales();
}
    
 private void verificarColisionesMortales() {
    Player player = handler.getPlayer();
    
    // ✅ VERIFICACIONES DE SEGURIDAD
    if (player == null) {
        System.err.println("[ERROR] Player es null en verificarColisionesMortales");
        return;
    }
    
    if (!player.estaVivo()) {
        return; // Ya está muriendo o muerto
    }
    
    // ✅ MUERTE POR CAÍDA FUERA DEL MAPA
    if (player.getY() > VENTANA_HEIGHT + 50) {
        System.out.println("[MUERTE] 💀 Jugador cayó fuera del mapa (Y=" + player.getY() + ")");
        
        // ✅ PASAR NULL COMO ENEMIGO (muerte por caída)
        player.recibirDanio(null);
    }
}
    
private void respawnJugador() {
    Player player = handler.getPlayer();
    if (player != null) {
        player.respawnear();
        System.out.println("[RESPAWN] Jugador reposicionado");
    }
}
    
    public void configurarPuntoSpawn(int x, int y) {
    Player player = handler.getPlayer();
    if (player != null) {
        player.setPuntoSpawn(x, y);
    }
}

    /**
     * Renderiza los gráficos del juego
     */
private void render() {
    BufferStrategy buffer = this.getBufferStrategy();
    
    if (buffer == null) {
        this.createBufferStrategy(3);
        return;
    }
    
    Graphics g = buffer.getDrawGraphics();
    Graphics2D g2d = (Graphics2D) g;
    
    // Limpiar pantalla
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, VENTANA_WIDTH, VENTANA_HEIGHT);
    
    // Renderizar juego normal
    handler.render(g);
    
    // ✅ RENDERIZAR OVERLAY DEL GESTOR DE NIVELES (victoria, transición, etc.)
    if (gestorNiveles != null) {
        gestorNiveles.render(g);
    }
    
    // Debug info
    if (debug) {
        renderDebugInfo(g);
    }
    
    g.dispose();
    buffer.show();
}
    
    /**
     * Renderiza información de debug en pantalla
     */
  private void renderDebugInfo(Graphics g) {
         g.setColor(Color.GREEN);
        g.drawString("FPS: " + fps + " | TPS: " + tps, 10, 20);
        g.drawString("Objetos: " + handler.getGameObjs().size(), 10, 35);
        if (gestorNiveles != null) {
        g.setColor(Color.CYAN);
        g.drawString("Nivel: " + gestorNiveles.getNivelActual(), 10, 230);
        g.drawString("Estado: " + gestorNiveles.getEstadoActual().getClass().getSimpleName(), 10, 245);}
        Player player = handler.getPlayer();
        if (player != null) {
            g.drawString(String.format("Pos: (%.0f, %.0f)", player.getX(), player.getY()), 10, 50);
            g.drawString(String.format("Vel: (%.1f, %.1f)", player.getVelX(), player.getVely()), 10, 65);
            g.drawString("Salto: " + player.hasSalto(), 10, 80);
            g.drawString("En Escalera: " + player.isEnEscalera(), 10, 95);
            g.drawString("Puede Subir: " + player.isPuedeMoverseEnEscalera(), 10, 110);
            
            // ✅ DEBUG CRÍTICO DEL ESTADO DE VIDA
            String estadoVida = player.getEstadoVida().getClass().getSimpleName();
            Color colorEstado = Color.GREEN;
            
            if (player.estaMuriendo()) {
                colorEstado = Color.ORANGE;
                EstadoVidaPlayer.Muriendo muriendo = (EstadoVidaPlayer.Muriendo) player.getEstadoVida();
                estadoVida += " (Frame: " + muriendo.getFrameActual() + ")";
            } else if (player.estaMuerto()) {
                colorEstado = Color.RED;
            }
            
            g.setColor(colorEstado);
            g.drawString("Estado Vida: " + estadoVida, 10, 155);
            
            g.setColor(Color.CYAN);
            g.drawString("Invulnerable: " + player.isInvulnerable(), 10, 170);
            g.drawString("Spawn Point: " + player.getPuntoSpawn(), 10, 185);
        }
        
        if (barrelSpawner != null) {
            g.setColor(Color.YELLOW);
            g.drawString(barrelSpawner.getInfo(), 10, 200);
        }
        if (fuegoSpawner != null) {
            g.setColor(Color.ORANGE);
            g.drawString(fuegoSpawner.getInfo(), 10, 215);
        }
    }
    
    /**
     * Toggle modo debug
     */
   public void toggleDebug() {
        debug = !debug;
        System.out.println("[DEBUG] Modo debug: " + (debug ? "✅ ACTIVADO" : "❌ DESACTIVADO"));
        
        if (debug) {
            System.out.println("========================================");
            System.out.println("  MODO DEBUG ACTIVADO");
            System.out.println("========================================");
            System.out.println("Presiona F3 de nuevo para desactivar");
            System.out.println("========================================");
        }
    }
    // ==================== GETTERS ESTÁTICOS ====================
    
    public static int getVentanaWidth() {
        return VENTANA_WIDTH;
    }
    
    public static int getVentanaHeight() {
        return VENTANA_HEIGHT;
    }
    
    public static Texturas getTextura() {
        return textura;
    }
    
    // ==================== MÉTODOS PÚBLICOS ====================
    
    /**
     * Obtiene el handler del juego
     */
    public Handler getHandler() {
        return handler;
    }
    
    /**
     * Obtiene el barrel spawner
     */
    public BarrilSpawner getBarrelSpawner() {
        return barrelSpawner;
    }
    
    /**
     * Reinicia el nivel actual
     */
    public void reiniciarNivel() {
       System.out.println("\n[RESET] Reiniciando nivel...");
    
    if (gestorNiveles != null) {
        gestorNiveles.inicializarNivel(gestorNiveles.getNivelActual());
    }
        
        // Limpiar objetos (excepto jugador)
        handler.getGameObjs().removeIf(obj -> obj.getId() != ObjetosID.Jugador);
        
        // Recargar mapa
        
        // Reconfigurar barrel spawner
        
        // Respawnear jugador
        respawnJugador();
        
        System.out.println("[RESET] Nivel reiniciado correctamente");
    }
    
    /**
     * Pausa el juego
     */
    public void pausar() {
        // TODO: Implementar sistema de pausa
        System.out.println("[PAUSE] Juego pausado");
    }
    /**
     * Reanuda el juego
     */
    public void reanudar() {
        // TODO: Implementar sistema de pausa
        System.out.println("[RESUME] Juego reanudado");
    }
    
    public GestorNiveles getGestorNiveles() {
    return gestorNiveles;}
}