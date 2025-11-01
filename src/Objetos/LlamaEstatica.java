package Objetos;

import GameGFX.Animacion;
import Objetos.Utilidad.Handler;
import Objetos.Utilidad.ObjetosID;
import mariotest.Mariotest;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Clase LlamaEstatica - Llama que NO se mueve
 * Solo animación visual, ideal para trampas fijas o decoración peligrosa
 * 
 * @author LENOVO
 */
public class LlamaEstatica extends GameObjetos {
    
    // Dimensiones de la llama
    private static final float WIDTH = 16F;
    private static final float HEIGHT = 24F;
    
    // Componentes
    private Handler handler;
    private BufferedImage[] llamaSprites;
    private Animacion llamaAnimacion;
    
    // Efectos visuales
    private int ticksAnimacion = 0;
    private boolean brillando = false;
    
    /**
     * Constructor de la llama estática
     */
    public LlamaEstatica(float x, float y, int scale, Handler handler) {
        super(x, y, ObjetosID.Fuego, WIDTH, HEIGHT, scale);
        this.handler = handler;
        
        // NO establecer velocidad (permanece en 0)
        setVelX(0);
        setVely(0);
        
        // Cargar sprites y animaciones
        cargarSprites();
        inicializarAnimaciones();
        
        System.out.println("[LLAMA ESTATICA] Creada en (" + x + ", " + y + ")");
    }
    
    /**
     * Carga los sprites de la llama desde Texturas
     */
    private void cargarSprites() {
        try {
            llamaSprites = Mariotest.getTextura().getLlamaSprites();
            
            if (llamaSprites == null || llamaSprites.length == 0) {
                System.err.println("[ERROR] No se pudieron cargar sprites de llama");
                llamaSprites = crearSpritesPlaceholder();
            } else {
                System.out.println("[LLAMA ESTATICA] Sprites cargados: " + llamaSprites.length);
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Excepción al cargar sprites: " + e.getMessage());
            llamaSprites = crearSpritesPlaceholder();
        }
    }
    
    /**
     * Crea sprites placeholder para la llama
     */
    private BufferedImage[] crearSpritesPlaceholder() {
        BufferedImage[] sprites = new BufferedImage[3];
        
        for (int i = 0; i < 3; i++) {
            sprites[i] = new BufferedImage(16, 24, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = sprites[i].createGraphics();
            
            // Colores del fuego
            Color colorBase = new Color(255, 69, 0);
            Color colorMedio = new Color(255, 140, 0);
            Color colorPunta = new Color(255, 215, 0);
            
            int alturaVar = (i % 2 == 0) ? 2 : -2;
            
            // Base (roja)
            g.setColor(colorBase);
            int[] xBase = {8, 4, 0, 4, 8, 12, 16, 12};
            int[] yBase = {24+alturaVar, 22, 20, 18, 16, 18, 20, 22};
            g.fillPolygon(xBase, yBase, 8);
            
            // Medio (naranja)
            g.setColor(colorMedio);
            int[] xMedio = {8, 5, 2, 5, 8, 11, 14, 11};
            int[] yMedio = {16+alturaVar, 14, 12, 10, 8, 10, 12, 14};
            g.fillPolygon(xMedio, yMedio, 8);
            
            // Punta (amarilla)
            g.setColor(colorPunta);
            int[] xPunta = {8, 6, 5, 6, 8, 10, 11, 10};
            int[] yPunta = {8+alturaVar, 6, 4, 2, 0+alturaVar, 2, 4, 6};
            g.fillPolygon(xPunta, yPunta, 8);
            
            // Centro brillante
            if (i == 1) {
                g.setColor(Color.WHITE);
                g.fillOval(6, 10, 4, 6);
            }
            
            g.dispose();
        }
        
        return sprites;
    }
    
    /**
     * Inicializa las animaciones de la llama
     */
    private void inicializarAnimaciones() {
        if (llamaSprites == null || llamaSprites.length < 2) {
            System.err.println("[ERROR] No hay suficientes sprites para animación");
            return;
        }
        
        // Animación de llama
        if (llamaSprites.length >= 3) {
            llamaAnimacion = new Animacion(3, 
                llamaSprites[0], 
                llamaSprites[1], 
                llamaSprites[2]
            );
        } else {
            llamaAnimacion = new Animacion(4, 
                llamaSprites[0], 
                llamaSprites[1]
            );
        }
        
        System.out.println("[LLAMA ESTATICA] Animación inicializada");
    }

    @Override
    public void tick() {
        ticksAnimacion++;
        
        // IMPORTANTE: NO aplicar física ni movimiento
        // La llama permanece completamente estática
        
        // Solo actualizar animación
        if (llamaAnimacion != null) {
            llamaAnimacion.runAnimacion();
        }
        /*
        // Efecto de brillo ocasional
        if (ticksAnimacion % 60 == 0) {
            brillando = Math.random() < 0.3;
        }*/
    }
    
    @Override
    public void aplicarGravedad() {
        // NO aplicar gravedad - permanece fija
    }

    @Override
    public void render(Graphics g) {
        if (llamaSprites != null && llamaSprites[0] != null && llamaAnimacion != null) {
            // Renderizar animación
            llamaAnimacion.drawAnimacion(g, 
                (int) getX(), (int) getY(), 
                (int) getWidth(), (int) getHeight()
            );
            /*
            // Efecto de brillo ocasional
            if (brillando) {
                g.setColor(new Color(255, 255, 0, 80));
                g.fillOval((int)getX() - 4, (int)getY() - 4, 
                          (int)getWidth() + 8, (int)getHeight() + 8);
            }
            */
        } else {
            // Placeholder visual
            g.setColor(new Color(255, 69, 0));
            int[] xPoints = {(int)(getX() + getWidth()/2), 
                            (int)getX(), 
                            (int)getX(), 
                            (int)(getX() + getWidth()/2),
                            (int)(getX() + getWidth()),
                            (int)(getX() + getWidth())};
            int[] yPoints = {(int)getY(), 
                            (int)(getY() + getHeight()/3), 
                            (int)(getY() + getHeight()),
                            (int)(getY() + 2*getHeight()/3),
                            (int)(getY() + getHeight()),
                            (int)(getY() + getHeight()/3)};
            g.fillPolygon(xPoints, yPoints, 6);
            
            // Centro amarillo
            g.setColor(Color.YELLOW);
            g.fillOval((int)(getX() + getWidth()/4), 
                      (int)(getY() + getHeight()/3), 
                      (int)(getWidth()/2), 
                      (int)(getHeight()/3));
        }
    }

    @Override
    public Rectangle getBounds() {
        // Hitbox completa (es una trampa fija)
        return new Rectangle(
            (int)(getX() + 4),
            (int)(getY() + 4),
            (int)(getWidth() - 8),
            (int)(getHeight() - 8)
        );
    }
    
    /**
     * Verifica colisión con el jugador
     */
    public boolean colisionaConJugador(Player player) {
        return getBounds().intersects(player.getBounds());
    }
    
    /**
     * Destruye la llama
     */
    public void destruir() {
        handler.removeObj(this);
        System.out.println("[LLAMA ESTATICA] Destruida");
    }
}