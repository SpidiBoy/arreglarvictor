package Objetos;

import GameGFX.Animacion;
import Objetos.Utilidad.Handler;
import Objetos.Utilidad.ObjetosID;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import mariotest.Mariotest;

/**
 * Clase Princesa con animación de movimiento hacia DK
 * 
 * @author LENOVO
 */
public class Princesa extends GameObjetos {
    
    private static final float WIDTH = 16;
    private static final float HEIGHT = 32;
    
    private Handler handler;
    private BufferedImage[] princesaSprites;
    
    private Animacion princesaEspera;
    private Animacion princesaPideAyuda;
    private Animacion animacionActual;
    
    private EstadoPrincesa estado;
    private boolean mirandoDerecha = true;
    
    private int ticksDesdeUltimaAnimacion = 0;
    private int ticksEntreAnimaciones = 160;
    private static final int DURACION_PEDIR_AYUDA = 60;
    
    private boolean rescatada = false;
    
    // 🆕 NUEVO: Sistema de movimiento
    private boolean moviendose = false;
    private float destinoX, destinoY;
    private static final float VELOCIDAD_MOVIMIENTO = 1.5f;
    
    public enum EstadoPrincesa {
        ESPERANDO,
        PIDIENDO_AYUDA,
        RESCATADA,
        EN_PELIGRO,
        SIENDO_LLEVADA // 🆕 NUEVO
    }
    
    public Princesa(float x, float y, int scale, Handler handler) {
        super(x, y, ObjetosID.Princesa, WIDTH, HEIGHT, scale);
        this.handler = handler;
        this.estado = EstadoPrincesa.ESPERANDO;
        
        cargarSprites();
        inicializarAnimaciones();
        
        System.out.println("[PRINCESA] Creada en (" + x + ", " + y + ")");
    }
    
    private void cargarSprites() {
        try {
            princesaSprites = Mariotest.getTextura().getPrincesaSprites();
            System.out.println("[PRINCESA] Sprites cargados: " + princesaSprites.length);
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudieron cargar sprites de Princesa: " + e.getMessage());
            princesaSprites = crearSpritesPlaceholder();
        }
    }
    
    private BufferedImage[] crearSpritesPlaceholder() {
        BufferedImage[] sprites = new BufferedImage[4];
        
        for (int i = 0; i < 4; i++) {
            sprites[i] = new BufferedImage(16, 24, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = sprites[i].createGraphics();
            
            g.setColor(new Color(255, 192, 203));
            g.fillRect(2, 8, 12, 14);
            
            g.setColor(new Color(255, 220, 177));
            g.fillOval(4, 2, 8, 8);
            
            g.setColor(new Color(255, 215, 0));
            g.fillRect(3, 1, 10, 4);
            
            g.setColor(new Color(255, 215, 0));
            g.fillRect(5, 0, 6, 2);
            
            g.setColor(new Color(255, 220, 177));
            if (i >= 2) {
                g.fillRect(1, 6, 3, 4);
                g.fillRect(12, 6, 3, 4);
            } else {
                g.fillRect(1, 10, 3, 4);
                g.fillRect(12, 10, 3, 4);
            }
            
            g.dispose();
        }
        
        return sprites;
    }
    
    private void inicializarAnimaciones() {
        if (princesaSprites == null || princesaSprites.length < 4) {
            System.err.println("[ERROR] No hay suficientes sprites para las animaciones de Princesa");
            return;
        }
        
        princesaEspera = new Animacion(15, princesaSprites[0], princesaSprites[1]);
        princesaPideAyuda = new Animacion(8, princesaSprites[2], princesaSprites[3]);
        
        animacionActual = princesaEspera;
        
        System.out.println("[PRINCESA] Animaciones inicializadas correctamente.");
    }

    @Override
    public void tick() {
        if (animacionActual != null) {
            animacionActual.runAnimacion();
        }
        
        // 🆕 NUEVO: Sistema de movimiento
        if (moviendose) {
            moverHaciaDestino();
        }
        
        switch (estado) {
            case ESPERANDO:
                tickEsperando();
                break;
                
            case PIDIENDO_AYUDA:
                tickPidiendoAyuda();
                break;
                
            case RESCATADA:
                tickRescatada();
                break;
                
            case EN_PELIGRO:
                tickEnPeligro();
                break;
                
            case SIENDO_LLEVADA: // 🆕 NUEVO
                tickSiendoLlevada();
                break;
        }
        
        verificarProximidadJugador();
        verificarProximidadDiegoKong();
    }
    
    private void tickEsperando() {
        animacionActual = princesaEspera;
        ticksDesdeUltimaAnimacion++;
        
        if (ticksDesdeUltimaAnimacion >= ticksEntreAnimaciones) {
            estado = EstadoPrincesa.PIDIENDO_AYUDA;
            ticksDesdeUltimaAnimacion = 0;
        }
    }
    
    private void tickPidiendoAyuda() {
        animacionActual = princesaPideAyuda;
        ticksDesdeUltimaAnimacion++;
        
        if (ticksDesdeUltimaAnimacion >= DURACION_PEDIR_AYUDA) {
            estado = EstadoPrincesa.ESPERANDO;
            ticksDesdeUltimaAnimacion = 0;
            
            ticksEntreAnimaciones = 60 + (int)(Math.random() * 180);
        }
    }
    
    private void tickRescatada() {
        animacionActual = princesaPideAyuda;
    }
    
    private void tickEnPeligro() {
        animacionActual = princesaPideAyuda;
        ticksDesdeUltimaAnimacion++;
        
        if (ticksDesdeUltimaAnimacion >= 120 && !diegoKongCerca()) {
            estado = EstadoPrincesa.ESPERANDO;
            ticksDesdeUltimaAnimacion = 0;
        }
    }
    
    // 🆕 NUEVO: Tick cuando está siendo llevada por DK
    private void tickSiendoLlevada() {
        animacionActual = princesaPideAyuda; // Brazos arriba pidiendo ayuda
        // El movimiento se maneja en moverHaciaDestino()
    }
    
    // 🆕 NUEVO: Sistema de movimiento hacia un destino
    private void moverHaciaDestino() {
        float deltaX = destinoX - getX();
        float deltaY = destinoY - getY();
        
        float distancia = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        // Si llegó al destino
        if (distancia < VELOCIDAD_MOVIMIENTO) {
            setX(destinoX);
            setY(destinoY);
            moviendose = false;
            System.out.println("[PRINCESA] Llegó al destino");
            return;
        }
        
        // Normalizar vector de dirección
        float dirX = deltaX / distancia;
        float dirY = deltaY / distancia;
        
        // Mover hacia el destino
        setX(getX() + dirX * VELOCIDAD_MOVIMIENTO);
        setY(getY() + dirY * VELOCIDAD_MOVIMIENTO);
        
        // Actualizar dirección visual
        mirandoDerecha = deltaX > 0;
    }
    
    // 🆕 NUEVO: Iniciar movimiento hacia una posición
    public void moverHacia(float x, float y) {
        this.destinoX = x;
        this.destinoY = y;
        this.moviendose = true;
        this.estado = EstadoPrincesa.SIENDO_LLEVADA;
        
        System.out.println("[PRINCESA] Iniciando movimiento hacia (" + x + ", " + y + ")");
    }
    
    // 🆕 NUEVO: Detener movimiento
    public void detenerMovimiento() {
        this.moviendose = false;
    }
    
    private void verificarProximidadJugador() {
        if (rescatada) return;
        
        Player player = handler.getPlayer();
        if (player == null) return;
        
        float distanciaX = Math.abs(player.getX() - getX());
        float distanciaY = Math.abs(player.getY() - getY());
        
        if (distanciaX < 30 && distanciaY < 30) {
            rescatar();
        }
        
        if (distanciaX < 100) {
            mirandoDerecha = player.getX() > getX();
        }
    }
    
    private void verificarProximidadDiegoKong() {
        if (rescatada) return;
        
        boolean dkCerca = false;
        
        for (GameObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.DiegoKong) {
                float distanciaX = Math.abs(obj.getX() - getX());
                float distanciaY = Math.abs(obj.getY() - getY());
                
                if (distanciaX < 150 && distanciaY < 150) {
                    dkCerca = true;
                    break;
                }
            }
        }
        
        if (dkCerca && estado == EstadoPrincesa.ESPERANDO) {
            estado = EstadoPrincesa.EN_PELIGRO;
            ticksDesdeUltimaAnimacion = 0;
        }
    }
    
    private boolean diegoKongCerca() {
        for (GameObjetos obj : handler.getGameObjs()) {
            if (obj.getId() == ObjetosID.DiegoKong) {
                float distanciaX = Math.abs(obj.getX() - getX());
                float distanciaY = Math.abs(obj.getY() - getY());
                
                if (distanciaX < 150 && distanciaY < 150) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void rescatar() {
        if (!rescatada) {
            rescatada = true;
            estado = EstadoPrincesa.RESCATADA;
            System.out.println("[PRINCESA] ¡Ha sido rescatada!");
        }
    }
    
    @Override
    public void aplicarGravedad() {
        // La princesa no tiene gravedad (está fija o se mueve suavemente)
    }

    @Override
    public void render(Graphics g) {
        if (animacionActual != null && princesaSprites != null) {
            if (mirandoDerecha) {
                animacionActual.drawAnimacion(g, 
                    (int) getX(), (int) getY(), 
                    (int) getWidth(), (int) getHeight()
                );
            } else {
                animacionActual.drawAnimacion(g, 
                    (int) (getX() + getWidth()), (int) getY(), 
                    (int) -getWidth(), (int) getHeight()
                );
            }
            
            if (estado == EstadoPrincesa.PIDIENDO_AYUDA || estado == EstadoPrincesa.EN_PELIGRO) {
                g.setColor(Color.WHITE);
                g.drawString("AYUDA!", (int) getX() + 45, (int) getY() - -20);
            }
            
        } else {
            g.setColor(new Color(255, 192, 203));
            g.fillRect((int) getX() + 2, (int) getY() + 8, 
                      (int) getWidth() - 4, (int) getHeight() - 10);
            
            g.setColor(new Color(255, 220, 177));
            g.fillOval((int) getX() + 4, (int) getY() + 2, 
                      (int) getWidth() - 8, 8);
            
            g.setColor(Color.YELLOW);
            g.fillRect((int) getX() + 5, (int) getY(), 6, 2);
            
            g.setColor(Color.WHITE);
            g.drawString("P", (int) getX() + 6, (int) getY() + 7);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(
            (int) getX(),
            (int) getY(),
            (int) getWidth(),
            (int) getHeight()
        );
    }
    
    public EstadoPrincesa getEstado() {
        return estado;
    }
    
    public boolean isRescatada() {
        return rescatada;
    }
    
    public void setRescatada(boolean rescatada) {
        this.rescatada = rescatada;
        if (rescatada) {
            estado = EstadoPrincesa.RESCATADA;
        }
    }
    
    public boolean isMirandoDerecha() {
        return mirandoDerecha;
    }
    
    public void setMirandoDerecha(boolean mirandoDerecha) {
        this.mirandoDerecha = mirandoDerecha;
    }
    
    // 🆕 NUEVO: Verificar si está en movimiento
    public boolean isMoviendose() {
        return moviendose;
    }
}