/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Mapa;
import Objetos.*;
import java.awt.Graphics;
import Objetos.Utilidad.*;
import mariotest.Mariotest;

/**
 * Patrón STATE para manejar estados del nivel
 * Estados: JUGANDO → VICTORIA → TRANSICION → SIGUIENTE_NIVEL
 * 
 * @author LENOVO
 */
public abstract class EstadoNivel {
    
    protected Mariotest juego;
    protected GestorNiveles gestorNiveles;
    
    public EstadoNivel(Mariotest juego, GestorNiveles gestorNiveles) {
        this.juego = juego;
        this.gestorNiveles = gestorNiveles;
    }
    
    /**
     * Métodos abstractos que cada estado implementa
     */
    public abstract void entrar();
    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract void salir();
    
    /**
     * Control de entradas
     */
    public abstract boolean permitirMovimientoJugador();
    public abstract boolean permitirSpawnEnemigos();
    
    // ==================== ESTADO: JUGANDO ====================
    
    public static class Jugando extends EstadoNivel {
        
        public Jugando(Mariotest juego, GestorNiveles gestorNiveles) {
            super(juego, gestorNiveles);
        }
        
        @Override
        public void entrar() {
            System.out.println("[ESTADO NIVEL] → JUGANDO");
        }
        
        @Override
        public void tick() {
            // Verificar victoria (jugador llegó a la princesa)
            if (gestorNiveles.verificarVictoria()) {
                gestorNiveles.cambiarEstado(
                    new Victoria(juego, gestorNiveles)
                );
            }
        }
        
        @Override
        public void render(Graphics g) {
            // Renderizado normal del juego
        }
        
        @Override
        public void salir() {
            System.out.println("[ESTADO NIVEL] JUGANDO → saliendo");
        }
        
        @Override
        public boolean permitirMovimientoJugador() {
            return true;
        }
        
        @Override
        public boolean permitirSpawnEnemigos() {
            return true;
        }
    }
    
    // ==================== ESTADO: VICTORIA ====================
    
// ==================== ESTADO: VICTORIA ====================

public static class Victoria extends EstadoNivel {
    
    private int ticksAnimacion;
    private static final int DURACION_ANIMACION = 240; // ✅ 4 segundos (extendido)
    
    // Control de secuencia de animación
    private int faseActual;
    private static final int FASE_CORAZON = 0;
    private static final int FASE_DK_AGARRA = 1;
    private static final int FASE_MOVIMIENTO = 2;     // 🆕 NUEVO
    private static final int FASE_CORAZON_ROTO = 3;
    private static final int FASE_TRANSICION = 4;
    
    public Victoria(Mariotest juego, GestorNiveles gestorNiveles) {
        super(juego, gestorNiveles);
        this.ticksAnimacion = 0;
        this.faseActual = FASE_CORAZON;
    }
    
    @Override
    public void entrar() {
        System.out.println("[ESTADO NIVEL] → VICTORIA");
        
        // Detener spawners
        gestorNiveles.detenerSpawners();
        
        // Detener movimiento del jugador
        if (juego.getHandler().getPlayer() != null) {
            juego.getHandler().getPlayer().detenerMovimiento();
        }
        
        // Iniciar animación de victoria
        gestorNiveles.iniciarAnimacionVictoria();
    }
    
    @Override
    public void tick() {
        ticksAnimacion++;
        
        // ==================== SECUENCIA DE ANIMACIÓN ====================
        
        // FASE 0: Mostrar corazón (0.5 seg)
        if (ticksAnimacion == 30) {
            faseActual = FASE_CORAZON;
            gestorNiveles.mostrarCorazon();
        } 
        // FASE 1: DK comienza a agarrar (1.5 seg)
        else if (ticksAnimacion == 90) {
            faseActual = FASE_DK_AGARRA;
            gestorNiveles.animarDKAgarraPrincesa();
        } 
        // FASE 2: Movimiento visible de princesa hacia DK (2.5 seg)
        else if (ticksAnimacion == 150) {
            faseActual = FASE_MOVIMIENTO;
            // La princesa sigue moviéndose
        }
        // FASE 3: Corazón roto (3.5 seg)
        else if (ticksAnimacion == 210) {
            faseActual = FASE_CORAZON_ROTO;
            gestorNiveles.mostrarCorazonRoto();
        }
        
        // Finalizar animación (4 seg)
        if (ticksAnimacion >= DURACION_ANIMACION) {
            gestorNiveles.cambiarEstado(
                new Transicion(juego, gestorNiveles)
            );
        }
    }
    
    @Override
    public void render(Graphics g) {
        // Renderizar overlay de victoria
        gestorNiveles.renderOverlayVictoria(g, faseActual, ticksAnimacion);
    }
    
    @Override
    public void salir() {
        System.out.println("[ESTADO NIVEL] VICTORIA → saliendo");
    }
    
    @Override
    public boolean permitirMovimientoJugador() {
        return false;
    }
    
    @Override
    public boolean permitirSpawnEnemigos() {
        return false;
    }
}
    
    // ==================== ESTADO: TRANSICION ====================
    
    public static class Transicion extends EstadoNivel {
        
        private int ticksTransicion;
        private static final int DURACION_FADE = 60; // 1 segundo
        private float alphaFade;
        
        public Transicion(Mariotest juego, GestorNiveles gestorNiveles) {
            super(juego, gestorNiveles);
            this.ticksTransicion = 0;
            this.alphaFade = 0f;
        }
        
        @Override
        public void entrar() {
            System.out.println("[ESTADO NIVEL] → TRANSICION");
        }
        
        @Override
        public void tick() {
            ticksTransicion++;
            
            // Fade out
            alphaFade = Math.min(1f, (float)ticksTransicion / DURACION_FADE);
            
            if (ticksTransicion >= DURACION_FADE) {
                gestorNiveles.cambiarEstado(
                    new CargandoNivel(juego, gestorNiveles)
                );
            }
        }
        
        @Override
        public void render(Graphics g) {
            // Overlay negro con fade
            java.awt.Color colorFade = new java.awt.Color(
                0, 0, 0, (int)(alphaFade * 255)
            );
            g.setColor(colorFade);
            g.fillRect(0, 0, 
                Mariotest.getVentanaWidth(), 
                Mariotest.getVentanaHeight()
            );
        }
        
        @Override
        public void salir() {
            System.out.println("[ESTADO NIVEL] TRANSICION → saliendo");
        }
        
        @Override
        public boolean permitirMovimientoJugador() {
            return false;
        }
        
        @Override
        public boolean permitirSpawnEnemigos() {
            return false;
        }
    }
    
    // ==================== ESTADO: CARGANDO NIVEL ====================
    
    public static class CargandoNivel extends EstadoNivel {
        
        public CargandoNivel(Mariotest juego, GestorNiveles gestorNiveles) {
            super(juego, gestorNiveles);
        }
        
        @Override
        public void entrar() {
            System.out.println("[ESTADO NIVEL] → CARGANDO_NIVEL");
            
            // Cargar siguiente nivel
            gestorNiveles.cargarSiguienteNivel();
            
            // Volver a estado JUGANDO
            gestorNiveles.cambiarEstado(
                new Jugando(juego, gestorNiveles)
            );
        }
        
        @Override
        public void tick() {
            // Se ejecuta solo una vez al cargar
        }
        
        @Override
        public void render(Graphics g) {
            // Pantalla de carga
            g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, 0, 
                Mariotest.getVentanaWidth(), 
                Mariotest.getVentanaHeight()
            );
            
            g.setColor(java.awt.Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
            String texto = "CARGANDO NIVEL...";
            int x = Mariotest.getVentanaWidth() / 2 - 100;
            int y = Mariotest.getVentanaHeight() / 2;
            g.drawString(texto, x, y);
        }
        
        @Override
        public void salir() {
            System.out.println("[ESTADO NIVEL] CARGANDO_NIVEL → saliendo");
        }
        
        @Override
        public boolean permitirMovimientoJugador() {
            return false;
        }
        
        @Override
        public boolean permitirSpawnEnemigos() {
            return false;
        }
    }
}
