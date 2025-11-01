package Objetos.Utilidad;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Sistema de control de teclas mejorado
 * Soporta movimiento en escaleras con animaciones completas
 * 
 * @author LENOVO
 */
public class Teclas extends KeyAdapter {
    private boolean[] keyAbajo = new boolean[5];
    private Handler handler;
    
    // Índices de las teclas
    private static final int KEY_SPACE = 0;
    private static final int KEY_W = 1;
    private static final int KEY_S = 2;
    private static final int KEY_A = 3;
    private static final int KEY_D = 4;
    
    public Teclas(Handler handler) {
        this.handler = handler;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        // Salir del juego (no requiere jugador)
        if (key == KeyEvent.VK_ESCAPE) {
            System.out.println("Saliendo del juego...");
            System.exit(0);
        }
        
        // Verificar que el jugador exista
        if (handler.getPlayer() == null) {
            return;
        }
        
        // ESPACIO - Saltar (solo si no está en escalera)
        if (key == KeyEvent.VK_SPACE) {
            if (!keyAbajo[KEY_SPACE]) {
                handler.getPlayer().iniciarSalto();
                keyAbajo[KEY_SPACE] = true;
            }
        }
        
        // W - Subir escalera
        if (key == KeyEvent.VK_W) {
            if (!keyAbajo[KEY_W]) {
                if (handler.getPlayer().isPuedeMoverseEnEscalera()) {
                    handler.getPlayer().subirEscalera();
                }
                keyAbajo[KEY_W] = true;
            }
        }
        
        // S - Bajar escalera
        if (key == KeyEvent.VK_S) {
            if (!keyAbajo[KEY_S]) {
                if (handler.getPlayer().isPuedeMoverseEnEscalera()) {
                    handler.getPlayer().bajarEscalera();
                }
                keyAbajo[KEY_S] = true;
            }
        }
        
        // A - Mover a la izquierda
        if (key == KeyEvent.VK_A) {
            if (!keyAbajo[KEY_A]) {
                handler.getPlayer().moverIzquierda();
                keyAbajo[KEY_A] = true;
            }
        }
        
        // D - Mover a la derecha
        if (key == KeyEvent.VK_D) {
            if (!keyAbajo[KEY_D]) {
                handler.getPlayer().moverDerecha();
                keyAbajo[KEY_D] = true;
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        // Verificar que el jugador exista
        if (handler.getPlayer() == null) {
            return;
        }
        
        // ESPACIO - Liberado
        if (key == KeyEvent.VK_SPACE) {
            keyAbajo[KEY_SPACE] = false;
        }
        
        // W - Liberado (dejar de subir)
        if (key == KeyEvent.VK_W) {
            keyAbajo[KEY_W] = false;
            
            // Si está en escalera, detener movimiento vertical
            if (handler.getPlayer().isEnEscalera()) {
                // Si no está presionando S, detener completamente
                if (!keyAbajo[KEY_S]) {
                    handler.getPlayer().detenerMovimientoVertical();
                }
            }
        }
        
        // S - Liberado (dejar de bajar)
        if (key == KeyEvent.VK_S) {
            keyAbajo[KEY_S] = false;
            
            // Si está en escalera, detener movimiento vertical
            if (handler.getPlayer().isEnEscalera()) {
                // Si no está presionando W, detener completamente
                if (!keyAbajo[KEY_W]) {
                    handler.getPlayer().detenerMovimientoVertical();
                }
            }
        }
        
        // A - Liberado
        if (key == KeyEvent.VK_A) {
            keyAbajo[KEY_A] = false;
        }
        
        // D - Liberado
        if (key == KeyEvent.VK_D) {
            keyAbajo[KEY_D] = false;
        }
        
        // Detener movimiento horizontal si no hay teclas horizontales presionadas
        if (!keyAbajo[KEY_A] && !keyAbajo[KEY_D]) {
            handler.getPlayer().detenerMovimiento();
        }
    }
    
    /**
     * Verifica si una tecla específica está presionada
     */
    public boolean isKeyDown(int keyIndex) {
        if (keyIndex >= 0 && keyIndex < keyAbajo.length) {
            return keyAbajo[keyIndex];
        }
        return false;
    }
    
    /**
     * Verifica si W está presionada (útil para debug)
     */
    public boolean isWPressed() {
        return keyAbajo[KEY_W];
    }
    
    /**
     * Verifica si S está presionada (útil para debug)
     */
    public boolean isSPressed() {
        return keyAbajo[KEY_S];
    }
    
    /**
     * Verifica si A está presionada
     */
    public boolean isAPressed() {
        return keyAbajo[KEY_A];
    }
    
    /**
     * Verifica si D está presionada
     */
    public boolean isDPressed() {
        return keyAbajo[KEY_D];
    }
    
    /**
     * Resetea todas las teclas (útil para pausas o cambios de estado)
     */
    public void resetKeys() {
        for (int i = 0; i < keyAbajo.length; i++) {
            keyAbajo[i] = false;
        }
        
        if (handler.getPlayer() != null) {
            handler.getPlayer().detenerMovimiento();
            handler.getPlayer().detenerMovimientoVertical();
        }
    }
}