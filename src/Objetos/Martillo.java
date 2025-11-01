package Objetos;

import Objetos.Utilidad.Handler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import mariotest.*;

/**
 * Item Martillo - Power-up principal
 * 
 * ✅ FIX: Corregida carga de sprites
 * 
 * @author LENOVO
 */
public class Martillo extends Item {
    private static final float WIDTH = 16f;
    private static final float HEIGHT = 16f;
    private static final int VALOR_PUNTOS = 300;
    private static final int DURACION_PODER = 600; // 10 segundos
    
    public Martillo(float x, float y, int scale, Handler handler) {
        super(x, y, WIDTH, HEIGHT, scale, handler, VALOR_PUNTOS);
        this.flotar = true;
        this.desapareceDespuesDeRecoger = true;
        this.tieneGravedad = false;
        cargarSprites();
    }
    
    /**
     * ✅ FIX: Carga correcta de sprites
     */
    private void cargarSprites() {
        try {
            sprites = Mariotest.getTextura().getMartilloSprites();
            
            if (sprites != null && sprites.length > 0 && sprites[0] != null) {
                System.out.println("[MARTILLO] Sprite estático cargado correctamente.");
            } else {
                System.err.println("[MARTILLO] ERROR: Array de sprites vacío o nulo.");
                sprites = null; // Forzar uso de placeholder
            }
        } catch (Exception e) {
            System.err.println("[MARTILLO] Error cargando sprites: " + e.getMessage());
            e.printStackTrace();
            sprites = null;
        }
    }
    
    @Override
    protected void aplicarEfecto(Player player) {
        // Activar el poder del martillo
        player.activarMartillo(DURACION_PODER / 60); // Convertir ticks a segundos
        
        System.out.println("[MARTILLO] ¡Power-up activado! Duración: " + 
                          (DURACION_PODER / 60) + " segundos");
    }
    
    @Override
    protected void renderPlaceholder(Graphics g) {
        // Mango del martillo
        /*
        g.setColor(new Color(139, 69, 19)); // Marrón
        g.fillRect((int)getX() + 6, (int)getY() + 8, 4, 8);
        
        // Cabeza del martillo
        g.setColor(new Color(128, 128, 128)); // Gris
        g.fillRect((int)getX() + 4, (int)getY() + 2, 8, 6);
        
        // Borde de la cabeza
        g.setColor(Color.BLACK);
        g.drawRect((int)getX() + 4, (int)getY() + 2, 8, 6);
        
        // Detalles del metal
        g.setColor(new Color(180, 180, 180));
        g.fillRect((int)getX() + 5, (int)getY() + 3, 2, 2);
*/
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Rotación suave para efecto visual (si tuvieras animación)
        if (animacion != null) {
            animacion.runAnimacion();
        }
    }
}